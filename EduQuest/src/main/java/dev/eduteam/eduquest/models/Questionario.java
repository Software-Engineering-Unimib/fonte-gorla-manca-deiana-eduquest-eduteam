package dev.eduteam.eduquest.models;

import java.time.LocalDate;
import java.util.ArrayList;

public class Questionario {

    // Variabili private del questionario
    private int ID; // GLI ID SARANNO LEGATI AI QUESTIONARI SALVATI SUL DB
    //ID inizializzato nel costruttore vuoto
    private String nome; //null
    private String descrizione; //null

    private int numeroDomande; //0
    private ArrayList<Domanda> elencoDomande; //vuota?

    private LocalDate dataCreazione; //inizializzata di default nel costruttore senza parametri
    // private Docente creatore;

    //COSTRUTTORE SENZA PARAMETRI

    // IL SUPER NON VUOLE IL PARAMETRO "numeroDomande" QUINDI SE CAUSA PROBLEMI
    // PRENDETEVELA CON JAVA

    public Questionario(String nome, String descrizione, ArrayList<Domanda> domande /* , Docente docente */) {

        setNome(nome);
        setNumeroDomande(domande.size());
        dataCreazione = LocalDate.now(); //questo va spostato nel costruttore senza parametri
        // creatore = docente;
        elencoDomande = domande;
        setDescrizione(descrizione);
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
        return numeroDomande;
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

    public LocalDate getDataCreazione() {
        return dataCreazione;
    }

    // AGGIUNGERE EVENTUALI GETTER PER DOCENTE
}
