package dev.eduteam.eduquest.controllers.questionari;

import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Esercitazione;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.services.questionari.DomandaService;
import dev.eduteam.eduquest.services.questionari.EsercitazioneService;
import dev.eduteam.eduquest.services.questionari.QuestionarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("rest/docente/{docenteID}/questionari/{questionarioID}/domande")
public class DomandaController {

    @Autowired
    private DomandaService domandaService;

    @Autowired
    private QuestionarioService questionarioService;

    @Autowired
    private EsercitazioneService esercitazioneService;

    @GetMapping()
    public ResponseEntity<ArrayList<Domanda>> getDomande(@PathVariable int questionarioID) {
        if (questionarioService.getQuestionarioCompleto(questionarioID) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(domandaService.getDomandeComplete(questionarioID));
    }

    @GetMapping("{domandaID}")
    public ResponseEntity<Domanda> getDomanda(
            @PathVariable int docenteID,
            @PathVariable int questionarioID,
            @PathVariable int domandaID) {

        if (questionarioService.getQuestionarioCompleto(questionarioID) == null) {
            return ResponseEntity.notFound().build();
        }

        Domanda domanda = domandaService.getDomandaByIdCompleta(questionarioID, domandaID);
        if (domanda == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(domanda);
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

    @PostMapping("aggiungiDomandaFeedback")
    public ResponseEntity<Questionario> aggiungiDomandaFeedback(
            @PathVariable int docenteID,
            @PathVariable int questionarioID,
            @RequestParam(name = "tipo") Domanda.Type tipo,
            @RequestParam(name = "feedback") String feedbackTesto) {

        Questionario questionarioDaModificare = questionarioService.getQuestionarioCompleto(questionarioID);
        if (questionarioDaModificare == null) {
            return ResponseEntity.notFound().build();
        }

        if (!(questionarioDaModificare instanceof Esercitazione)) {
            return ResponseEntity.badRequest().build();
        }

        Domanda domandaAggiunta = esercitazioneService.aggiungiDomandaConFeedback(questionarioID, tipo, feedbackTesto);

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

    @PostMapping("modifica/{domandaID}/punteggio")
    public ResponseEntity<Domanda> setPunteggioDomanda(
            @PathVariable int domandaID,
            @RequestParam int punteggio) {
        boolean successo = domandaService.modificaPunteggio(domandaID, punteggio);

        if (successo) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}