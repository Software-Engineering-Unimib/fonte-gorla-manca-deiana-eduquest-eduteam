package dev.eduteam.eduquest.controllers;

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

    @GetMapping("get")
    public Questionario getQuestionario(@RequestParam(name="id") int ID) {

        return questionarioService.getQuestionari().stream().filter(q -> q.getID() == ID).findFirst().orElse(null);

    }

    @PostMapping("crea")
    public Questionario creaQuestionario(@RequestBody InfoQuestionario questionario) {

        Questionario questionarioCreato = questionarioService.creaQuestionario(questionario.getDescrizione(), questionario.getNumeroDomande());

        questionarioService.getQuestionari().add(questionarioCreato);
        return questionarioCreato;
    }
}

class InfoQuestionario {
    private String descrizione;
    private int numeroDomande;

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    public void setNumeroDomande(int numeroDomande) {
        this.numeroDomande = numeroDomande;
    }
    public int getNumeroDomande() {
        return numeroDomande;
    }
    public String getDescrizione() {
        return descrizione;
    }
}
