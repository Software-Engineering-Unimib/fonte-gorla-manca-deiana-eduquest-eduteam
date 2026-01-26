package dev.eduteam.eduquest.models;

import java.util.ArrayList;

public class DomandaMultipleRisposte extends Domanda{

    protected ArrayList<Risposta> risposteCorrette;

    public DomandaMultipleRisposte(String testo) {

        tipoDomanda = Type.DOMANDA_MULTIPLE_RISPOSTE;

        setNumeroRisposte(0);
        elencoRisposte = new ArrayList<Risposta>();
        risposteCorrette = new ArrayList<Risposta>();
        setTesto(testo);
    }

    public ArrayList<Risposta> getRisposteCorrette() { return risposteCorrette; }

    @Override
    public void setRispostaCorretta(Risposta risposta) {
        if (elencoRisposte.contains(risposta)) {
            risposteCorrette.add(risposta);
        }
    }
}
