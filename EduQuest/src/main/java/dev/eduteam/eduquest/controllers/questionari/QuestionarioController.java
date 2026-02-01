package dev.eduteam.eduquest.controllers.questionari;

import dev.eduteam.eduquest.models.questionari.Compitino;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.questionari.Questionario.Difficulty;
import dev.eduteam.eduquest.services.questionari.CompitinoService;
import dev.eduteam.eduquest.services.questionari.QuestionarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;

@RestController
@RequestMapping("api/docente/{docenteID}/questionari")
public class QuestionarioController {

    @Autowired
    private QuestionarioService questionarioService;

    @Autowired
    private CompitinoService compitinoService;

    @GetMapping()
    public ArrayList<Questionario> getQuestionariByDocente(@PathVariable int docenteID) {
        return questionarioService.getQuestionariByDocente(docenteID);
    }

    @GetMapping("{ID}")
    public ResponseEntity<Questionario> getQuestionario(@PathVariable int docenteID, @PathVariable int ID) {
        Questionario questionario = questionarioService.getQuestionarioCompleto(ID);
        if (questionario != null) {
            return ResponseEntity.ok(questionario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("crea")
    public ResponseEntity<Questionario> creaQuestionario(
            @PathVariable int docenteID,
            @RequestParam Difficulty difficolta) { // potremmo mettere @RequestParam(defaultValue = "Medio")

        Questionario questionarioCreato = questionarioService.creaQuestionario(docenteID, difficolta);

        if (questionarioCreato != null) {
            return ResponseEntity.status(201).body(questionarioCreato);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("creaCompitino")
    public ResponseEntity<?> creaCompitino(@PathVariable int docenteID,
            @RequestParam Difficulty difficolta,
            @RequestParam String dataFine, // Formato YYYY-MM-DD
            @RequestParam int tentativi) {
        LocalDate scadenza = LocalDate.parse(dataFine);
        Compitino compitino = compitinoService.creaCompitino(docenteID, difficolta, scadenza, tentativi);
        if (compitino != null) {
            return ResponseEntity.status(201).body(compitino);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Post -> Delete per standard REST
    @DeleteMapping("rimuovi/{ID}")
    public ResponseEntity<Questionario> rimuoviQuestionario(@PathVariable int ID) {
        boolean result = questionarioService.rimuoviQuestionario(ID);
        if (result) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Post -> Put per standard REST, unificato metodo modifica
    @PutMapping("modifica/{ID}")
    public ResponseEntity<Questionario> setDescrizoneQuestionario(
            @PathVariable int ID,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String descrizione,
            @RequestParam(required = false) Difficulty difficolta) {

        Questionario q = questionarioService.getQuestionarioCompleto(ID);
        if (q == null) {
            return ResponseEntity.notFound().build();
        }

        // Validazione input - se un parametro non inviato, manteniamo valore attuale
        String nuovoNome = (nome != null && !nome.trim().isEmpty()) ? nome : q.getNome();
        String nuovaDesc = (descrizione != null && !descrizione.trim().isEmpty()) ? descrizione : q.getDescrizione();
        Difficulty nuovaDiff = (difficolta != null) ? difficolta : q.getLivelloDifficulty();

        boolean result = questionarioService.modificaInfo(q, nuovoNome, nuovaDesc, nuovaDiff);
        if (result) {
            return ResponseEntity.ok(q);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

}