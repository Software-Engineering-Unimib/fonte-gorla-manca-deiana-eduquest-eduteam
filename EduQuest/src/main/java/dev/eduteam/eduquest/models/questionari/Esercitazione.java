package dev.eduteam.eduquest.models.questionari;

import java.util.ArrayList;

import dev.eduteam.eduquest.models.accounts.Docente;

public class Esercitazione extends Questionario {

    // Suggerimenti generali del docente sull'esercitazione
    private String noteDidattiche;

    public Esercitazione(String nome, String descrizione, ArrayList<Domanda> domande, Docente docente,
            Difficulty livelloDiff) {
        super(nome, descrizione, domande, docente, livelloDiff);
    }

    public String getNoteDidattiche() {
        return noteDidattiche;
    }

    public void setNoteDidattiche(String note) {
        this.noteDidattiche = note;
    }

    /*
     * TODO Si potrebbe aggiungere un metodo che in base al punteggio ottenuto (in
     * %) ti consiglia se ripassare l'argomento
     */
}
