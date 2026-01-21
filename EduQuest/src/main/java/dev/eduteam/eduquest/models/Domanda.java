package dev.eduteam.eduquest.models;

import java.util.ArrayList;

public class Domanda {

    private int ID; // GLI ID SARANNO LEGATI ALLE DOMANDE SALVATI SUL DB
    private String testo;

    private int numeroRisposte;
    private ArrayList<Risposta> elencoRisposte;
    private Risposta rispostaCorretta;

    public Domanda(int numeroRisposte) {

        setNumeroRisposte(numeroRisposte);
        elencoRisposte = creaRisposte(numeroRisposte);
        testo = "";
    }

    public Domanda(String testo, int numeroRisposte) {
        super();
        setTesto(testo);
    }

    public int getID() { return ID; }

    public void setID(int ID) { this.ID = ID; }

    public String getTesto() { return testo; }

    public void setTesto(String testo) {
        if (testo == null) {
            throw new IllegalArgumentException("Il testo di una domanda non puo' essere nullo");
        }
        this.testo = testo;
    }

    public int getNumeroRisposte() { return numeroRisposte; }

    private void setNumeroRisposte(int numeroRisposte) {

        if (numeroRisposte < 0) {
            throw new IllegalArgumentException("Il numero di risposte non puo' essere negativo");
        }

        this.numeroRisposte = numeroRisposte;
    }
    private ArrayList<Risposta> creaRisposte(int numeroRisposte) {

        ArrayList<Risposta> tempRisposte = new ArrayList<Risposta>();

        for (int i = 0; i < numeroRisposte; i++) {

            Risposta risposta = new Risposta();
            tempRisposte.add(risposta);
        }

        return tempRisposte;
    }

    public void aggiungiRisposta() {

        Risposta risposta = new Risposta();
        setNumeroRisposte(numeroRisposte + 1);
        elencoRisposte.add(risposta);

    }

    public void rimuoviDomanda(int ID) {

        Risposta rispostaDaRimuovere = elencoRisposte.stream().filter(r -> r.getID() == ID).findFirst().orElse(null);
        elencoRisposte.remove(rispostaDaRimuovere);
        setNumeroRisposte(numeroRisposte - 1);

    }
}
