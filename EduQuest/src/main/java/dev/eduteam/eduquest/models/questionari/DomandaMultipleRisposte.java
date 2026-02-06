package dev.eduteam.eduquest.models.questionari;

import java.util.ArrayList;

// Domanda a risposta multipla con più risposte corrette
public class DomandaMultipleRisposte extends Domanda {

    public DomandaMultipleRisposte(String testo) {

        tipoDomanda = Type.DOMANDA_MULTIPLE_RISPOSTE;

        setNumeroRisposte(0);
        setTesto(testo);
    }


}
