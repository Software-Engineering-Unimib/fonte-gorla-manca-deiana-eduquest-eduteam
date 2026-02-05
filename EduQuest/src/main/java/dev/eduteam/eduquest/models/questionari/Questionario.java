package dev.eduteam.eduquest.models.questionari;

import java.time.LocalDate;
import java.util.ArrayList;

import dev.eduteam.eduquest.models.accounts.Docente;

public class Questionario {

    public enum Difficulty {
        Facile,
        Medio,
        Difficile
    }

    // Variabili private del questionario
    private int ID; // GLI ID SARANNO LEGATI AI QUESTIONARI SALVATI SUL DB
    private String nome;
    private String descrizione;

    private int numeroDomande;
    private ArrayList<Domanda> elencoDomande;

    private LocalDate dataCreazione; // inizializzata di default nel costruttore senza parametri
    private Docente docenteCreatore; // ID del docente che ha creato il questionario
    private String materia;
    private Difficulty livelloDiff;

    public Questionario(String nome, String descrizione, ArrayList<Domanda> domande, Docente docente,
            Difficulty livelloDiff) {
        setNome(nome);
        setDescrizione(descrizione);
        this.elencoDomande = (domande != null) ? domande : new ArrayList<>();
        setNumeroDomande(domande.size());
        setDataCreazione(LocalDate.now());
        setDocente(docente);
        setLivelloDiff(livelloDiff);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {

        if (descrizione == null) {
            throw new IllegalArgumentException("La descrizione di un questionario non puo' essere nulla");
        }

        this.descrizione = descrizione;
    }

    public int getNumeroDomande() {
        // Se la lista esiste, restituisce la sua dimensione reale, altrimenti
        // restituisce il valore salvato nel campo.
        return (elencoDomande != null && !elencoDomande.isEmpty()) ? elencoDomande.size() : numeroDomande;
    }

    public void setNumeroDomande(int numeroDomande) {

        if (numeroDomande < 0) {
            throw new IllegalArgumentException("Il numero di domande non puo' essere negativo");
        }

        this.numeroDomande = numeroDomande;
    }

    public ArrayList<Domanda> getElencoDomande() {
        return elencoDomande;
    }

    // Serve solo internamente al Service
    public void setElencoDomande(ArrayList<Domanda> elencoDomande) {
        this.elencoDomande = (elencoDomande != null) ? elencoDomande : new ArrayList<>();
        this.numeroDomande = this.elencoDomande.size();
    }

    public LocalDate getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDate dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public Docente getDocente() {
        return docenteCreatore;
    }

    public void setDocente(Docente docente) {
        this.docenteCreatore = docente;
        this.materia = docente.getInsegnamento();
    }

    public String getMateria() {
        return materia;
    }

    public Difficulty getLivelloDifficulty() {
        return livelloDiff;
    }

    public void setLivelloDiff(Difficulty livello) {
        this.livelloDiff = livello;
    }

}
