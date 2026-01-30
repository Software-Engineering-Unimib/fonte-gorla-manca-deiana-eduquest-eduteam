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

        Domanda domanda = domandaService.getDomandaByIdCompleta(questionarioID, domandaID);
        if (domanda == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(domanda.getElencoRisposte());
    }

    @GetMapping("{rispostaID}")
    public ResponseEntity<Risposta> getRisposta(
            @PathVariable int questionarioID,
            @PathVariable int domandaID,
            @PathVariable int rispostaID) {

        Risposta risposta = rispostaService.getRispostaById(rispostaID);
        if (risposta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(risposta);
    }

    @PostMapping("aggiungi")
    public ResponseEntity<Risposta> aggiungiRisposta(
            @PathVariable int questionarioID,
            @PathVariable int domandaID) {

        Domanda domanda = domandaService.getDomandaByIdCompleta(questionarioID, domandaID);
        if (domanda == null) {
            return ResponseEntity.notFound().build();
        }

        Risposta ripostaAggiunta = rispostaService.aggiungiRisposta(domandaID);
        if (ripostaAggiunta != null) {
            return ResponseEntity.ok(ripostaAggiunta);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("rimuovi/{rispostaID}")
    public ResponseEntity<Void> rimuoviRisposta(
            @PathVariable int domandaID,
            @PathVariable int rispostaID) {

        boolean rimosso = rispostaService.rimuoviRisposta(domandaID, rispostaID);
        if (rimosso) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("modifica/{rispostaID}/testo")
    public ResponseEntity<Risposta> setTestoRisposta(
            @PathVariable int domandaID,
            @PathVariable int rispostaID,
            @RequestParam(name = "testo") String testo) {

        // Controllo validità del testo
        if (testo == null || testo.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Risposta rispostaDaModificare = rispostaService.getRispostaById(rispostaID);
        if (rispostaDaModificare == null)
            return ResponseEntity.notFound().build();

        if (rispostaService.modificaTesto(rispostaDaModificare, testo)) {
            return ResponseEntity.ok(rispostaDaModificare);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }
}
