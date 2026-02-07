package dev.eduteam.eduquest.models.accounts;

public class Docente extends Account {

    private String insegnamento;

    public Docente() {
        super();
        this.insegnamento = null;
    }

    public Docente(String nome, String cognome, String userName, String email, String password) {
        super(nome, cognome, userName, email, password);
        this.insegnamento = null;
    }

    public String getInsegnamento() {
        return insegnamento;
    }

    public void setInsegnamento(String insegnamento) {
        this.insegnamento = insegnamento;
    }

    @Override
    public boolean isDocente() {
        return true;
    }
}
