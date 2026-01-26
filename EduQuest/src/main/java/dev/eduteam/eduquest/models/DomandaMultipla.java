package dev.eduteam.eduquest.models;

import java.util.ArrayList;

public class DomandaMultipla extends  Domanda{

    protected Risposta rispostaCorretta;

    public DomandaMultipla(String testo) {

        tipoDomanda = Type.DOMANDA_MULTIPLA;

        setNumeroRisposte(0);
        elencoRisposte = new ArrayList<Risposta>();
        setTesto(testo);
    }

    public Risposta getRispostaCorretta() { return rispostaCorretta; }

    @Override
    public void setRispostaCorretta(Risposta risposta) { rispostaCorretta = risposta; }
}
