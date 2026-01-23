package dev.eduteam.eduquest.controllers;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Questionario;
import dev.eduteam.eduquest.services.QuestionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("questionari")
public class QuestionarioController {

    @Autowired
    private QuestionarioService questionarioService;

    @GetMapping()
    public ArrayList<Questionario> getQuestionari() {
        return questionarioService.getQuestionari();
    }

    @GetMapping("get/{ID}")
    public Questionario getQuestionario(@PathVariable int ID) {

        return questionarioService.getQuestionario(ID);
    }

    @PostMapping("crea")
    public Questionario creaQuestionario() {

        Questionario questionarioCreato = questionarioService.creaQuestionario();

        questionarioService.getQuestionari().add(questionarioCreato);
        return questionarioCreato;
    }

    @PostMapping("modifica/{ID}/rinomina")
    public Questionario rinominaQuestionario(
            @PathVariable int ID,
            @RequestParam(name = "nome") String nome) {

        Questionario questionarioModificato = questionarioService.getQuestionario(ID);

        if (nome != null) {
            questionarioService.modificaNome(questionarioModificato, nome);
        }

        return questionarioModificato;
    }
    @PostMapping("modifica/{ID}/descrizione")
    public Questionario setDescrizoneQuestionario(
            @PathVariable int ID,
            @RequestParam(name = "descrizione") String descrizione) {

        Questionario questionarioModificato = questionarioService.getQuestionario(ID);

        if (descrizione != null) {
            questionarioService.modificaDescrizione(questionarioModificato, descrizione);
        }

        return questionarioModificato;
    }

    @PostMapping("/modifica/{ID}/aggiungi_domanda")
    public Questionario aggiungiDomanda(@PathVariable int ID) {
        Questionario questionarioModificato = questionarioService.getQuestionario(ID);

        questionarioService.aggiungiDomanda(questionarioModificato);

        return questionarioModificato;
    }

    @PostMapping("/modifica/{ID}/rimuovi_domanda/{domandaID}")
    public Questionario rimuoviDomanda(@PathVariable int ID, @PathVariable int domandaID) {
        Questionario questionarioModificato = questionarioService.getQuestionario(ID);
        Domanda domandaRimossa = questionarioService.getDomanda(questionarioModificato, domandaID);

        questionarioService.rimuoviDomanda(questionarioModificato, domandaRimossa);

        return questionarioModificato;
    }
}
