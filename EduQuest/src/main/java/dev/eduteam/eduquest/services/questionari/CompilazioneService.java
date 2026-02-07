package dev.eduteam.eduquest.services.questionari;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.models.questionari.Compilazione;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Questionario;
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

    @Autowired
    private DomandaService domandaService;

    public boolean inserisciRispostaComp(int compilazioneID, int domandaID, int rispostaID) {
        Compilazione compilazione = compilazioneRepository.getCompilazioneByID(compilazioneID);
        popolaCompilazione(compilazione);
        Risposta[] risposte = compilazione.getRisposte();
        // mi serve la domanda attuale per sapere quanto vale
        Domanda domandaAttuale = domandaService.getDomandaByIdCompleta(compilazione.getQuestionario().getID(),
                domandaID);
        Risposta risposta = rispostaRepository.getRispostaByID(rispostaID);
        if (!isValida(domandaID, rispostaID))
            return false;

        for (int i = 0; i < risposte.length; i++) {
            if (risposte[i] == null) {
                risposte[i] = risposta;
                // controllo se è corretta e setto il punteggio
                if (risposta.isCorretta()) {
                    compilazione.setPunteggio(compilazione.getPunteggio() + domandaAttuale.getPunteggio());
                }
                if (compilazioneRepository.salvaRisposta(compilazioneID, rispostaID))
                    return compilazioneRepository.aggiornaPunteggio(compilazioneID, compilazione.getPunteggio());
                return false;
            }
        }
        return false;

    }

    public ArrayList<Compilazione> getCompilazioniCompletate(int studenteID) {
        return compilazioneRepository.getCompilazioniStatus(studenteID, true);
    }

    public Compilazione creaCompilazione(int studenteID, int questionarioID) {
        // controllo per vedere se lo studente può fare la compilazione del compitino
        if (!compitinoService.isCompilabileByStudente(studenteID, questionarioID)) {
            return null;
        }

        Studente s = studenteRepository.getStudenteByAccountID(studenteID);
        Questionario q = questionarioRepository.getQuestionarioByID(questionarioID);
        if (s == null || q == null) {
            return null;
        }

        Compilazione c = new Compilazione(s, q);
        return compilazioneRepository.insertCompilazione(c);
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

        Questionario q = questionarioRepository.getQuestionarioByID(c.getQuestionario().getID());
        int maxPunteggio = q.getPunteggioMax();

        if (c == null || studente == null || maxPunteggio == 0)
            return false;

        double punteggioOttenuto = c.getPunteggio();
        // per ora in centesimi - si può cambiare
        double votoNormalizzato = (punteggioOttenuto / maxPunteggio) * 100;

        double mediaAttuale = studente.getMediaPunteggio();
        int totCompilazioni = compilazioneRepository.getCompilazioniStatus(studenteID, true).size();
        double nuovaMedia = ((mediaAttuale * totCompilazioni) + votoNormalizzato) / (totCompilazioni + 1);

        studente.setMediaPunteggio(nuovaMedia);
        c.setCompletato(true);
        return (studenteRepository.updateStudente(studente)
                && compilazioneRepository.upateStatusCompilazione(compilazioneID, true));
    }

    public ArrayList<Compilazione> getCompilazioniInSospeso(int studenteID) {
        return compilazioneRepository.getCompilazioniStatus(studenteID, false);
    }

    public Compilazione riprendiCompilazione(int studenteID, int questionarioID) {
        Compilazione c = compilazioneRepository.getCompilazioneInSospeso(studenteID, questionarioID);
        if (c != null) {
            // devo caricare le risposte salvate fino ad ora
            popolaCompilazione(c);
        }
        return c;
    }
}
