package dev.eduteam.eduquest.models;

public class Docente extends Account {

    public Docente() {
        super();
    }

    public Docente(String nome, String cognome, String userName, String email, String password) {
        super(nome, cognome, userName, email, password);
    }

    @Override
    public boolean isDocente() {
        return true;
    }
}
