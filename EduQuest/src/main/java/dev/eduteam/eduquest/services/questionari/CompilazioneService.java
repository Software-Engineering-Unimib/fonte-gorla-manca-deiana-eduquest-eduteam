package dev.eduteam.eduquest.services.questionari;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.eduteam.eduquest.models.questionari.Compilazione;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.repositories.accounts.StudenteRepository;
import dev.eduteam.eduquest.repositories.questionari.CompilazioneRepository;
import dev.eduteam.eduquest.repositories.questionari.QuestionarioRepository;
import dev.eduteam.eduquest.repositories.questionari.RispostaRepository;

@Service
public class CompilazioneService {

    @Autowired
    private CompilazioneRepository compilazioneRepository;

    @Autowired
    private StudenteRepository studenteRepository;

    @Autowired
    private QuestionarioRepository questionarioRepository;

    @Autowired
    private RispostaRepository rispostaRepository;

    public boolean inserisciRispostaComp(int compilazioneID, int domandaID, int rispostaID) {
        Compilazione compilazione = compilazioneRepository.getCompilazioneByID(compilazioneID);
        popolaCompilazione(compilazione);
        Risposta[] risposte = compilazione.getRisposte();
        Risposta risposta = rispostaRepository.getRispostaByID(rispostaID);
        if (!isValida(domandaID, rispostaID))
            return false;

        for (int i = 0; i < risposte.length; i++) {
            if (risposte[i] == null) {
                risposte[i] = risposta;
                if (compilazioneRepository.salvaRisposta(compilazioneID, rispostaID))
                    return true;
                return false;
            }
        }
        return false;

    }

    public ArrayList<Compilazione> getCompilazioniCompletate(int studenteID) {
        return compilazioneRepository.getCompilazioniCompletate(studenteID);
    }

    public Compilazione creaCompilazione(int studenteID, int questionarioID) {
        Compilazione compilazione = new Compilazione(studenteRepository.getStudenteByAccountID(studenteID),
                questionarioRepository.getQuestionarioByID(questionarioID));
        return compilazioneRepository.insertCompilazione(compilazione);
    }

    // Metodi private

    private void popolaCompilazione(Compilazione c) {
        Risposta[] r1 = c.getRisposte();
        Risposta[] r2 = compilazioneRepository.getRisposteCompilazione(c.getID(), c.getNumeroDomande());
        for (int i = 0; i < r1.length; i++) {
            r1[i] = r2[i];
        }
    }

    // Controlla la risposta data sia valida per la corrente domanda
    private boolean isValida(int domandaID, int rispostaID) {
        List<Risposta> valide = rispostaRepository.getRisposteByDomanda(domandaID);
        Risposta risposta = rispostaRepository.getRispostaByID(rispostaID);
        if (valide.contains(risposta))
            return true;
        return false;
    }

}
