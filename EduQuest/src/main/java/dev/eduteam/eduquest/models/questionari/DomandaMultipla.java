package dev.eduteam.eduquest.models.questionari;

//Domanda a risposta multipla con una risposta corretta
public class DomandaMultipla extends Domanda {

    protected Risposta rispostaCorretta;

    public DomandaMultipla(String testo) {

        tipoDomanda = Type.DOMANDA_MULTIPLA;

        setNumeroRisposte(0);
        setTesto(testo);
    }
}
