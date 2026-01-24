package dev.eduteam.eduquest.controllers;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Questionario;
import dev.eduteam.eduquest.services.QuestionarioService;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("questionari")
@Validated
public class QuestionarioController {

    @Autowired
    private QuestionarioService questionarioService;

    @GetMapping()
    public ArrayList<Questionario> getQuestionari() {
        return questionarioService.getQuestionari();
    }

    @GetMapping("get/{ID}") // si può semplificare in @GetMapping("{ID}")
    public ResponseEntity<Questionario> getQuestionario(@PathVariable int ID) {
        Questionario questionario = questionarioService.getQuestionario(ID);
        if (questionario != null) {
            return ResponseEntity.ok(questionario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("crea")
    public ResponseEntity<Questionario> creaQuestionario() {

        Questionario questionarioCreato = questionarioService.creaQuestionario();

        /*
         * Non ho capito se questa linea serve davvero,
         * questionarioService.getQuestionari().add(questionarioCreato);
         */
        if (questionarioCreato != null) {
            return ResponseEntity.ok(questionarioCreato);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("rimuovi/{ID}")
    public ResponseEntity<Questionario> rimuoviQuestionario(@PathVariable int ID) {
        Questionario questionarioDaRimuovere = questionarioService.getQuestionario(ID);
        if (questionarioDaRimuovere == null) {
            return ResponseEntity.notFound().build();
        }
        boolean result = questionarioService.rimuoviQuestionario(ID);
        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("modifica/{ID}/rinomina")
    public ResponseEntity<Questionario> rinominaQuestionario(
            @PathVariable int ID,
            @RequestParam(name = "nome") String nome) {

        // Validazione del nome - non nullo o vuoto
        if (nome == null || nome.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Questionario questionarioDaModificare = questionarioService.getQuestionario(ID);
        if (questionarioDaModificare == null) {
            return ResponseEntity.notFound().build();
        }
        boolean result = questionarioService.modificaNome(questionarioDaModificare, nome);
        if (result) {
            return ResponseEntity.ok(questionarioDaModificare);
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
        Questionario questionarioDaModificare = questionarioService.getQuestionario(ID);
        if (questionarioDaModificare == null) {
            return ResponseEntity.notFound().build();
        }
        boolean result = questionarioService.modificaDescrizione(questionarioDaModificare, descrizione);
        if (result) {
            return ResponseEntity.ok(questionarioDaModificare);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Forse da spostare in DomandeController e sistemare i path
    @PostMapping("/modifica/{ID}/aggiungi_domanda")
    public ResponseEntity<Questionario> aggiungiDomanda(@PathVariable int ID) {
        Questionario questionarioDaModificare = questionarioService.getQuestionario(ID);

        if (questionarioDaModificare == null) {
            return ResponseEntity.notFound().build();
        }
        boolean result = questionarioService.aggiungiDomanda(questionarioDaModificare);

        if (result) {
            return ResponseEntity.ok(questionarioDaModificare);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/modifica/{ID}/rimuovi_domanda/{domandaID}")
    public ResponseEntity<Questionario> rimuoviDomanda(@PathVariable int ID, @PathVariable int domandaID) {
        Questionario questionarioDaModificare = questionarioService.getQuestionario(ID);
        if (questionarioDaModificare == null) {
            return ResponseEntity.notFound().build();
        }

        boolean rimosso = questionarioService.rimuoviDomanda(ID, domandaID);
        if (!rimosso) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(questionarioDaModificare);
    }
}
