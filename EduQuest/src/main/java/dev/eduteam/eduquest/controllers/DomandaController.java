package dev.eduteam.eduquest.controllers;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Questionario;
import dev.eduteam.eduquest.models.Risposta;
import dev.eduteam.eduquest.services.DomandaService;
import dev.eduteam.eduquest.services.QuestionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("domande")
public class DomandaController {

    @Autowired
    private DomandaService domandaService;

    @Autowired
    private QuestionarioService questionarioService;

    @GetMapping("{questionarioID}")
    public ArrayList<Domanda> getDomande(@PathVariable int questionarioID) {

        return questionarioService.getQuestionario(questionarioID).getElencoDomande();
    }

    @GetMapping("{questionarioID}/{domandaID}")
    public Domanda getDomanda(
            @PathVariable int questionarioID,
            @PathVariable int domandaID) {

        Questionario questionario = questionarioService.getQuestionario(questionarioID);
        return questionarioService.getDomanda(questionario, domandaID);
    }

    @PostMapping("{questionarioID}/{domandaID}/testo")
    public Domanda setTestoDomanda(
            @PathVariable int questionarioID,
            @PathVariable int domandaID,
            @RequestParam(name = "testo") String testo) {

        Domanda domandaModificata = getDomanda(questionarioID, domandaID);

        domandaService.modificaTesto(domandaModificata, testo);

        return domandaModificata;
    }

    @PostMapping("{questionarioID}/{domandaID}/aggiungi_risposta")
    public Domanda aggiungiRisposta(
            @PathVariable int questionarioID,
            @PathVariable int domandaID) {

        Domanda domandaModificata = getDomanda(questionarioID, domandaID);

        domandaService.aggiungiRisposta(domandaModificata);

        return domandaModificata;
    }

    @PostMapping("{questionarioID}/{domandaID}/rimuovi_risposta/{rispostaID}")
    public Domanda rimuoviDomanda(
            @PathVariable int questionarioID,
            @PathVariable int domandaID,
            @PathVariable int rispostaID) {
        Domanda domandaModificata = getDomanda(questionarioID, domandaID);
        Risposta rispostaRimossa = domandaService.getRisposta(domandaModificata, rispostaID);

        domandaService.rimuoviRisposta(domandaModificata, rispostaRimossa);

        return domandaModificata;
    }

    @PostMapping("{questionarioID}/{domandaID}/risposta_corretta/{rispostaID}")
    public Domanda setRispostaCorretta(
            @PathVariable int questionarioID,
            @PathVariable int domandaID,
            @PathVariable int rispostaID) {

        Domanda domandaModificata = getDomanda(questionarioID, domandaID);
        Risposta rispostaCorretta = domandaService.getRisposta(domandaModificata, rispostaID);

        domandaService.setRispostaCorretta(domandaModificata, rispostaCorretta);

        return domandaModificata;
    }
}
