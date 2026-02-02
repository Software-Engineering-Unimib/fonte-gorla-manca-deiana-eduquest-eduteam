package dev.eduteam.eduquest.controllers.web;

import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.services.questionari.DomandaService;
import dev.eduteam.eduquest.services.questionari.RispostaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("api/docente/{docenteID}/questionari/{questionarioID}/domande/{domandaID}/risposte")
public class RispostaControllerWeb {

    @Autowired
    private RispostaService rispostaService;

    @Autowired
    private DomandaService domandaService;

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
            Model model,
            @PathVariable int questionarioID,
            @PathVariable int domandaID,
            @PathVariable int rispostaID) {

        Domanda domanda = domandaService.getDomandaByIdCompleta(questionarioID, domandaID);
        if (domanda != null) {
            Risposta risposta = rispostaService.getRispostaById(rispostaID);
            if (risposta != null) {
                model.addAttribute("domanda", domanda);
                model.addAttribute("risposta", risposta);
            }
        }
        return "singola-risposta";
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