package dev.eduteam.eduquest.models;

import java.util.ArrayList;

public class DomandaVeroFalso extends Domanda {
    // private final Risposta vero;
    // private final Risposta falso;

    protected Risposta rispostaCorretta;

    public DomandaVeroFalso(String testo) {
        tipoDomanda = Type.DOMANDA_VERO_FALSO;
        setNumeroRisposte(2);
        setTesto(testo);
    }

    public Risposta getRispostaCorretta() {
        return rispostaCorretta;
    }

    @Override
    public void setRispostaCorretta(Risposta risposta) {
        rispostaCorretta = risposta;
    }

    // Sovrascitto il setter pe rimpedire modifiche errate
    @Override
    public void setNumeroRisposte(int numeroRisposte) {
        super.setNumeroRisposte(2);
    }

}
