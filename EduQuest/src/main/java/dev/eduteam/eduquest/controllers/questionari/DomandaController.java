package dev.eduteam.eduquest.controllers.questionari;

import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.services.questionari.DomandaService;
import dev.eduteam.eduquest.services.questionari.QuestionarioService;
import dev.eduteam.eduquest.services.questionari.RispostaService;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("api/docente/{docenteID}/questionari/{questionarioID}/domande")
public class DomandaController {

    @Autowired
    private DomandaService domandaService;

    @Autowired
    private QuestionarioService questionarioService;

    @GetMapping()
    public String getDomande(
            Model model,
            @PathVariable int docenteID,
            @PathVariable int questionarioID) {
        Questionario questionario = questionarioService.getQuestionarioCompleto(docenteID, questionarioID);
        if (questionario != null) {

            ArrayList<Domanda> domande = domandaService.getDomandeComplete(questionarioID);

            model.addAttribute("questionario", questionario);
            model.addAttribute("domande", domande);
        }
        return "lista-domande";
    }

    @GetMapping("{domandaID}")
    public String getDomanda(
            Model model,
            @PathVariable int docenteID,
            @PathVariable int questionarioID,
            @PathVariable int domandaID) {

        Questionario questionario = questionarioService.getQuestionarioCompleto(docenteID, questionarioID);

        if (questionario != null) {
            Domanda domanda = domandaService.getDomandaByIdCompleta(questionarioID, domandaID);
            if (domanda != null) {
                model.addAttribute("questionario", questionario);
                model.addAttribute("domanda", domanda);
            }
        }

        return "singola-domanda";
    }

    @PostMapping("aggiungi")
    public ResponseEntity<Questionario> aggiungiDomanda(
            @PathVariable int docenteID,
            @PathVariable int questionarioID,
            @RequestParam(name = "tipo") Domanda.Type tipo) {

        Questionario questionarioDaModificare = questionarioService.getQuestionarioCompleto(docenteID, questionarioID);
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

    @PostMapping("modifica/{domandaID}/testo")
    public ResponseEntity<Domanda> setTestoDomanda(
            @PathVariable int domandaID,
            @RequestParam(name = "testo") String testo) {

        boolean successo = domandaService.modificaTesto(domandaID, testo);

        if (successo) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("modifica/{domandaID}/risposta_corretta/{rispostaID}")
    public ResponseEntity<Domanda> setRispostaCorretta(
            @PathVariable int domandaID,
            @PathVariable int rispostaID) {

        boolean successo = domandaService.setRispostaCorretta(domandaID, rispostaID);

        if (successo) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
