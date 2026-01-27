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
@RequestMapping("questionari/{questionarioID}/domande")
public class DomandaController {

    @Autowired
    private DomandaService domandaService;

    @Autowired
    private QuestionarioService questionarioService;

    @GetMapping()
    public ResponseEntity<ArrayList<Domanda>> getDomande(@PathVariable int questionarioID) {
        if (questionarioService.getQuestionarioCompleto(questionarioID) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(domandaService.getDomandeComplete(questionarioID));
    }

    @GetMapping("{domandaID}")
    public ResponseEntity<Domanda> getDomanda(
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

    /*
     * Non ho capito se volevi rimuovere la domanda o una risposta
     * 
     * @PostMapping("{questionarioID}/{domandaID}/rimuovi_risposta/{rispostaID}")
     * public Domanda rimuoviDomanda(
     * 
     * @PathVariable int questionarioID,
     * 
     * @PathVariable int domandaID,
     * 
     * @PathVariable int rispostaID) {
     * Domanda domandaModificata = getDomanda(questionarioID, domandaID);
     * Risposta rispostaRimossa = domandaService.getRisposta(domandaModificata,
     * rispostaID);
     * 
     * domandaService.rimuoviRisposta(domandaModificata, rispostaRimossa);
     * 
     * return domandaModificata;
     * }
     */

    @PostMapping("aggiungi")
    public ResponseEntity<Questionario> aggiungiDomanda(
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
