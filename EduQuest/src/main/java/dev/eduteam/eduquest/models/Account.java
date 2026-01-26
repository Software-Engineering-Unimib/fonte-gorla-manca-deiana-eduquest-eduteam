package dev.eduteam.eduquest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Account {
    protected int accountID;
    protected String nome;
    protected String cognome;
    protected String userName;
    protected String email;
    // @JsonIgnore // non voglio che la password sia visibile, cosi funziona, TBD
    // come rendere la password non visibile
    protected String password;

    public Account() {
    }

    public Account(String nome, String cognome, String userName, String email, String password) {
        super();
        setNome(nome);
        setCognome(cognome);
        setUserName(userName);
        setEmail(email);
        setPassword(password);
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

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

    public String getPassword() {
        return password;
    }

    public abstract boolean isDocente();

    @Override
    public String toString() {
        return "Account -> " + userName + " (" + (isDocente() ? "Docente" : "Studente") + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Account))
            return false;
        Account other = (Account) o;
        return userName != null && userName.equals(other.userName);
    }

    @Override
    public int hashCode() {
        return userName != null ? userName.hashCode() : 0;
    }
}
