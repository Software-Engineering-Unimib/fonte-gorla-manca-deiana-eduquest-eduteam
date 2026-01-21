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

    public Questionario(int numeroDomande /*, Docente docente */) {

        setNumeroDomande(numeroDomande);

        dataCreazione = LocalDate.now();
        // creatore = docente;
        elencoDomande = creaDomande(numeroDomande);
        descrizione = "";

    }

    // IL SUPER NON VUOLE IL PARAMETRO "numeroDomande" QUINDI SE CAUSA PROBLEMI PRENDETEVELA CON JAVA

    public Questionario(String descrizione, int numeroDomande /*, Docente docente */) {

        super(/*, docente*/);
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

    private void setNumeroDomande(int numeroDomande) {

        if (numeroDomande < 0) {
            throw new IllegalArgumentException("Il numero di domande non puo' essere negativo");
        }

        this.numeroDomande = numeroDomande;
    }

    public ArrayList<Domanda> getDomande() { return elencoDomande;}

    private ArrayList<Domanda> creaDomande(int numeroDomande) {

        ArrayList<Domanda> tempDomande = new ArrayList<Domanda>();

        for (int i = 0; i < numeroDomande; i++) {

            Domanda domanda = new Domanda(0);
            tempDomande.add(domanda);
        }

        return tempDomande;
    }

    public LocalDate getDataCreazione() { return dataCreazione; }

    public void aggiungiDomanda(int numeroRisposte) {

        Domanda domanda = new Domanda(numeroRisposte);
        setNumeroDomande(numeroDomande + 1);
        elencoDomande.add(domanda);

    }

    public void rimuoviDomanda(int ID) {

        Domanda domandaDaRimuovere = elencoDomande.stream().filter(d -> d.getID() == ID).findFirst().orElse(null);
        elencoDomande.remove(domandaDaRimuovere);
        setNumeroDomande(numeroDomande - 1);

    }

    // AGGIUNGERE EVENTUALI GETTER PER DOCENTE
}
