package dev.eduteam.eduquest.controllers.accounts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.eduteam.eduquest.models.accounts.Account;
import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.models.questionari.Compilazione;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.services.accounts.AccountService;
import dev.eduteam.eduquest.services.accounts.StudenteService;
import dev.eduteam.eduquest.services.questionari.CompilazioneService;
import dev.eduteam.eduquest.services.questionari.QuestionarioService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/studente")
public class StudenteController {

    @Autowired
    private StudenteService studenteService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private QuestionarioService questionarioService;

    @Autowired
    private CompilazioneService compilazioneService;

    @GetMapping("listaStudenti")
    public List<Studente> getAll() {
        return studenteService.getAll();
    }

    @GetMapping("{varID}")
    public ResponseEntity<Studente> getByID(@PathVariable int varID) {
        Studente studente = studenteService.getById(varID);
        if (studente != null) {
            return ResponseEntity.ok(studente);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("registra")
    public ResponseEntity<?> registra(@RequestBody Map<String, Object> payload) {
        try {
            Account nuovo = accountService.registraAccount(
                    (String) payload.get("nome"),
                    (String) payload.get("cognome"),
                    (String) payload.get("userName"),
                    (String) payload.get("email"),
                    (String) payload.get("password"),
                    false);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuovo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("login")
    public ResponseEntity<?> logIn(@RequestParam String userName, @RequestParam String pw) {
        try {
            Studente studente = accountService.logInStudente(userName, pw);
            return ResponseEntity.ok(studente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("aggiorna")
    public ResponseEntity<?> aggiornaStudente(
            @RequestParam String user,
            @RequestParam String pw,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cognome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String newPw) {
        try {
            Account aggiornato = accountService.aggiornaAccount(user, pw, nome, cognome, email, newPw);
            if (aggiornato instanceof Studente) {
                return ResponseEntity.ok().body((Studente) aggiornato);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("L'account non è uno studente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("rimuovi")
    public ResponseEntity<String> rimuoviStudente(@RequestParam String userName, @RequestParam String pw) {
        try {
            accountService.eliminaAccount(userName, pw);
            return ResponseEntity.ok("Account eliminato correttamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Errore: " + e.getMessage());
        }
    }

    // Verrà mostrato allo studente una simil-galleria di questionari
    @GetMapping("{studenteID}/questionari")
    public ResponseEntity<List<Questionario>> mostraQuestionariDisponibili(@PathVariable int studenteID) {
        List<Questionario> questionari = questionarioService.getQuestionariDisponibliPerStudente(studenteID);
        if (questionari.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        } else {
            return ResponseEntity.ok(questionari);
        }
    }

    // Dovrebbe inizializzare la compilazione e mostrare la prima domanda del
    // questionario
    @GetMapping("{studenteID}/compila/{questionarioID}")
    public ResponseEntity<?> compilaQuestionario(@PathVariable int studenteID,
            @PathVariable int questionarioID) {
        Compilazione c = compilazioneService.creaCompilazione(studenteID, questionarioID);
        if (c != null) {
            Questionario q = questionarioService.getQuestionarioCompleto(questionarioID);
            if (q.getElencoDomande().isEmpty()) {
                return ResponseEntity.badRequest().body("Il questionario non ha domande.");
            }
            return ResponseEntity.ok(q.getElencoDomande().getFirst());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Accesso negato: Tentativi esauriti.");
        }
    }

    // Memorizza la risposta alla domanda e mostra la domanda successiva
    @GetMapping("{studenteID}/compila/{questionarioID}/{compilazioneID}/{domandaID}")
    public ResponseEntity<?> rispondiDomanda(@PathVariable int studenteID,
            @PathVariable int questionarioID, @PathVariable int compilazioneID,
            @PathVariable int domandaID, @RequestParam int rispostaID) {
        if (!compilazioneService.inserisciRispostaComp(compilazioneID, domandaID, rispostaID)) {
            return ResponseEntity.internalServerError().body("Errore durante il salvataggio della risposta");
        }
        Domanda d = questionarioService.getDomandaSuccessiva(questionarioID, domandaID);
        if (d != null)
            return ResponseEntity.ok(d);
        // questionario completato: aggiornamento automatico media studente e status
        // compilazione
        if (compilazioneService.chiudiCompilazione(studenteID, compilazioneID)) {
            return ResponseEntity.ok("Questionario completato");
        }
        return ResponseEntity.internalServerError().body("Errore durante la chiusura della compilazione");
    }

    // Torna le compilazione completate dallo studente
    @GetMapping("{studenteID}/compilazioni")
    public ResponseEntity<ArrayList<Compilazione>> getCompilazioniByStudente(@PathVariable int studenteID) {
        // va bene anche una lista vuota, ad es. se lo studente non ha ancora
        // compilato alcun questionario
        return ResponseEntity.ok(compilazioneService.getCompilazioniCompletate(studenteID));
    }

    // Torna le compilazioni lasciare in sospeso
    @GetMapping("{studenteID}/riprendi")
    public ResponseEntity<ArrayList<Compilazione>> getCompilazioniInSospeso(@PathVariable int studenteID) {
        return ResponseEntity.ok(compilazioneService.getCompilazioniInSospeso(studenteID));
    }

    // TODO sistemare in modo che ricominci dalla prima domanda senza risposta
    @GetMapping("{studenteID}/riprendi/{questionarioID}")
    // ->
    // @GetMapping("{studenteID}/compila/{questionarioID}/{compilazioneID}/{domandaID}")
    public ResponseEntity<?> riprendiCompilazione(
            @PathVariable int studenteID,
            @PathVariable int questionarioID) {

        Compilazione c = compilazioneService.riprendiCompilazione(studenteID, questionarioID);
        if (c != null) {
            Domanda d = questionarioService.getDomandaInSospeso(questionarioID, c.getRisposte());
            if (d != null)
                return ResponseEntity.ok(d);
            // se d == null allora lo student ha risposto a tutte le domande
            if (compilazioneService.chiudiCompilazione(studenteID, c.getID())) {
                return ResponseEntity.ok("Questionario completato");
            }
            return ResponseEntity.internalServerError().body("Errore durante la chiusura della compilazione");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Nessuna compilazione in corso trovata per questo questionario.");
    }
}
