package dev.eduteam.eduquest.controllers.accounts;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.eduteam.eduquest.models.accounts.Account;
import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.services.accounts.AccountService;
import dev.eduteam.eduquest.services.accounts.DocenteService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("api/docente")
public class DocenteController {

    @Autowired
    private DocenteService docenteService;

    @Autowired
    private AccountService accountService;

    @GetMapping("listaDocenti")
    public List<Docente> getDocenti() {
        return docenteService.getAll();
    }

    @GetMapping("{varID}")
    public ResponseEntity<Docente> getDocenteByID(@PathVariable int varID) {
        Docente docente = docenteService.getByID(varID);
        if (docente != null) {
            return ResponseEntity.ok(docente);
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
                    true);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuovo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("login")
    public ResponseEntity<?> logIn(@RequestParam String userName, @RequestParam String pw) {
        try {
            Docente docente = accountService.logInDocente(userName, pw);
            return ResponseEntity.ok(docente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("aggiorna")
    public ResponseEntity<?> aggiornaDocente(
            @RequestParam String user,
            @RequestParam String pw,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cognome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String newPw) {
        try {
            Account aggiornato = accountService.aggiornaAccount(user, pw, nome, cognome, email, newPw);
            if (aggiornato instanceof Docente) {
                return ResponseEntity.ok().body((Docente) aggiornato);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("L'account non è un docente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("rimuovi")
    public ResponseEntity<String> rimuoviDocente(@RequestParam String userName, @RequestParam String pw) {
        try {
            accountService.eliminaAccount(userName, pw);
            return ResponseEntity.ok("Account eliminato correttamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Errore: " + e.getMessage());
        }
    }

    @PostMapping("aggiornaInsegnamento")
    public ResponseEntity<?> updateInsegnamento(@RequestParam(name = "id") int id, @RequestParam String nuovaMateria) {
        try {
            boolean successo = docenteService.aggiornaInsegnamento(id, nuovaMateria);
            if (successo) {
                Docente aggiornato = docenteService.getByID(id);
                return ResponseEntity.ok(aggiornato);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Docente con ID " + id + " non trovato.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Errore durante l'aggiornamento: " + e.getMessage());
        }
    }

}
