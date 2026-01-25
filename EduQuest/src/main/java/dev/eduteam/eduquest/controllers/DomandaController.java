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

    @Autowired
    private RispostaService rispostaService;

    /*
     * Non so quanoto serva tornare TUTTE le domande di tutti i questionari
     * 
     * @GetMapping()
     * public ArrayList<Domanda> getDomande() {
     * return domandaService.getDomande();
     * }
     */

    @GetMapping()
    public ResponseEntity<ArrayList<Domanda>> getDomande(@PathVariable int questionarioID) {
        return ResponseEntity.ok(domandaService.getDomande(questionarioID));
    }

    @GetMapping("{domandaID}")
    public ResponseEntity<Domanda> getDomanda(
            @PathVariable int questionarioID,
            @PathVariable int domandaID) {

        if (questionarioService.getQuestionario(questionarioID) == null) {
            return ResponseEntity.notFound().build();
        }

        Domanda domanda = domandaService.getDomandaById(questionarioID, domandaID);
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
    public ResponseEntity<Questionario> aggiungiDomanda(@PathVariable int questionarioID) {
        Questionario questionarioDaModificare = questionarioService.getQuestionario(questionarioID);
        if (questionarioDaModificare == null) {
            return ResponseEntity.notFound().build();
        }

        Domanda domandaAggiunta = domandaService.aggiungiDomanda(questionarioID);
        if (domandaAggiunta != null) {
            return ResponseEntity.ok(questionarioDaModificare);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("rimuovi/{domandaID}")
    public ResponseEntity<Questionario> rimuoviDomanda(@PathVariable int questionarioID, @PathVariable int domandaID) {
        Questionario questionarioDaModificare = questionarioService.getQuestionario(questionarioID);
        if (questionarioDaModificare == null) {
            return ResponseEntity.notFound().build();
        }

        boolean rimosso = domandaService.rimuoviDomanda(questionarioID, domandaID);
        if (rimosso) {
            return ResponseEntity.ok(questionarioDaModificare);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("modifica/{domandaID}/testo")
    public ResponseEntity<Domanda> setTestoDomanda(
            @PathVariable int questionarioID,
            @PathVariable int domandaID,
            @RequestParam(name = "testo") String testo) {

        Domanda domandaModificata = domandaService.getDomandaById(questionarioID, domandaID);
        if (domandaModificata != null) {
            domandaService.modificaTesto(domandaModificata, testo);
            return ResponseEntity.ok(domandaModificata);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("modifica/{domandaID}/risposta_corretta/{rispostaID}")
    public ResponseEntity<Domanda> setRispostaCorretta(
            @PathVariable int questionarioID,
            @PathVariable int domandaID,
            @PathVariable int rispostaID) {

        Domanda domandaDaModificare = domandaService.getDomandaById(questionarioID, domandaID);
        Risposta rispostaCorretta = rispostaService.getRispostaById(domandaID, rispostaID);

        if (domandaDaModificare != null && rispostaCorretta != null) {
            domandaService.modificaRispostaCorretta(domandaDaModificare, rispostaCorretta);
            return ResponseEntity.ok(domandaDaModificare);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

}
