package dev.eduteam.eduquest.controllers.web;

import dev.eduteam.eduquest.models.accounts.Account;
import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.services.accounts.DocenteService;
import dev.eduteam.eduquest.services.questionari.DomandaService;
import dev.eduteam.eduquest.services.questionari.QuestionarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
@RequestMapping("docente/dashboard/{questionarioID}/")
public class DomandaControllerWeb {

    @Autowired
    private DomandaService domandaService;

    @Autowired
    private QuestionarioService questionarioService;

    @Autowired
    private DocenteService docenteService;

    @GetMapping()
    public String getDomande(
            Model model,
            @PathVariable int docenteID,
            @PathVariable int questionarioID) {
        Questionario questionario = questionarioService.getQuestionarioCompleto(questionarioID);
        if (questionario != null) {

            ArrayList<Domanda> domande = domandaService.getDomandeComplete(questionarioID);

            model.addAttribute("questionario", questionario);
            model.addAttribute("domande", domande);
        }
        return "lista-domande";
    }

    @GetMapping("{domandaID}")
    public String getDomanda(
            HttpSession session,
            Model model,
            @PathVariable int questionarioID,
            @PathVariable int domandaID) {

            Account user = (Account) session.getAttribute("user");

            if (user == null) {
                return "redirect:/login";
            }

            if (!user.isDocente()) {
                return "redirect:/studente/dashboard"; // TEMPORANEO, DEVE RIMANDARE ALLA PAGINA DOVE LO STUDENTE PUO' COMPILARE IL QUESTIONARIO
            }

            Docente docente = (Docente) user;
            Questionario questionario = questionarioService.getQuestionarioCompleto(questionarioID);
            if (questionario != null) {

                if (!docenteService.proprietarioQuestionario(docente, questionario)) {
                    return "redirect:/docente/redirect";
                }

                Domanda domanda = domandaService.getDomandaByIdCompleta(questionarioID, domandaID);

                if (domanda != null) {
                    ArrayList<Risposta> risposte = domanda.getElencoRisposte();
                    model.addAttribute("user", docente);
                    model.addAttribute("questionario", questionario);
                    model.addAttribute("domanda", domanda);
                    model.addAttribute("index", questionario.getElencoDomande().indexOf(domanda));
                    model.addAttribute("risposte", risposte);
                }
            }
        return "domanda/singola-domanda";
    }

    @PostMapping("aggiungi")
    public ResponseEntity<Questionario> aggiungiDomanda(
            @PathVariable int docenteID,
            @PathVariable int questionarioID,
            @RequestParam(name = "tipo") Domanda.Type tipo) {

        Questionario questionarioDaModificare = questionarioService.getQuestionarioCompleto(questionarioID);
        if (questionarioDaModificare == null) {
            return ResponseEntity.notFound().build();
        }

        Domanda domandaAggiunta = domandaService.aggiungiDomanda(questionarioID, tipo);
        if (domandaAggiunta != null) {
            return ResponseEntity.ok(questionarioDaModificare);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("rimuovi/{domandaID}")
    public ResponseEntity<Questionario> rimuoviDomanda(
            @PathVariable int questionarioID,
            @PathVariable int domandaID) {

        boolean rimosso = domandaService.rimuoviDomanda(questionarioID, domandaID);
        if (rimosso) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/modifica/{domandaID}")
    public String modificaQuestionario(@PathVariable int questionarioID,
                                       @PathVariable int domandaID,
                                       @RequestParam String testo,
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
        domandaService.modificaTesto(domandaID, testo);

        return "redirect:/docente/dashboard/" + questionarioID;
    }

    @GetMapping("/modifica/{domandaID}")
    public String modificaQuestionario(@PathVariable int questionarioID,
                                       @PathVariable int domandaID,
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
        if (questionario != null) {

            if (!docenteService.proprietarioQuestionario(docente, questionario)) {
                return "redirect:/docente/redirect";
            }

            model.addAttribute("questionario", questionario);
            model.addAttribute("domanda", domanda);
        }
        return "domanda/modifica-domanda";
    }

}