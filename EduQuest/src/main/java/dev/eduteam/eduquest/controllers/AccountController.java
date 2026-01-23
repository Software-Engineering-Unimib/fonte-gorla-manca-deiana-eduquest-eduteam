package dev.eduteam.eduquest.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.eduteam.eduquest.models.Account;
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

    @PostMapping()
    public Account registraAccount(
            @RequestParam(name = "nom") String nome,
            @RequestParam(name = "cog") String cognome,
            @RequestParam(name = "user") String userName,
            @RequestParam(name = "em") String email,
            @RequestParam(name = "pw") String password,
            @RequestParam(name = "doc") boolean docente) {
        return accountService.registraAccount(nome, cognome, userName, email, password, docente);
    }

}
