package dev.eduteam.eduquest.controllers;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Questionario;
import dev.eduteam.eduquest.models.Risposta;
import dev.eduteam.eduquest.services.DomandaService;
import dev.eduteam.eduquest.services.QuestionarioService;
import dev.eduteam.eduquest.services.RispostaService;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("questionari/{questionarioID}/domande/{domandaID}/risposte")
public class RispostaController {

    @Autowired
    private RispostaService rispostaService;

    @Autowired
    private DomandaService domandaService;

    @GetMapping()
    public ResponseEntity<ArrayList<Risposta>> getRisposte(
            @PathVariable int questionarioID,
            @PathVariable int domandaID) {

        Domanda domanda = domandaService.getDomandaById(questionarioID, domandaID);
        // Non c'è bisogno di controllare, se la domanda/questionario non esiste torna
        // una lista vuota
        return ResponseEntity.ok(domanda.getElencoRisposte());
    }

    @GetMapping("{rispostaID}")
    public ResponseEntity<Risposta> getRisposta(
            @PathVariable int questionarioID,
            @PathVariable int domandaID,
            @PathVariable int rispostaID) {

        Domanda domanda = domandaService.getDomandaById(questionarioID, domandaID);
        if (domanda == null) {
            return ResponseEntity.notFound().build();
        }
        Risposta risposta = rispostaService.getRispostaById(domandaID, rispostaID);
        if (risposta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(risposta);
    }

    @PostMapping("aggiungi")
    public ResponseEntity<Domanda> aggiungiRisposta(
            @PathVariable int questionarioID,
            @PathVariable int domandaID) {

        Domanda domanda = domandaService.getDomandaById(questionarioID, domandaID);
        if (domanda == null) {
            return ResponseEntity.notFound().build();
        }

        Risposta ripostaAggiunta = rispostaService.aggiungiRisposta(questionarioID, domandaID);
        if (ripostaAggiunta != null) {
            return ResponseEntity.ok(domanda);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("rimuovi/{rispostaID}")
    public ResponseEntity<Domanda> rimuoviRisposta(
            @PathVariable int questionarioID,
            @PathVariable int domandaID,
            @PathVariable int rispostaID) {
        Domanda domanda = domandaService.getDomandaById(questionarioID, domandaID);
        if (domanda == null) {
            return ResponseEntity.notFound().build();
        }

        boolean rimosso = rispostaService.rimuoviRisposta(questionarioID, domandaID, rispostaID);
        if (rimosso) {
            return ResponseEntity.ok(domanda);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("modifica/{rispostaID}/testo")
    public ResponseEntity<Risposta> setTestoRisposta(
            @PathVariable int questionarioID,
            @PathVariable int domandaID,
            @PathVariable int rispostaID,
            @RequestParam(name = "testo") String testo) {

        Risposta rispostaDaModificare = rispostaService.getRispostaById(domandaID, rispostaID);
        if (rispostaDaModificare != null) {
            rispostaService.modificaTesto(rispostaDaModificare, testo);
            return ResponseEntity.ok(rispostaDaModificare);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }
}
