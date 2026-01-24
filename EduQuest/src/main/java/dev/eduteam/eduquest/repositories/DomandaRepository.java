package dev.eduteam.eduquest.repositories;

import java.util.ArrayList;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Questionario;
import dev.eduteam.eduquest.models.Risposta;

public class DomandaRepository {

    // - METODI DI GET / READ -
    public ArrayList<Domanda> getDomandeByQuestionario(Questionario questionario) {

        return null;
    }

    public ArrayList<Risposta> getRispostaByDomanda(Domanda domanda) {

        return null;
    }
    // - FINE METODI DI GET / READ -

    // - METODI DI ADD / INSERT -
    public boolean addDomanda(Questionario questionario, Domanda domanda) {

        return false;
    }

    public boolean addRisposta(Domanda domanda, Risposta risposta) {

        return false;
    }
    // - FINE METODI DI ADD / INSERT -

    // - METODI DI REMOVE / DELETE -
    public boolean removeDomanda(Questionario questionario, Domanda domanda) {

        return false;
    }

    public boolean removeRisposta(Domanda domanda, Risposta risposta) {

        return false;
    }
    // - FINE METODI DI REMOVE / DELETE -

    // - METODI DI SET / UPDATE -
        public boolean setDomanda(Questionario questionario, Domanda domanda) {

        return false;
    }

    public boolean setRisposta(Domanda domanda, Risposta risposta) {

        return false;
    }
    // - FINE DEI METODI DI SET / UPDATE -

}
