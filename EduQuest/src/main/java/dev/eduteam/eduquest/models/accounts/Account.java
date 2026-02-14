package dev.eduteam.eduquest.models.accounts;

public abstract class Account {
    protected int accountID;
    protected String nome;
    protected String cognome;
    protected String userName;
    protected String email;
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

}
