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
    public String dashboard(HttpSession session, Model model,
                            @RequestParam(required = false) String cerca) {
        Account user = (Account) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/login";
        }
        
        if (!user.isDocente()) {
            return "redirect:/studente/dashboard";
        }

        Docente docente = docenteService.getByID(user.getAccountID());

        model.addAttribute("user", docente);
        
        // Carica i questionari del docente (con ricerca opzionale)
        try {
            List<Questionario> questionari = questionarioService.getQuestionariByDocente(docente.getAccountID());

            if (cerca != null && !cerca.trim().isEmpty()) {
                String keyword = cerca.trim().toLowerCase();
                questionari = questionari.stream()
                        .filter(q -> (q.getNome() != null && q.getNome().toLowerCase().contains(keyword))
                                || (q.getDescrizione() != null && q.getDescrizione().toLowerCase().contains(keyword))
                                || (q.getMateria() != null && q.getMateria().toLowerCase().contains(keyword)))
                        .collect(java.util.stream.Collectors.toList());
            }

            model.addAttribute("questionari", questionari);
        } catch (Exception e) {
            // Se non ci sono questionari, la lista sarà null
            model.addAttribute("questionari", null);
        }
        model.addAttribute("cerca", cerca);

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

        Docente docente = docenteService.getByID(user.getAccountID());

        model.addAttribute("user", docente);
        return "docente/profilo";
    }

    @PostMapping("/profilo")
    public String aggiornaProfilo(@RequestParam String nome,
                                  @RequestParam String cognome,
                                  @RequestParam String email,
                                  @RequestParam String passwordAttuale,
                                  @RequestParam(required = false) String nuovaPassword,
                                  @RequestParam String insegnamento,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Account user = (Account) session.getAttribute("user");
        Docente docente = docenteService.getByID(user.getAccountID());
        
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

            Docente docenteAggiornato = docenteService.getByID(accountAggiornato.getAccountID());

            // Aggiorna il campo specifico del docente: insegnamento
            if (insegnamento != null && !insegnamento.isBlank()) {
                docenteAggiornato.setInsegnamento(insegnamento);
                docenteService.aggiornaDocente(docenteAggiornato);
            }


            // Aggiorna la sessione con i nuovi dati
            session.setAttribute("user", accountAggiornato);
            
            redirectAttributes.addFlashAttribute("success", "Profilo aggiornato con successo!");
            return "redirect:/docente/dashboard";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("nome", nome);
            redirectAttributes.addFlashAttribute("cognome", cognome);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("insegnamento", insegnamento);
            return "redirect:/docente/profilo";
        }
    }

    // ==================== ELIMINA ACCOUNT ====================

    @PostMapping("/elimina-account")
    public String eliminaAccount(@RequestParam String passwordConferma,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Account user = (Account) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        try {
            accountService.eliminaAccount(user.getUserName(), passwordConferma);
            session.invalidate();
            return "redirect:/login?accountEliminato";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/docente/profilo";
        }
    }
}
