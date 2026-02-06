package dev.eduteam.eduquest.models.questionari;

public class DomandaVeroFalso extends Domanda {
    // private final Risposta vero;
    // private final Risposta falso;

    public DomandaVeroFalso(String testo) {
        tipoDomanda = Type.DOMANDA_VERO_FALSO;
        setNumeroRisposte(2);
        setTesto(testo);
    }

}
