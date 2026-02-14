package dev.eduteam.eduquest.models.questionari;

import java.time.LocalDate;
import java.util.ArrayList;

import dev.eduteam.eduquest.models.accounts.Docente;

public class Compitino extends Questionario {
    private LocalDate dataFine; // con LocalDateTime si potrebbe impostare anche l'ora
    private int tentativiMax;
    private int puntiBonus;
    private boolean assegnatiPtBonus = false;

    public Compitino(String nome, String descrizione, ArrayList<Domanda> domande, Docente docente,
            Difficulty livelloDiff, LocalDate dataFine, int tentativiMax) {
        super(nome, descrizione, domande, docente, livelloDiff);
        setDataFine(dataFine);
        setTentativiMax(tentativiMax);
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public int getTentativiMax() {
        return tentativiMax;
    }

    public void setTentativiMax(int tentativiMax) {
        if (tentativiMax < 1) {
            throw new IllegalArgumentException("Il numero di tentativi deve essere almeno 1");
        }
        this.tentativiMax = tentativiMax;
    }

    public int getPuntiBonus() {
        return puntiBonus;
    }

    public void setPuntiBonus(int puntiBonus) {
        if (puntiBonus < 0) {
            throw new IllegalArgumentException("I punti bonus devono essere >= 0");
        }
        this.puntiBonus = puntiBonus;
    }

    public boolean getAssegnatiPtBonus() {
        return assegnatiPtBonus;
    }

    public void setAssegnatiPtBonus(boolean assegnatiPtBonus) {
        this.assegnatiPtBonus = assegnatiPtBonus;
    }

}
