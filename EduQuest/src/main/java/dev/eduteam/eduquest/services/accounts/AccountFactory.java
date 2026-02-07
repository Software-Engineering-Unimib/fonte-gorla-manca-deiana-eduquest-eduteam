package dev.eduteam.eduquest.services.accounts;

import dev.eduteam.eduquest.models.accounts.Account;
import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.accounts.Studente;

public class AccountFactory {

    public static Account creaAccount(String nome, String cognome, String userName, String email, String password,
            boolean docente) {
        if (docente) {
            return new Docente(nome, cognome, userName, email, password);
        } else {
            return new Studente(nome, cognome, userName, email, password);
        }
    }
}
