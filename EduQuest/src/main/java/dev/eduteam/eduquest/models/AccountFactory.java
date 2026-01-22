package dev.eduteam.eduquest.models;

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
