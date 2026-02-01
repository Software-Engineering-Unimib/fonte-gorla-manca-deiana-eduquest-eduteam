package dev.eduteam.eduquest.controllers.web;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.questionari.Questionario.Difficulty;
import dev.eduteam.eduquest.services.accounts.DocenteService;
import dev.eduteam.eduquest.services.questionari.QuestionarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("web/docente/{docenteID}/questionari")
public class QuestionarioControllerWeb {

    @Autowired
    private QuestionarioService questionarioService;

    @Autowired
    private DocenteService docenteService;

    @GetMapping()
    public String getQuestionariByDocente(
            Model model,
            @PathVariable int docenteID) {

        ArrayList<Questionario> questionari = questionarioService.getQuestionariByDocente(docenteID);
        Docente docente = docenteService.getByID(docenteID);

        model.addAttribute("docente", docente);
        model.addAttribute("questionari", questionari);

        return "lista-questionari";
    }

    @GetMapping("{ID}")
    public String getQuestionario(
            Model model,
            @PathVariable int docenteID,
            @PathVariable int ID) {
        Questionario questionario = questionarioService.getQuestionarioCompleto(ID);
        if (questionario != null) {
            model.addAttribute("questionario", questionario);
        } else {
        }
        return "singolo-questionario";
    }

    @PostMapping("crea")
    public ResponseEntity<Questionario> creaQuestionario(@RequestParam int docenteID,
            @RequestParam Difficulty difficolta) {

        Questionario questionarioCreato = questionarioService.creaQuestionario(docenteID, difficolta);

        if (questionarioCreato != null) {
            return ResponseEntity.status(201).body(questionarioCreato);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Post -> Delete per standard REST
    @DeleteMapping("rimuovi/{ID}")
    public ResponseEntity<Questionario> rimuoviQuestionario(@PathVariable int ID) {
        boolean result = questionarioService.rimuoviQuestionario(ID);
        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Post -> Put per standard REST
    @PutMapping("modifica/{ID}")
    public ResponseEntity<Questionario> rinominaQuestionario(
            @PathVariable int ID,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String descrizione,
            @RequestParam(required = false) Difficulty livelloDiff) {

        Questionario q = questionarioService.getQuestionarioCompleto(ID);
        if (q == null) {
            return ResponseEntity.notFound().build();
        }
        boolean result = questionarioService.modificaInfo(q, nome, descrizione, livelloDiff);
        if (result) {
            return ResponseEntity.ok(q);
        } else {
            return ResponseEntity.internalServerError().build();
        }

    }

}