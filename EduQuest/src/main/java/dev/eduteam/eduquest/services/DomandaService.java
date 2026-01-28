package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Questionario;
import dev.eduteam.eduquest.models.Risposta;
import dev.eduteam.eduquest.repositories.DomandaRepository;
import dev.eduteam.eduquest.repositories.RispostaRepository;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomandaService {

    @Autowired
    private DomandaRepository domandaRepository;

    @Autowired
    private RispostaRepository rispostaRepository;

    public ArrayList<Domanda> getDomandeComplete(int questionarioID) {
        ArrayList<Domanda> domande = domandaRepository.getDomandeByQuestionario(questionarioID);
        for (Domanda d : domande) {
            popolaRisposte(d);
        }
        return domande;
    }

    public Domanda getDomandaByIdCompleta(int questionarioID, int domandaID) {
        Domanda d = domandaRepository.getDomandaByID(questionarioID, domandaID);
        if (d != null) {
            popolaRisposte(d);
        }
        return d;
    }

    // Metodo per popolare una domanda con le sue risposte
    private void popolaRisposte(Domanda d) {
        ArrayList<Risposta> risposte = rispostaRepository.getRisposteByDomanda(d.getID());
        for (Risposta r : risposte) {
            d.addRisposta(r);
            if (r.isCorretta()) {
                // Il polimorfismo implementato gestirà se aggiungere la risposta corretta a una
                // lista o a un singolo campo
                d.setRispostaCorretta(r);
            }
        }
    }

    public Domanda aggiungiDomanda(int questionarioID, Domanda.Type tipoDomanda) {
        Domanda nuovaDomanda = Domanda.createDomandaOfType(tipoDomanda);
        nuovaDomanda.setTesto("Inserisci qui il testo");
        return domandaRepository.insertDomanda(nuovaDomanda, questionarioID);
    }

    public boolean rimuoviDomanda(int questionarioID, int domandaID) {
        return domandaRepository.removeDomanda(domandaID, questionarioID);
    }

    public boolean modificaTesto(int domandaID, String testo) {
        Domanda domanda = domandaRepository.getDomandaByID(domandaID);

        if (domanda != null) {
            domanda.setTesto(testo);
            return domandaRepository.updateDomanda(domanda);
        }
        return false;
    }

    // POSSIBILE AGGIUNTA FUTURA DI UN METODO MODIFICA IMMAGINE/AUDIO PER DOMANDE
    // MULTIMEDIALI

    public boolean setRispostaCorretta(int domandaID, int rispostaID) {
        Domanda domanda = domandaRepository.getDomandaByID(domandaID);
        if (domanda == null)
            return false;
        Risposta rispostaTarget = rispostaRepository.getRispostaByID(domandaID, rispostaID);
        if (rispostaTarget == null)
            return false;

        boolean successo = true;
        // Per domande a risposta singola garantiamo esclusività della risposta corretta
        if (domanda.getTipoDomanda() != Domanda.Type.DOMANDA_MULTIPLE_RISPOSTE) {
            ArrayList<Risposta> elencoRisposte = rispostaRepository.getRisposteByDomanda(domanda.getID());
            for (Risposta r : elencoRisposte) {
                boolean correttezza = r.getID() == rispostaID;

                if (r.isCorretta() != correttezza) {
                    r.setCorretta(correttezza);
                    if (!rispostaRepository.updateRisposta(r))
                        successo = false;
                }
            }
            return successo;
        } else {
            // Se a risposta multiple, cambiamo solo quella passata al metodo
            rispostaTarget.setCorretta(true);
            successo = rispostaRepository.updateRisposta(rispostaTarget);
        }
        return successo;
    }

}
