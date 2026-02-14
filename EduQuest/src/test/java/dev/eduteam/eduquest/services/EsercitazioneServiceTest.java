package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Esercitazione;
import dev.eduteam.eduquest.models.questionari.Feedback;
import dev.eduteam.eduquest.models.questionari.Questionario.Difficulty;
import dev.eduteam.eduquest.repositories.accounts.DocenteRepository;
import dev.eduteam.eduquest.repositories.questionari.DomandaRepository;
import dev.eduteam.eduquest.repositories.questionari.EsercitazioneRepository;
import dev.eduteam.eduquest.repositories.questionari.FeedbackRepository;
import dev.eduteam.eduquest.services.questionari.EsercitazioneService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EsercitazioneServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private DomandaRepository domandaRepository;

    @Mock
    private EsercitazioneRepository esercitazioneRepository;

    @Mock
    private DocenteRepository docenteRepository;

    @InjectMocks
    private EsercitazioneService esercitazioneService;

    private Esercitazione esercitazione;
    private Domanda domanda;
    private Feedback feedback;
    private Docente docente;

    @BeforeEach
    void setUp() {
        docente = new Docente();
        docente.setAccountID(1);
        docente.setInsegnamento("Informatica");

        esercitazione = new Esercitazione("Esercitazione 1", "Descrizione esercitazione", new ArrayList<>(), docente,
                Difficulty.Medio);
        esercitazione.setID(1);
        esercitazione.setNoteDidattiche("Note importanti");

        domanda = Domanda.createDomandaOfType(Domanda.Type.DOMANDA_MULTIPLA);
        domanda.setTesto("Qual è la capitale dell'Italia?");
        domanda.setID(1);

        feedback = new Feedback(domanda, "Risposta corretta!");
        feedback.setID(1);
    }

    @Test
    void testCreaEsercitazioneSuccess() {
        int docenteID = 1;
        Difficulty difficolta = Difficulty.Difficile;
        String noteDidattiche = "Note per gli studenti";

        when(docenteRepository.getDocenteByAccountID(docenteID)).thenReturn(docente);
        when(esercitazioneRepository.insertEsercitazione(any())).thenAnswer(i -> i.getArguments()[0]);

        Esercitazione result = esercitazioneService.creaEsercitazione(docenteID, difficolta, noteDidattiche);

        assertNotNull(result);
        assertEquals("Nuova Esercitazione", result.getNome());
        assertEquals(noteDidattiche, result.getNoteDidattiche());
        assertEquals(difficolta, result.getLivelloDifficulty());
        verify(docenteRepository, times(1)).getDocenteByAccountID(docenteID);
        verify(esercitazioneRepository, times(1)).insertEsercitazione(any(Esercitazione.class));
    }

    @Test
    void testCreaEsercitazioneDocenteInesistente() {
        int docenteID = 99;
        Difficulty difficolta = Difficulty.Facile;
        String noteDidattiche = "Note didattiche";

        when(docenteRepository.getDocenteByAccountID(docenteID)).thenReturn(null);

        Esercitazione result = esercitazioneService.creaEsercitazione(docenteID, difficolta, noteDidattiche);

        assertNull(result);
        verify(docenteRepository, times(1)).getDocenteByAccountID(docenteID);
        verify(esercitazioneRepository, never()).insertEsercitazione(any(Esercitazione.class));
    }

    @Test
    void testCreaEsercitazioneConNoteNull() {
        int docenteID = 1;
        Difficulty difficolta = Difficulty.Medio;
        String noteDidattiche = null;

        Esercitazione esercitazioneAttesa = new Esercitazione("Esercitazione 1", "Descrizione esercitazione",
                new ArrayList<>(), docente, difficolta);
        esercitazioneAttesa.setID(1);

        when(docenteRepository.getDocenteByAccountID(docenteID)).thenReturn(docente);
        when(esercitazioneRepository.insertEsercitazione(any(Esercitazione.class))).thenReturn(esercitazioneAttesa);

        Esercitazione result = esercitazioneService.creaEsercitazione(docenteID, difficolta, noteDidattiche);

        assertNotNull(result);
        verify(docenteRepository).getDocenteByAccountID(docenteID);
        verify(esercitazioneRepository).insertEsercitazione(any(Esercitazione.class));
    }

    @Test
    void testAggiungiDomandaConFeedbackSuccess() {
        int questionarioID = 1;
        Domanda.Type tipoDomanda = Domanda.Type.DOMANDA_MULTIPLA;
        String feedbackTesto = "Questa è una risposta corretta";

        when(domandaRepository.insertDomanda(any(Domanda.class), eq(questionarioID)))
                .thenAnswer(invocation -> (Domanda) invocation.getArgument(0));
        when(feedbackRepository.insertFeedback(any(Feedback.class))).thenReturn(feedback);

        Domanda result = esercitazioneService.aggiungiDomandaConFeedback(questionarioID, tipoDomanda, feedbackTesto);

        assertNotNull(result);
        assertEquals("Inserisci qui il testo", result.getTesto());
        verify(domandaRepository, times(1)).insertDomanda(any(Domanda.class), eq(questionarioID));
        verify(feedbackRepository, times(1)).insertFeedback(any(Feedback.class));
    }

    @Test
    void testAggiungiDomandaConFeedbackNullo() {
        int questionarioID = 1;
        Domanda.Type tipoDomanda = Domanda.Type.DOMANDA_MULTIPLE_RISPOSTE;
        String feedbackTesto = null;

        when(domandaRepository.insertDomanda(any(Domanda.class), eq(questionarioID))).thenReturn(domanda);

        Domanda result = esercitazioneService.aggiungiDomandaConFeedback(questionarioID, tipoDomanda, feedbackTesto);

        assertNotNull(result);
        verify(domandaRepository, times(1)).insertDomanda(any(Domanda.class), eq(questionarioID));
        verify(feedbackRepository, never()).insertFeedback(any(Feedback.class));
    }

    @Test
    void testAggiungiDomandaConFeedbackVuoto() {
        int questionarioID = 1;
        Domanda.Type tipoDomanda = Domanda.Type.DOMANDA_VERO_FALSO;
        String feedbackTesto = "   ";

        when(domandaRepository.insertDomanda(any(Domanda.class), eq(questionarioID))).thenReturn(domanda);

        Domanda result = esercitazioneService.aggiungiDomandaConFeedback(questionarioID, tipoDomanda, feedbackTesto);

        assertNotNull(result);
        verify(domandaRepository, times(1)).insertDomanda(any(Domanda.class), eq(questionarioID));
        verify(feedbackRepository, never()).insertFeedback(any(Feedback.class));
    }

    @Test
    void testAggiungiDomandaDomandaNulla() {
        int questionarioID = 1;
        Domanda.Type tipoDomanda = Domanda.Type.DOMANDA_MULTIPLA;
        String feedbackTesto = "Feedback";

        when(domandaRepository.insertDomanda(any(Domanda.class), eq(questionarioID))).thenReturn(null);

        Domanda result = esercitazioneService.aggiungiDomandaConFeedback(questionarioID, tipoDomanda, feedbackTesto);

        assertNull(result);
        verify(domandaRepository, times(1)).insertDomanda(any(Domanda.class), eq(questionarioID));
        verify(feedbackRepository, never()).insertFeedback(any(Feedback.class));
    }

}
