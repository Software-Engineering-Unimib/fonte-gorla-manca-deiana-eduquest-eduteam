package dev.eduteam.eduquest.controllers;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Questionario;
import dev.eduteam.eduquest.services.DomandaService;
import dev.eduteam.eduquest.services.QuestionarioService;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

    @GetMapping("{ID}")
    public ResponseEntity<Questionario> getQuestionario(@PathVariable int ID) {
        Questionario questionario = questionarioService.getQuestionarioCompleto(ID);
        if (questionario != null) {
            return ResponseEntity.ok(questionario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("crea")
    public ResponseEntity<Questionario> creaQuestionario(@RequestParam int docenteID) {

        Questionario questionarioCreato = questionarioService.creaQuestionario(docenteID);

        if (questionarioCreato != null) {
            return ResponseEntity.status(201).body(questionarioCreato);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("docente/{docenteID}")
    public ResponseEntity<ArrayList<Questionario>> getQuestionariDocente(@PathVariable int docenteID) {
        ArrayList<Questionario> questionari = questionarioService.getQuestionariByDocente(docenteID);
        if (!questionari.isEmpty()) {
            return ResponseEntity.ok(questionari);
        } else {
            return ResponseEntity.noContent().build();
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
    @PutMapping("modifica/{ID}/rinomina")
    public ResponseEntity<Questionario> rinominaQuestionario(
            @PathVariable int ID,
            @RequestParam(name = "nome") String nome) {

        // Validazione del nome - non nullo o vuoto
        if (nome == null || nome.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Questionario q = questionarioService.getQuestionarioCompleto(ID);
        if (q == null) {
            return ResponseEntity.notFound().build();
        }
        boolean result = questionarioService.modificaInfo(q, nome, q.getDescrizione());
        if (result) {
            return ResponseEntity.ok(q);
        } else {
            return ResponseEntity.internalServerError().build();
        }

    }

    @PostMapping("modifica/{ID}/descrizione")
    public ResponseEntity<Questionario> setDescrizoneQuestionario(
            @PathVariable int ID,
            @RequestParam(name = "descrizione") String descrizione) {

        // Validazione della descrizione - non nulla o vuota
        if (descrizione == null || descrizione.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Questionario q = questionarioService.getQuestionarioCompleto(ID);
        if (q == null) {
            return ResponseEntity.notFound().build();
        }
        boolean result = questionarioService.modificaInfo(q, q.getNome(), descrizione);
        if (result) {
            return ResponseEntity.ok(q);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

}
