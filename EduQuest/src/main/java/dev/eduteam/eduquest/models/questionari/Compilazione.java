
package dev.eduteam.eduquest.models.questionari;

import dev.eduteam.eduquest.models.accounts.Studente;

public class Compilazione {
    private int ID;
    private Studente studente;
    private Questionario questionario;
    private Risposta[] risposte;
    private boolean completato = false;
    private int punteggio = 0;
    private int numeroDomande;

    public Compilazione(Studente studente, Questionario questionario) {
        setStudente(studente);
        setQuestionario(questionario);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Studente getStudente() {
        return studente;
    }

    public void setStudente(Studente studente) {
        this.studente = studente;
    }

    public Questionario getQuestionario() {
        return questionario;
    }

    public void setQuestionario(Questionario questionario) {
        this.questionario = questionario;
        this.numeroDomande = questionario.getNumeroDomande();
        this.risposte = new Risposta[numeroDomande];
    }

    public Risposta[] getRisposte() {
        return risposte;
    }

    public boolean isCompletato() {
        return completato;
    }

    public void setCompletato(boolean completato) {
        this.completato = completato;
    }

    public int getPunteggio() {
        return punteggio;
    }

    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }

    public int getNumeroDomande() {
        return numeroDomande;
    }
}
