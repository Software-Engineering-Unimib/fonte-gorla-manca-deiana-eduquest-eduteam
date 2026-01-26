package dev.eduteam.eduquest.models;

import java.util.ArrayList;

public class DomandaVeroFalso extends  Domanda{
    private final Risposta vero;
    private final Risposta falso;

    protected Risposta rispostaCorretta;

    public DomandaVeroFalso(String testo) {

        tipoDomanda = Type.DOMANDA_VERO_FALSO;

        setNumeroRisposte(2);
        elencoRisposte = new ArrayList<Risposta>();

        vero = new Risposta("vero");
        falso = new Risposta("falso");

        elencoRisposte.add(vero);
        elencoRisposte.add(falso);
        rispostaCorretta = vero;

        setTesto(testo);
    }

    public Risposta getRispostaCorretta() { return rispostaCorretta; }

    @Override
    public void setRispostaCorretta(Risposta risposta) {
        rispostaCorretta = risposta;
    }
}
