package dev.eduteam.eduquest.services.questionari;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.models.questionari.Compilazione;
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

    @Autowired
    private CompitinoService compitinoService;

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
                // controllo se è corretta e setto il punteggio
                if (risposta.isCorretta()) {
                    compilazione.setPunteggio(compilazione.getPunteggio() + 1);
                }
                if (compilazioneRepository.salvaRisposta(compilazioneID, rispostaID))
                    return compilazioneRepository.aggiornaPunteggio(compilazioneID, compilazione.getPunteggio());
                return false;
            }
        }
        return false;

    }

    public ArrayList<Compilazione> getCompilazioniCompletate(int studenteID) {
        return compilazioneRepository.getCompilazioniCompletate(studenteID);
    }

    public Compilazione creaCompilazione(int studenteID, int questionarioID) {
        // controllo per vedere se lo studente può fare la compilazione del compitino
        if (!compitinoService.isCompilabileByStudente(studenteID, questionarioID)) {
            return null;
        }
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

    public boolean chiudiCompilazione(int studenteID, int compilazioneID) {
        Compilazione c = compilazioneRepository.getCompilazioneByID(compilazioneID);
        Studente studente = studenteRepository.getStudenteByAccountID(studenteID);

        double mediaAttuale = studente.getMediaPunteggio();
        int totCompilazioni = compilazioneRepository.getCompilazioniCompletate(studenteID).size();
        int punteggioCompilazione = c.getPunteggio();
        double nuovaMedia = ((mediaAttuale * totCompilazioni) + punteggioCompilazione) / (totCompilazioni + 1);

        studente.setMediaPunteggio(nuovaMedia);
        c.setCompletato(true);
        return (studenteRepository.updateStudente(studente)
                && compilazioneRepository.upateStatusCompilazione(compilazioneID, true));
    }

    public Compilazione riprendiCompilazione(int studenteID, int questionarioID) {
        Compilazione c = compilazioneRepository.getCompilazioneInCorso(studenteID, questionarioID);
        if (c != null) {
            // devo caricare le risposte salvate fino ad ora
            popolaCompilazione(c);
        }
        return c;
    }
}
