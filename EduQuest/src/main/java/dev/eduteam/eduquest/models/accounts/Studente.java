package dev.eduteam.eduquest.models.accounts;

public class Studente extends Account {

    private double mediaPunteggio;
    private int eduPoints;

    public Studente() {
        super();
        this.mediaPunteggio = 0.0;
        this.eduPoints = 0;
    }

    public Studente(String nome, String cognome, String userName, String email, String password) {
        super(nome, cognome, userName, email, password);
        this.mediaPunteggio = 0.0;
        this.eduPoints = 0;
    }

    public double getMediaPunteggio() {
        return mediaPunteggio;
    }

    public void setMediaPunteggio(double mediaPunteggio) {
        this.mediaPunteggio = mediaPunteggio;
    }

    public int getEduPoints() {
        return eduPoints;
    }

    public void setEduPoints(int eduPoints) {
        if (eduPoints < 0) {
            throw new IllegalArgumentException("Lo studente non pupò avere meno di 0 eduPoints");
        }
        this.eduPoints = eduPoints;
    }

    @Override
    public boolean isDocente() {
        return false;
    }

}
