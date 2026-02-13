package dev.eduteam.eduquest.services.questionari;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.models.questionari.Compilazione;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.repositories.accounts.StudenteRepository;
import dev.eduteam.eduquest.repositories.questionari.CompilazioneRepository;
import dev.eduteam.eduquest.repositories.questionari.RispostaRepository;

@Service
public class CompilazioneService {

    @Autowired
    private QuestionarioService questionarioService;

    @Autowired
    private CompilazioneRepository compilazioneRepository;

    @Autowired
    private StudenteRepository studenteRepository;

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
        if (!isValida(domandaID, rispostaID)) {
            return false;
        }

        for (int i = 0; i < risposte.length; i++) {
            if (risposte[i] == null) {
                risposte[i] = risposta;
                // controllo se è corretta e setto il punteggio
                if (risposta.isCorretta()) {
                    compilazione.setPunteggio(compilazione.getPunteggio() + domandaAttuale.getPunteggio());
                }
                if (compilazioneRepository.salvaRisposta(compilazioneID, rispostaID)) {
                    return compilazioneRepository.aggiornaPunteggio(compilazioneID, compilazione.getPunteggio());
                }
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
        Questionario q = questionarioService.getQuestionarioCompleto(questionarioID);
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
        for (int i = 0; i < r2.length; i++) {
            r1[i] = r2[i];
        }
    }

    // Controlla la risposta data sia valida per la corrente domanda
    private boolean isValida(int domandaID, int rispostaID) {
        ArrayList<Risposta> valide = rispostaRepository.getRisposteByDomanda(domandaID);

        for (Risposta r : valide) {
            if (r.getID() == rispostaID) {
                return true;
            }
        }
        return false;
    }

    public boolean chiudiCompilazione(int studenteID, int compilazioneID) {
        Compilazione c = compilazioneRepository.getCompilazioneByID(compilazioneID);
        Studente studente = studenteRepository.getStudenteByAccountID(studenteID);

        Questionario q = questionarioService.getQuestionarioCompleto(c.getQuestionario().getID());
        int maxPunteggio = q.getPunteggioMax();

        if (studente == null || maxPunteggio == 0) {

            System.out.println("Studente: " + studente);
            System.out.println("Punteggio: " + maxPunteggio);
            return false;
        }

        double punteggioOttenuto = c.getPunteggio();
        double votoNormalizzato = (punteggioOttenuto / maxPunteggio) * 100;

        double mediaAttuale = studente.getMediaPunteggio();
        int totCompilazioni = compilazioneRepository.getCompilazioniStatus(studenteID, true).size();
        double nuovaMedia = ((mediaAttuale * totCompilazioni) + votoNormalizzato) / (totCompilazioni + 1);
        studente.setMediaPunteggio(nuovaMedia);

        int eduPointsDaAssegnare = 0;
        if (votoNormalizzato == 100)
            eduPointsDaAssegnare = 15;
        else if (votoNormalizzato > 75)
            eduPointsDaAssegnare = 10;
        else if (votoNormalizzato > 50)
            eduPointsDaAssegnare = 5;

        if (eduPointsDaAssegnare > 0) {
            int eduPointsAttuali = studente.getEduPoints();
            studente.setEduPoints(eduPointsAttuali + eduPointsDaAssegnare);
        }

        c.setCompletato(true);
        return (studenteRepository.updateStudente(studente)
                && compilazioneRepository.updateStatusCompilazione(compilazioneID, true));
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

    public boolean esisteCompilazioneSospesa(int studenteID, int questionarioID) {
        ArrayList<Compilazione> compilazioni = getCompilazioniInSospeso(studenteID);

        for (Compilazione c : compilazioni) {
            if (c.getQuestionario().getID() == questionarioID) {
                return true;
            }
        }
        return false;
    }

    public Compilazione getCompilazioneByID(int compilazioneID) {
        Compilazione c = compilazioneRepository.getCompilazioneByID(compilazioneID);
        if (c != null) {
            // devo caricare le risposte salvate fino ad ora
            popolaCompilazione(c);
        }
        return c;
    }
}
