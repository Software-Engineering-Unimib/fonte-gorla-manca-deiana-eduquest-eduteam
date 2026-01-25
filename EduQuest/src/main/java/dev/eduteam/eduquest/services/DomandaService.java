package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Questionario;
import dev.eduteam.eduquest.models.Risposta;
import dev.eduteam.eduquest.repositories.DomandaRepository;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomandaService {

    @Autowired
    private DomandaRepository domandaRepository;

    /*
     * Non penso serva - creazione in repository
     * public Domanda creaDomanda() {
     * int ID = (int) (Math.random() * 100000); // TEMPORARIO
     * Domanda domanda = new Domanda("");
     * domanda.setID(ID); // TEMPORARIO
     * return domanda;
     * }
     */

    public ArrayList<Domanda> getDomande(int questionarioID) {
        return domandaRepository.getDomandeByQuestionario(questionarioID);
    }

    public Domanda getDomandaById(int questionarioID, int domandaID) {
        return domandaRepository.getDomandaByID(questionarioID, domandaID);
    }

    public Domanda aggiungiDomanda(int questionarioID) {
        Domanda nuovaDomanda = new Domanda("");
        return domandaRepository.insertDomanda(nuovaDomanda, questionarioID);
    }

    public boolean rimuoviDomanda(int questionarioID, int domandaID) {
        return domandaRepository.removeDomanda(domandaID, questionarioID);
    }

    public boolean modificaTesto(Domanda domanda, String testo) {
        domanda.setTesto(testo);
        return domandaRepository.updateDomanda(domanda);
    }

    public boolean modificaRispostaCorretta(Domanda domanda, Risposta risposta) {
        if (domanda.getElencoRisposte().contains(risposta)) {
            domanda.setRispostaCorretta(risposta);
        }
        return domandaRepository.updateDomanda(domanda);
    }

}
