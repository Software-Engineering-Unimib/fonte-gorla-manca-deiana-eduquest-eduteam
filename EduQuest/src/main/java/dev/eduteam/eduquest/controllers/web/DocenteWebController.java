package dev.eduteam.eduquest.controllers.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.eduteam.eduquest.models.accounts.Account;
import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.services.accounts.AccountService;
import dev.eduteam.eduquest.services.accounts.DocenteService;
import dev.eduteam.eduquest.services.questionari.QuestionarioService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/docente")
public class DocenteWebController {

    @Autowired
    private QuestionarioService questionarioService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DocenteService docenteService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/login";
        }
        
        if (!user.isDocente()) {
            return "redirect:/studente/dashboard";
        }

        Docente docente = docenteService.getByID(user.getAccountID());

        model.addAttribute("user", docente);
        
        // Carica i questionari del docente
        try {
            List<Questionario> questionari = questionarioService.getQuestionariByDocente(docente.getAccountID());

            model.addAttribute("questionari", questionari);
        } catch (Exception e) {
            // Se non ci sono questionari, la lista sarà null
            model.addAttribute("questionari", null);
        }

        return "docente/dashboard";
    }

    @GetMapping("/redirect")
    public String redirectPage() {
        return "questionario-redirect";
    }

    // ==================== PROFILO ====================

    @GetMapping("/profilo")
    public String profiloPage(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/login";
        }
        
        if (!user.isDocente()) {
            return "redirect:/studente/profilo";
        }

        Docente docente = (Docente) user;
        model.addAttribute("user", docente);
        return "docente/profilo";
    }

    @PostMapping("/profilo")
    public String aggiornaProfilo(@RequestParam String nome,
                                  @RequestParam String cognome,
                                  @RequestParam String email,
                                  @RequestParam String passwordAttuale,
                                  @RequestParam(required = false) String nuovaPassword,
                                  @RequestParam(required = false) String insegnamento,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Docente docente = (Docente) session.getAttribute("user");
        
        if (docente == null) {
            return "redirect:/login";
        }

        // Validazione: campi obbligatori non vuoti
        if (nome.isBlank() || cognome.isBlank() || email.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Nome, cognome e email sono obbligatori.");
            redirectAttributes.addFlashAttribute("nome", nome);
            redirectAttributes.addFlashAttribute("cognome", cognome);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("insegnamento", insegnamento);
            return "redirect:/docente/profilo";
        }

        try {
            // Aggiorna i campi comuni tramite AccountService
            Account accountAggiornato = accountService.aggiornaAccount(
                docente.getUserName(),
                passwordAttuale,
                nome,
                cognome,
                email,
                nuovaPassword != null && !nuovaPassword.isBlank() ? nuovaPassword : null
            );

            Docente docenteAggiornato = (Docente) accountAggiornato;

            // Aggiorna il campo specifico del docente: insegnamento
            if (insegnamento != null && !insegnamento.isBlank()) {
                docenteAggiornato.setInsegnamento(insegnamento);
                docenteService.aggiornaDocente(docenteAggiornato);
            }

            // Aggiorna la sessione con i nuovi dati
            session.setAttribute("user", docenteAggiornato);
            
            redirectAttributes.addFlashAttribute("success", "Profilo aggiornato con successo!");
            return "redirect:/docente/profilo";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("nome", nome);
            redirectAttributes.addFlashAttribute("cognome", cognome);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("insegnamento", insegnamento);
            return "redirect:/docente/profilo";
        }
    }
}
