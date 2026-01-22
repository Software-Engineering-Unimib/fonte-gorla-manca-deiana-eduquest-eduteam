package dev.eduteam.eduquest.models;


import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;

public class Questionario {

    // Variabili private del questionario
    private int ID; // GLI ID SARANNO LEGATI AI QUESTIONARI SALVATI SUL DB
    private String descrizione;

    private int numeroDomande;
    private ArrayList<Domanda> elencoDomande;

    private LocalDate dataCreazione;
    // private Docente creatore;

    // IL SUPER NON VUOLE IL PARAMETRO "numeroDomande" QUINDI SE CAUSA PROBLEMI PRENDETEVELA CON JAVA

    public Questionario(String descrizione, ArrayList<Domanda> domande /*, Docente docente */) {

        setNumeroDomande(domande.size());
        dataCreazione = LocalDate.now();
        // creatore = docente;
        elencoDomande = domande;
        setDescrizione(descrizione);
    }

    public int getID() { return ID; }

    public void setID(int ID) { this.ID = ID; }

    public String getDescrizione() { return descrizione; }

    public void setDescrizione(String descrizione) {

        if (descrizione == null) {
            throw new IllegalArgumentException("La descrizione di un questionario non puo' essere nulla");
        }

        this.descrizione = descrizione;
    }

    public int getNumeroDomande() { return numeroDomande; }

    public void setNumeroDomande(int numeroDomande) {

        if (numeroDomande < 0) {
            throw new IllegalArgumentException("Il numero di domande non puo' essere negativo");
        }

        this.numeroDomande = numeroDomande;
    }

    public ArrayList<Domanda> getElencoDomande() { return elencoDomande;}

    public LocalDate getDataCreazione() { return dataCreazione; }

    // AGGIUNGERE EVENTUALI GETTER PER DOCENTE
}
