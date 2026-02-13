package dev.eduteam.eduquest.controllers.web;

import dev.eduteam.eduquest.models.questionari.Compilazione;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.services.accounts.StudenteService;
import dev.eduteam.eduquest.services.questionari.CompilazioneService;
import dev.eduteam.eduquest.services.questionari.QuestionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.eduteam.eduquest.models.accounts.Account;
import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.services.accounts.AccountService;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/studente")
public class StudenteWebController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private StudenteService studenteService;

    @Autowired
    private QuestionarioService questionarioService;

    @Autowired
    private CompilazioneService compilazioneService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/login";
        }
        
        if (user.isDocente()) {
            return "redirect:/docente/dashboard";
        }

        Studente studente = studenteService.getById(user.getAccountID());

        model.addAttribute("user", studente);

        // Statistiche reali: questionari completati
        ArrayList<Compilazione> compilazioniCompletate = compilazioneService.getCompilazioniCompletate(studente.getAccountID());
        model.addAttribute("questionariCompletati", compilazioniCompletate.size());

        // Questionari disponibili per lo studente
        List<Questionario> questionariDisponibili = questionarioService.getQuestionariDisponibliPerStudente(studente.getAccountID());
        model.addAttribute("questionariDisponibili", questionariDisponibili);

        return "studente/dashboard";
    }

    // ==================== PROFILO ====================

    @GetMapping("/profilo")
    public String profiloPage(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/login";
        }
        
        if (user.isDocente()) {
            return "redirect:/docente/profilo";
        }

        Studente studente = studenteService.getById(user.getAccountID());

        model.addAttribute("user", studente);
        return "studente/profilo";
    }

    @PostMapping("/profilo")
    public String aggiornaProfilo(@RequestParam String nome,
                                  @RequestParam String cognome,
                                  @RequestParam String email,
                                  @RequestParam String passwordAttuale,
                                  @RequestParam(required = false) String nuovaPassword,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Studente studente = (Studente) session.getAttribute("user");
        
        if (studente == null) {
            return "redirect:/login";
        }

        // Validazione: campi obbligatori non vuoti
        if (nome.isBlank() || cognome.isBlank() || email.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Nome, cognome e email sono obbligatori.");
            redirectAttributes.addFlashAttribute("nome", nome);
            redirectAttributes.addFlashAttribute("cognome", cognome);
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/studente/profilo";
        }

        try {
            // Aggiorna i campi comuni tramite AccountService
            Account accountAggiornato = accountService.aggiornaAccount(
                studente.getUserName(),
                passwordAttuale,
                nome,
                cognome,
                email,
                nuovaPassword != null && !nuovaPassword.isBlank() ? nuovaPassword : null
            );

            Studente studenteAggiornato = (Studente) accountAggiornato;

            // Aggiorna la sessione con i nuovi dati
            session.setAttribute("user", studenteAggiornato);
            
            redirectAttributes.addFlashAttribute("success", "Profilo aggiornato con successo!");
            return "redirect:/studente/profilo";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("nome", nome);
            redirectAttributes.addFlashAttribute("cognome", cognome);
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/studente/profilo";
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
            return "redirect:/studente/profilo";
        }
    }
}
