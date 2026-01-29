package dev.eduteam.eduquest.models.accounts;

public class Studente extends Account {

    private double mediaPunteggio;

    public Studente() {
        super();
        this.mediaPunteggio = 0.0;
    }

    public Studente(String nome, String cognome, String userName, String email, String password) {
        super(nome, cognome, userName, email, password);
        this.mediaPunteggio = 0.0;
    }

    public double getMediaPunteggio() {
        return mediaPunteggio;
    }

    public void setMediaPunteggio(double mediaPunteggio) {
        this.mediaPunteggio = mediaPunteggio;
    }

    @Override
    public boolean isDocente() {
        return false;
    }

}
