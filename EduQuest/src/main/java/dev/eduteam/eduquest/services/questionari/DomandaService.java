package dev.eduteam.eduquest.services.questionari;

import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.repositories.questionari.DomandaRepository;
import dev.eduteam.eduquest.repositories.questionari.RispostaRepository;

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
        return domande;
    }

    public Domanda getDomandaByIdCompleta(int questionarioID, int domandaID) {
        Domanda d = domandaRepository.getDomandaByID(questionarioID, domandaID);
        return d;
    }

    public Domanda aggiungiDomanda(int questionarioID, Domanda.Type tipoDomanda) {
        Domanda nuovaDomanda = Domanda.createDomandaOfType(tipoDomanda);
        nuovaDomanda.setTesto("Inserisci qui il testo");
        nuovaDomanda.setPunteggio(1);

        return domandaRepository.insertDomanda(nuovaDomanda, questionarioID);
    }

    public boolean rimuoviDomanda(int questionarioID, int domandaID) {
        return domandaRepository.removeDomanda(domandaID, questionarioID);
    }

    public boolean modificaDomanda(int domandaID, String testo, int nuovoPunteggio) {

        if (testo == null) {
            throw new IllegalArgumentException("Il testo della domanda non può essere null");
        }
        Domanda domanda = domandaRepository.getDomandaByID(domandaID);

        if (domanda != null) {
            domanda.setTesto(testo);
            domanda.setPunteggio(nuovoPunteggio);

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
        Risposta rispostaTarget = rispostaRepository.getRispostaByID(rispostaID);
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
