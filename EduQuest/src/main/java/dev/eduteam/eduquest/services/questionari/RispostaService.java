package dev.eduteam.eduquest.services.questionari;

import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.repositories.questionari.DomandaRepository;
import dev.eduteam.eduquest.repositories.questionari.RispostaRepository;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RispostaService {

    @Autowired
    private RispostaRepository rispostaRepository;

    @Autowired
    private DomandaRepository domandaRepository;

    public ArrayList<Risposta> getRisposteByDomanda(int domandaID) {
        return rispostaRepository.getRisposteByDomanda(domandaID);
    }

    public Risposta getRispostaById(int rispostaID) {
        return rispostaRepository.getRispostaByID(rispostaID);
    }

    public Risposta aggiungiRisposta(int domandaID) {
        Risposta nuovaRisposta = new Risposta("Nuova Risposta");
        nuovaRisposta.setCorretta(false);
        return rispostaRepository.insertRisposta(nuovaRisposta, domandaID);
    }

    public ArrayList<Risposta> aggiungiRisposteVeroFalso(int domandaID) {

        ArrayList<Risposta> risposte = new ArrayList<Risposta>();

        Risposta vero = new Risposta("Vero");
        Risposta falso = new Risposta("Falso");
        vero.setCorretta(true);
        falso.setCorretta(false);
        risposte.add(rispostaRepository.insertRisposta(vero, domandaID));
        risposte.add(rispostaRepository.insertRisposta(falso, domandaID));
        return risposte;
    }

    public boolean rimuoviRisposta(int domandaID, int rispostaID) {
        return rispostaRepository.removeRisposta(domandaID, rispostaID);
    }

    public boolean modificaRisposta(Risposta risposta, String testo, boolean corretta) {
        if (testo == null)
            throw new IllegalArgumentException("Il testo non può essere null");
        risposta.setTesto(testo);
        risposta.setCorretta(corretta);

        Domanda domanda = domandaRepository.getDomandaByRisposta(risposta.getID());

        if (domanda.getTipoDomanda() != Domanda.Type.DOMANDA_MULTIPLE_RISPOSTE && corretta) {

            for (Risposta r : domanda.getElencoRisposte()) {
                r.setCorretta(false);
                rispostaRepository.updateRisposta(r);
            }
        }

        return rispostaRepository.updateRisposta(risposta);
    }
}
