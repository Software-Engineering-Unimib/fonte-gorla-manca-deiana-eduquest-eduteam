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
@RequestMapping("docente/dashboard/{questionarioID}/")
public class DomandaControllerWeb {

    @Autowired
    private RispostaService rispostaService;

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

            Docente docente = docenteService.getByID(user.getAccountID());

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

    @PostMapping("aggiungiSingolaRisposta")
    public String creaDomandaSingolaRisposta(HttpSession session,
                                   @PathVariable int questionarioID) {
        Docente docente = (Docente) session.getAttribute("user");

        if (docente == null) {
            return "redirect:/login";
        }
        domandaService.aggiungiDomanda(questionarioID, Domanda.Type.DOMANDA_MULTIPLA);
        return "redirect:/docente/dashboard/" + questionarioID;
    }
    @PostMapping("aggiungiMultipleRisposte")
    public String creaDomandaMultipleRisposte(HttpSession session,
                                   @PathVariable int questionarioID) {
        Docente docente = (Docente) session.getAttribute("user");

        if (docente == null) {
            return "redirect:/login";
        }
        domandaService.aggiungiDomanda(questionarioID, Domanda.Type.DOMANDA_MULTIPLE_RISPOSTE);
        return "redirect:/docente/dashboard/" + questionarioID;
    }
    @PostMapping("aggiungiVeroFalso")
    public String creaDomandaVeroFalso(HttpSession session,
                                   @PathVariable int questionarioID) {
        Docente docente = (Docente) session.getAttribute("user");

        if (docente == null) {
            return "redirect:/login";
        }
        Domanda domanda = domandaService.aggiungiDomanda(questionarioID, Domanda.Type.DOMANDA_VERO_FALSO);

        domanda.setElencoRisposte(rispostaService.aggiungiRisposteVeroFalso(domanda.getID()));

        return "redirect:/docente/dashboard/" + questionarioID;
    }


    @PostMapping("rimuovi/{domandaID}")
    public String rimuoviDomanda(HttpSession session,
            @PathVariable int questionarioID,
            @PathVariable int domandaID) {

        Docente docente = (Docente) session.getAttribute("user");

        if (docente == null) {
            return "redirect:/login";
        }
        domandaService.rimuoviDomanda(questionarioID, domandaID);
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

        Docente docente = docenteService.getByID(user.getAccountID());

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

    @PostMapping("/modifica/{domandaID}")
    public String modificaQuestionario(@PathVariable int questionarioID,
                                       @PathVariable int domandaID,
                                       @RequestParam String testo,
                                       @RequestParam int punteggio,
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
        domandaService.modificaDomanda(domandaID, testo, punteggio);

        return "redirect:/docente/dashboard/" + questionarioID;
    }
}