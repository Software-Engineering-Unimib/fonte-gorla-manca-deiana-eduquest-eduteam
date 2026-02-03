package dev.eduteam.eduquest.models.questionari;

public class Feedback {
    private int ID;
    private Domanda domanda;
    private String testo;

    public Feedback(Domanda domanda, String testo) {
        this.domanda = domanda;
        this.testo = testo;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Domanda getDomanda() {
        return domanda;
    }

    public void setDomanda(Domanda domanda) {
        this.domanda = domanda;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

}
