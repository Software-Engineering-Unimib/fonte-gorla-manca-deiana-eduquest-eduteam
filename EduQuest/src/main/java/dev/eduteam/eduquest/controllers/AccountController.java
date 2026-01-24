package dev.eduteam.eduquest.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.eduteam.eduquest.models.Account;
import dev.eduteam.eduquest.models.Studente;
import dev.eduteam.eduquest.services.AccountService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("{varNome}/{varCognome}")
    public Account accountByNomeCognome(@PathVariable String varNome, @PathVariable String varCognome) {
        return accountService.getAccountByNomeECognome(varNome, varCognome);
    }

    @GetMapping("rimuovi/{varUserName}/{varPassword}")
    public void rimuoviAccount(@PathVariable String varUserName, @PathVariable String varPassword) {
        accountService.eliminaAccount(varUserName, varPassword);
    }

    @PostMapping("login")
    public Account login(
            @RequestParam(name = "user") String userName,
            @RequestParam(name = "pw") String password) {
        return accountService.logIn(userName, password);
    }

    @PostMapping("{tipo}") // Creo uno studente temporaneo che poi se in realtà è un docente
                           // l'AccountFactory lo crea comunque come tale
    public Account registraAccount(@RequestBody Studente temp, @PathVariable String tipo) {
        boolean isDocente = tipo.equalsIgnoreCase("docente");
        return accountService.registraAccount(temp.getNome(), temp.getCognome(), temp.getUserName(),
                temp.getEmail(), temp.getPassword(), isDocente);
    }

}
