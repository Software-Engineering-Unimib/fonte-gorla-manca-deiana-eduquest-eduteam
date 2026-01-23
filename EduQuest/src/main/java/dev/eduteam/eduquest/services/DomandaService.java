package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Risposta;
import org.springframework.stereotype.Service;

@Service
public class DomandaService {

    public void modificaTesto(Domanda domanda, String testo) {
        domanda.setTesto(testo);
    }

    public Domanda creaDomanda(String testo, int numeroRisposte) {

        int ID = (int) (Math.random() * 100000); // TEMPORARIO

        Domanda domanda = new Domanda(testo);

        domanda.setID(ID); // TEMPORARIO

        return domanda;
    }

    public void aggiungiRisposta(Domanda domanda, String testo) {

        Risposta risposta = new Risposta(testo);
        domanda.setNumeroRisposte(domanda.getNumeroRisposte() + 1);
        domanda.getElencoRisposte().add(risposta);
    }

    public void rimuoviRisposta(Domanda domanda, Risposta risposta) {

        domanda.setNumeroRisposte(domanda.getNumeroRisposte() - 1);
        domanda.getElencoRisposte().remove(risposta);

    }

    public void setRispostaCorretta(Domanda domanda, Risposta risposta) {
        if (domanda.getElencoRisposte().contains(risposta)) {
            domanda.setRispostaCorretta(risposta);
        }
    }
}
