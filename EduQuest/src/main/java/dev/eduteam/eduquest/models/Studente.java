package dev.eduteam.eduquest.models;

public class Studente extends Account {

    public Studente() {
        super();
    }

    public Studente(String nome, String cognome, String userName, String email, String password) {
        super(nome, cognome, userName, email, password);
    }

    @Override
    public boolean isDocente() {
        return false;
    }

}
