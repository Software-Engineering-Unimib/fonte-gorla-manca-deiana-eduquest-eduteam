package dev.eduteam.eduquest.models;

public abstract class Account {
    protected String nome;
    protected String cognome;
    protected String userName;
    protected String email;
    protected String password;

    public Account(String nome, String cognome, String userName, String email, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.userName = userName;
        this.email = email;
        this.password = password;
    };

    public abstract boolean isDocente();

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Account -> " + userName + " (" + (isDocente() ? "Docente" : "Studente") + ")";
    }
}
