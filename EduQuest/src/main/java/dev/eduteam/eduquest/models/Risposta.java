package dev.eduteam.eduquest.models;

public class Risposta {

    private int ID; // GLI ID SARANNO LEGATI ALLE RISPOSTA SALVATI SUL DB
    private String testo;
    private boolean corretta;

    public Risposta(String testo) {
        setTesto(testo);
        this.corretta = false;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {

        if (testo == null) {
            throw new IllegalArgumentException("Il testo di una risposta non puo' essere nullo");
        }

        this.testo = testo;
    }

    public boolean isCorretta() {
        return corretta;
    }

    public void setCorretta(boolean corretta) {
        this.corretta = corretta;
    }
}
