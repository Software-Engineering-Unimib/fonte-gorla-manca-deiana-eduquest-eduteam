package dev.eduteam.eduquest.controllers.accounts;

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
import dev.eduteam.eduquest.services.accounts.AccountService;
import dev.eduteam.eduquest.services.accounts.StudenteService;

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
    // TODO aggiungere updateMediaPunteggio
}
