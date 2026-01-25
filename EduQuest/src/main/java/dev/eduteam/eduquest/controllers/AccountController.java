package dev.eduteam.eduquest.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.eduteam.eduquest.models.Account;
import dev.eduteam.eduquest.services.AccountService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping()
    public List<Account> accountList() {
        return accountService.getAllAccounts();
    }

    @GetMapping("{varUserName}")
    public ResponseEntity<Account> accountByUserName(@PathVariable String varUserName) {
        Account account = accountService.getAccountByUserName(varUserName);
        if (account != null) {
            return ResponseEntity.ok(account);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("rimuovi/{varUserName}/{varPassword}")
    public ResponseEntity<String> rimuoviAccount(@PathVariable String varUserName, @PathVariable String varPassword) {
        try {
            accountService.eliminaAccount(varUserName, varPassword);
            return ResponseEntity.ok("Account eliminato correttamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Errore: " + e.getMessage());
        }
    }

    @PostMapping("login")
    public ResponseEntity<?> login(
            @RequestParam(name = "user") String userName,
            @RequestParam(name = "pw") String password) {

        try {
            Account account = accountService.logIn(userName, password);
            return ResponseEntity.ok().header("Login-Status", "Login effettuato con successo").body(account);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/registra/{tipo}") // Creo uno studente temporaneo che poi se in realtà è un docente
    // l'AccountFactory lo crea comunque come tale
    public ResponseEntity<?> registraAccount(@RequestBody Map<String, Object> payload, @PathVariable String tipo) {
        try {
            boolean urlDiceDocente = tipo.equalsIgnoreCase("docente");
            boolean bodyDiceDocente = payload.containsKey("docente") && (boolean) payload.get("docente");
            if (urlDiceDocente != bodyDiceDocente) {
                return ResponseEntity.badRequest().body(
                        "Incoerenza nei dati: l'URL richiede " + (urlDiceDocente ? "Docente" : "Studente") +
                                " ma il corpo della richiesta indica " + (bodyDiceDocente ? "Docente" : "Studente"));
            }

            Account nuovo = accountService.registraAccount(
                    (String) payload.get("nome"),
                    (String) payload.get("cognome"),
                    (String) payload.get("userName"),
                    (String) payload.get("email"),
                    (String) payload.get("password"),
                    urlDiceDocente // o bodyDiceDocente, tanto ora sono uguali!
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(nuovo);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("aggiorna")
    public ResponseEntity<?> aggiornaAccount(
            @RequestParam(name = "user") String userName,
            @RequestParam(name = "pw") String passwordAttuale,
            @RequestParam(name = "nome", required = false) String nuovoNome,
            @RequestParam(name = "cognome", required = false) String nuovoCognome,
            @RequestParam(name = "email", required = false) String nuovaEmail,
            @RequestParam(name = "newPw", required = false) String nuovaPassword) {

        try {
            Account accountAggiornato = accountService.aggiornaAccount(
                    userName, passwordAttuale, nuovoNome, nuovoCognome, nuovaEmail, nuovaPassword);

            // Restituiamo 200 OK con l'oggetto aggiornato
            return ResponseEntity.ok()
                    .header("Update-Status", "Dati aggiornati correttamente")
                    .body(accountAggiornato);

        } catch (IllegalArgumentException e) {
            // Gestiamo gli errori di validazione o credenziali errate (400 o 401)
            // Se l'errore è "Password attuale errata", 401 sarebbe più preciso,
            // ma 400 Bad Request è comunque accettabile per errori di logica.
            return ResponseEntity.badRequest().body("Errore durante l'aggiornamento: " + e.getMessage());
        }
    }

}
