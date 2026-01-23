package dev.eduteam.eduquest.controllers;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Questionario;
import dev.eduteam.eduquest.models.Risposta;
import dev.eduteam.eduquest.services.DomandaService;
import dev.eduteam.eduquest.services.QuestionarioService;
import dev.eduteam.eduquest.services.RispostaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("risposte")
public class RispostaController {

    @Autowired
    private RispostaService rispostaService;

    @Autowired
    private DomandaService domandaService;

    @Autowired
    private QuestionarioService questionarioService;

    @GetMapping("{questionarioID}/{domandaID}")
    public ArrayList<Risposta> getRisposte(
            @PathVariable int questionarioID,
            @PathVariable int domandaID) {

        Questionario questionario = questionarioService.getQuestionario(questionarioID);
        Domanda domanda = questionarioService.getDomanda(questionario, domandaID);

        return domanda.getElencoRisposte();
    }

    @GetMapping("{questionarioID}/{domandaID}/{rispostaID}")
    public Risposta getRisposta(
            @PathVariable int questionarioID,
            @PathVariable int domandaID,
            @PathVariable int rispostaID) {

        Questionario questionario = questionarioService.getQuestionario(questionarioID);
        Domanda domanda = questionarioService.getDomanda(questionario, domandaID);

        return domandaService.getRisposta(domanda, rispostaID);
    }

    @PostMapping("{questionarioID}/{domandaID}/{rispostaID}/testo")
    public Risposta setTestoRisposta(
            @PathVariable int questionarioID,
            @PathVariable int domandaID,
            @PathVariable int rispostaID,
            @RequestParam(name = "testo") String testo) {

        Risposta rispostaModificata = getRisposta(questionarioID, domandaID, rispostaID);

        rispostaService.modificaTesto(rispostaModificata, testo);

        return rispostaModificata;
    }
}
