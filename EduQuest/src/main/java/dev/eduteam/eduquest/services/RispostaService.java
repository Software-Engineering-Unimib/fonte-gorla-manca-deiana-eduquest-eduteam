package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Risposta;
import dev.eduteam.eduquest.repositories.RispostaRepository;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RispostaService {

    @Autowired
    private RispostaRepository rispostaRepository;

    /*
     * Non penso serva - creazione in repository
     * public Risposta creaRisposta() {
     * int ID = (int) (Math.random() * 100000); // TEMPORARIO
     * Risposta risposta = new Risposta("");
     * risposta.setID(ID); // TEMPORARIO
     * return risposta;
     * }
     */

    public ArrayList<Risposta> getDomande(int domandaID) {
        return rispostaRepository.getRisposteByDomanda(domandaID);
    }

    public Risposta getRispostaById(int domandaID, int rispostaID) {
        return rispostaRepository.getRispostaByID(domandaID, rispostaID);
    }

    public Risposta aggiungiRisposta(int questionarioID, int domandaID) {
        Risposta nuovaRisposta = new Risposta("");
        return rispostaRepository.insertRisposta(nuovaRisposta, domandaID);
    }

    public boolean rimuoviRisposta(int questionario, int domandaID, int rispostaID) {
        return rispostaRepository.removeRisposta(domandaID, rispostaID);
    }

    public boolean modificaTesto(Risposta risposta, String testo) {
        risposta.setTesto(testo);
        return rispostaRepository.updateRisposta(risposta);
    }
}
