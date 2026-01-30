package dev.eduteam.eduquest.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.models.questionari.Compilazione;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.repositories.accounts.StudenteRepository;
import dev.eduteam.eduquest.repositories.questionari.CompilazioneRepository;
import dev.eduteam.eduquest.repositories.questionari.QuestionarioRepository;
import dev.eduteam.eduquest.repositories.questionari.RispostaRepository;
import dev.eduteam.eduquest.services.questionari.CompilazioneService;

class CompilazioneServiceTest {

    @Mock
    private CompilazioneRepository compilazioneRepository;

    @Mock
    private StudenteRepository studenteRepository;

    @Mock
    private QuestionarioRepository questionarioRepository;

    @Mock
    private RispostaRepository rispostaRepository;

    @InjectMocks
    private CompilazioneService compilazioneService;

    private Compilazione compilazione;
    private Studente studente;
    private Questionario questionario;
    private Risposta risposta;
    private Docente docente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        studente = new Studente("Mario", "Rossi", "mrossi", "mario@email.com", "password123");
        studente.setAccountID(1);

        docente = new Docente("Luigi", "Bianchi", "lbianchi", "luigi@email.com", "password456");

        ArrayList<Domanda> domande = new ArrayList<>();
        // Aggiungi 3 domande fittizie per testare
        Domanda d1 = mock(Domanda.class);
        Domanda d2 = mock(Domanda.class);
        Domanda d3 = mock(Domanda.class);
        domande.add(d1);
        domande.add(d2);
        domande.add(d3);
        questionario = new Questionario("Test Questionario", "Descrizione", domande, docente);
        questionario.setID(1);

        compilazione = new Compilazione(studente, questionario);
        compilazione.setID(1);

        risposta = new Risposta("");
        risposta.setID(1);
    }

    @Test
    void testCreaCompilazioneSuccess() {
        when(studenteRepository.getStudenteByAccountID(1))
                .thenReturn(studente);
        when(questionarioRepository.getQuestionarioByID(1))
                .thenReturn(questionario);
        when(compilazioneRepository.insertCompilazione(any(Compilazione.class)))
                .thenReturn(compilazione);

        Compilazione risultato = compilazioneService.creaCompilazione(1, 1);

        assertNotNull(risultato);
        assertEquals(studente, risultato.getStudente());
        assertEquals(questionario, risultato.getQuestionario());
        verify(compilazioneRepository, times(1)).insertCompilazione(any(Compilazione.class));
    }

    @Test
    void testInserisciRispostaCompSuccess() {
        Risposta[] risposte = new Risposta[3];

        ArrayList<Risposta> risposteValide = new ArrayList<>();
        risposteValide.add(risposta);

        when(compilazioneRepository.getCompilazioneByID(1))
                .thenReturn(compilazione);
        when(compilazioneRepository.getRisposteCompilazione(1, 3))
                .thenReturn(risposte);
        when(rispostaRepository.getRispostaByID(1))
                .thenReturn(risposta);
        when(rispostaRepository.getRisposteByDomanda(1))
                .thenReturn(risposteValide);
        when(compilazioneRepository.salvaRisposta(1, 1))
                .thenReturn(true);

        boolean risultato = compilazioneService.inserisciRispostaComp(1, 1, 1);

        assertTrue(risultato);
        verify(compilazioneRepository, times(1)).salvaRisposta(1, 1);
    }

    @Test
    void testInserisciRispostaCompRispostaInvalida() {
        when(compilazioneRepository.getCompilazioneByID(1))
                .thenReturn(compilazione);
        when(compilazioneRepository.getRisposteCompilazione(1, 3))
                .thenReturn(new Risposta[3]);
        when(rispostaRepository.getRispostaByID(1))
                .thenReturn(risposta);
        when(rispostaRepository.getRisposteByDomanda(1))
                .thenReturn(new ArrayList<>());

        boolean risultato = compilazioneService.inserisciRispostaComp(1, 1, 1);

        assertFalse(risultato);
        verify(compilazioneRepository, never()).salvaRisposta(anyInt(), anyInt());
    }

    @Test
    void testInserisciRispostaCompSalvataggioFallito() {
        Risposta[] risposte = new Risposta[3];

        ArrayList<Risposta> risposteValide = new ArrayList<>();
        risposteValide.add(risposta);

        when(compilazioneRepository.getCompilazioneByID(1))
                .thenReturn(compilazione);
        when(compilazioneRepository.getRisposteCompilazione(1, 3))
                .thenReturn(risposte);
        when(rispostaRepository.getRispostaByID(1))
                .thenReturn(risposta);
        when(rispostaRepository.getRisposteByDomanda(1))
                .thenReturn(risposteValide);
        when(compilazioneRepository.salvaRisposta(1, 1))
                .thenReturn(false);

        boolean risultato = compilazioneService.inserisciRispostaComp(1, 1, 1);

        assertFalse(risultato);
    }

    @Test
    void testGetCompilazioniCompletate() {
        ArrayList<Compilazione> compilazioni = new ArrayList<>();
        compilazioni.add(compilazione);

        when(compilazioneRepository.getCompilazioniCompletate(1))
                .thenReturn(compilazioni);

        ArrayList<Compilazione> risultato = compilazioneService.getCompilazioniCompletate(1);

        assertNotNull(risultato);
        assertEquals(1, risultato.size());
        assertEquals(compilazione, risultato.get(0));
    }

    @Test
    void testGetCompilazioniCompletateEmpty() {
        when(compilazioneRepository.getCompilazioniCompletate(1))
                .thenReturn(new ArrayList<>());

        ArrayList<Compilazione> risultato = compilazioneService.getCompilazioniCompletate(1);

        assertNotNull(risultato);
        assertTrue(risultato.isEmpty());
    }
}
