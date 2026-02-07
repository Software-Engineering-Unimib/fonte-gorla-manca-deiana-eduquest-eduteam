package dev.eduteam.eduquest.controllers.web;

import dev.eduteam.eduquest.models.accounts.Account;
import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.services.accounts.DocenteService;
import dev.eduteam.eduquest.services.questionari.DomandaService;
import dev.eduteam.eduquest.services.questionari.QuestionarioService;
import dev.eduteam.eduquest.services.questionari.RispostaService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
@RequestMapping("docente/dashboard/{questionarioID}/{domandaID}/")
public class RispostaControllerWeb {

    @Autowired
    private DocenteService docenteService;

    @Autowired
    private QuestionarioService questionarioService;

    @Autowired
    private DomandaService domandaService;

    @Autowired
    private RispostaService rispostaService;

    @GetMapping()
    public String getRisposte(
            Model model,
            @PathVariable int questionarioID,
            @PathVariable int domandaID) {

        Domanda domanda = domandaService.getDomandaByIdCompleta(questionarioID, domandaID);
        if (domanda != null) {
            ArrayList<Risposta> risposte = rispostaService.getRisposteByDomanda(domandaID);

            model.addAttribute("domanda", domanda);
            model.addAttribute("risposte", risposte);
        }
        return "lista-risposte";
    }

    @GetMapping("{rispostaID}")
    public String getRisposta(
            HttpSession session,
            Model model,
            @PathVariable int questionarioID,
            @PathVariable int domandaID,
            @PathVariable int rispostaID) {

        Account user = (Account) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (!user.isDocente()) {
            return "redirect:/studente/dashboard"; // TEMPORANEO, DEVE RIMANDARE ALLA PAGINA DOVE LO STUDENTE PUO' COMPILARE IL QUESTIONARIO
        }

        Docente docente = (Docente) user;

        Questionario questionario = questionarioService.getQuestionarioCompleto(questionarioID);
        Domanda domanda = domandaService.getDomandaByIdCompleta(questionarioID, domandaID);
        if (domanda != null) {
            Risposta risposta = rispostaService.getRispostaById(rispostaID);
            if (risposta != null) {
                model.addAttribute("user", docente);
                model.addAttribute("questionario", questionario);
                model.addAttribute("domanda", domanda);
                model.addAttribute("risposta", risposta);
            }
        }
        return "singola-risposta";
    }

    @PostMapping("aggiungi")
    public String aggiungiRisposta(HttpSession session,
            @PathVariable int questionarioID,
            @PathVariable int domandaID) {
        Docente docente = (Docente) session.getAttribute("user");

        if (docente == null) {
            return "redirect:/login";
        }

        rispostaService.aggiungiRisposta(domandaID);
        return "redirect:/docente/dashboard/" + questionarioID + "/" + domandaID;
    }

    @PostMapping("rimuovi/{rispostaID}")
    public String rimuoviRisposta(HttpSession session,
            @PathVariable int questionarioID,
            @PathVariable int domandaID,
            @PathVariable int rispostaID) {

        Docente docente = (Docente) session.getAttribute("user");

        if (docente == null) {
            return "redirect:/login";
        }

        rispostaService.rimuoviRisposta(rispostaID, domandaID);
        return "redirect:/docente/dashboard/" + questionarioID + "/" + domandaID;
    }

    @GetMapping("/modifica/{rispostaID}")
    public String modificaQuestionario(@PathVariable int questionarioID,
                                       @PathVariable int domandaID,
                                       @PathVariable int rispostaID,
                                       HttpSession session,
                                       Model model) {

        Account user = (Account) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (!user.isDocente()) {
            return "redirect:/studente/dashboard"; // TEMPORANEO, DEVE RIMANDARE ALLA PAGINA DOVE LO STUDENTE PUO' COMPILARE IL QUESTIONARIO
        }

        Docente docente = (Docente) user;
        Questionario questionario = questionarioService.getQuestionarioCompleto(questionarioID);
        Domanda domanda = domandaService.getDomandaByIdCompleta(questionarioID, domandaID);
        Risposta risposta = rispostaService.getRispostaById(rispostaID);

        if (questionario != null) {

            if (!docenteService.proprietarioQuestionario(docente, questionario)) {
                return "redirect:/docente/redirect";
            }

            model.addAttribute("questionario", questionario);
            model.addAttribute("domanda", domanda);
            model.addAttribute("risposta", risposta);
        }
        return "risposta/modifica-risposta";
    }

    @PostMapping("/modifica/{rispostaID}")
    public String modificaQuestionario(@PathVariable int questionarioID,
                                       @PathVariable int domandaID,
                                       @PathVariable int rispostaID,
                                       @RequestParam String testo,
                                       @RequestParam(name="corretta", defaultValue="false") boolean corretta,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        Docente docente = (Docente) session.getAttribute("user");

        if (docente == null) {
            return "redirect:/login";
        }

        // Validazione: campi obbligatori non vuoti
        if (domandaID == 0 || testo.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Il testo è obbligatorio.");
            redirectAttributes.addFlashAttribute("nome", testo);
            return "redirect:/docente/profilo";
        }
        Risposta risposta = rispostaService.getRispostaById(rispostaID);

        rispostaService.modificaRisposta(risposta, testo, corretta);

        return "redirect:/docente/dashboard/" + questionarioID + "/" + domandaID;
    }
}