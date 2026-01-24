package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Questionario;
import dev.eduteam.eduquest.repositories.QuestionarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionarioServiceTest {

    @Mock
    private DomandaService domandaService;

    @Mock
    private QuestionarioRepository questionarioRepository;

    @InjectMocks
    private QuestionarioService questionarioService;

    private Questionario questionario;

    @BeforeEach
    void setUp() {
        ArrayList<Domanda> domande = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            domande.add(new Domanda("Domanda " + i));
        }
        questionario = new Questionario("Test Questionario", "Descrizione test", domande);
        questionario.setID(1);
    }

    @Test
    void testGetQuestionari() {
        ArrayList<Questionario> questionari = new ArrayList<>();
        questionari.add(questionario);
        when(questionarioRepository.getQuestionari()).thenReturn(questionari);

        ArrayList<Questionario> result = questionarioService.getQuestionari();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(questionarioRepository, times(1)).getQuestionari();
    }

    @Test
    void testCreaQuestionario() {
        assertNotNull(questionario);
        assertEquals("Test Questionario", questionario.getNome());
        assertEquals("Descrizione test", questionario.getDescrizione());
        assertEquals(3, questionario.getNumeroDomande());
        assertNotNull(questionario.getID());
        assertTrue(questionario.getID() >= 0);
    }

    @Test
    void testCreaQuestionarioDiversi() {
        ArrayList<Domanda> domande1 = new ArrayList<>();
        domande1.add(new Domanda("Domanda 1"));
        domande1.add(new Domanda("Domanda 2"));
        Questionario q1 = new Questionario("Quiz 1", "Descrizione 1", domande1);

        ArrayList<Domanda> domande2 = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            domande2.add(new Domanda("Domanda " + i));
        }
        Questionario q2 = new Questionario("Quiz 2", "Descrizione 2", domande2);

        assertEquals("Quiz 1", q1.getNome());
        assertEquals("Quiz 2", q2.getNome());
        assertEquals(2, q1.getNumeroDomande());
        assertEquals(4, q2.getNumeroDomande());
    }

    @Test
    void testModificaDescrizione() {
        String nuovaDescrizione = "Nuova descrizione";
        when(questionarioRepository.updateQuestionario(questionario)).thenReturn(true);

        questionarioService.modificaDescrizione(questionario, nuovaDescrizione);
        assertEquals(nuovaDescrizione, questionario.getDescrizione());
        verify(questionarioRepository, times(1)).updateQuestionario(questionario);
    }

    @Test
    void testAggiungiDomanda() {
        Domanda domandaMock = new Domanda("Domanda da aggiungere");
        when(domandaService.creaDomanda()).thenReturn(domandaMock);
        when(questionarioRepository.updateQuestionario(questionario)).thenReturn(true);

        int numeroDomandeIniziale = questionario.getNumeroDomande();
        questionarioService.aggiungiDomanda(questionario);

        assertEquals(numeroDomandeIniziale + 1, questionario.getNumeroDomande());
        assertTrue(questionario.getElencoDomande().contains(domandaMock));
        verify(domandaService, times(1)).creaDomanda();
        verify(questionarioRepository, times(1)).updateQuestionario(questionario);
    }

    @Test
    void testRimuoviDomanda() {
        Domanda domandaDaRimuovere = questionario.getElencoDomande().get(0);
        domandaDaRimuovere.setID(10);
        int numeroDomandeIniziale = questionario.getNumeroDomande();

        when(questionarioRepository.getQuestionarioByID(questionario.getID())).thenReturn(questionario);
        when(questionarioRepository.updateQuestionario(questionario)).thenReturn(true);

        questionarioService.rimuoviDomanda(questionario.getID(), domandaDaRimuovere.getID());

        assertEquals(numeroDomandeIniziale - 1, questionario.getNumeroDomande());
        assertFalse(questionario.getElencoDomande().contains(domandaDaRimuovere));
    }

    @Test
    void testAggiungiERimuoviDomande() {
        Domanda domanda1 = new Domanda("Domanda 1");
        domanda1.setID(101);
        Domanda domanda2 = new Domanda("Domanda 2");
        domanda2.setID(102);

        when(domandaService.creaDomanda()).thenReturn(domanda1).thenReturn(domanda2);
        when(questionarioRepository.updateQuestionario(questionario)).thenReturn(true);
        when(questionarioRepository.getQuestionarioByID(questionario.getID())).thenReturn(questionario);

        int numeroDomandeIniziale = questionario.getNumeroDomande();

        // Aggiungi due domande
        questionarioService.aggiungiDomanda(questionario);
        questionarioService.aggiungiDomanda(questionario);

        assertEquals(numeroDomandeIniziale + 2, questionario.getNumeroDomande());

        // Rimuovi una domanda
        questionarioService.rimuoviDomanda(questionario.getID(), domanda1.getID());
        assertEquals(numeroDomandeIniziale + 1, questionario.getNumeroDomande());

        assertTrue(questionario.getElencoDomande().contains(domanda2));
        assertFalse(questionario.getElencoDomande().contains(domanda1));
    }

    @Test
    void testCreaQuestionarioConZeroDomande() {
        Questionario questionarioZero = new Questionario("Quiz Vuoto", "Senza domande", new ArrayList<>());
        assertEquals(0, questionarioZero.getNumeroDomande());
        assertTrue(questionarioZero.getElencoDomande().isEmpty());
    }

    @Test
    void testModificaDescrizioneNull() {
        assertThrows(IllegalArgumentException.class,
                () -> questionarioService.modificaDescrizione(questionario, null));
    }
}
