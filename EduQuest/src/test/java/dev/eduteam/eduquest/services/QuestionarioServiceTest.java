package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Questionario;
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

    @InjectMocks
    private QuestionarioService questionarioService;

    private Questionario questionario;

    @BeforeEach
    void setUp() {
        questionario = questionarioService.creaQuestionario("Test Questionario", "Descrizione test", 3);
    }

    @Test
    void testGetQuestionari() {
        ArrayList<Questionario> questionari = questionarioService.getQuestionari();
        assertNotNull(questionari);
        assertFalse(questionari.isEmpty());
        assertTrue(questionari.size() >= 3);
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
        Questionario q1 = questionarioService.creaQuestionario("Quiz 1", "Descrizione 1", 2);
        Questionario q2 = questionarioService.creaQuestionario("Quiz 2", "Descrizione 2", 4);

        assertEquals("Quiz 1", q1.getNome());
        assertEquals("Quiz 2", q2.getNome());
        assertEquals(2, q1.getNumeroDomande());
        assertEquals(4, q2.getNumeroDomande());
    }

    @Test
    void testModificaDescrizione() {
        String nuovaDescrizione = "Nuova descrizione";
        questionarioService.modificaDescrizione(questionario, nuovaDescrizione);
        assertEquals(nuovaDescrizione, questionario.getDescrizione());
    }

    @Test
    void testAggiungiDomanda() {
        Domanda domandaMock = new Domanda("Domanda aggiunta");
        when(domandaService.creaDomanda("Domanda aggiunta", 4)).thenReturn(domandaMock);

        int numeroDomandeIniziale = questionario.getNumeroDomande();
        questionarioService.aggiungiDomanda(questionario, "Domanda aggiunta", 4);

        assertEquals(numeroDomandeIniziale + 1, questionario.getNumeroDomande());
        assertTrue(questionario.getElencoDomande().contains(domandaMock));
        verify(domandaService, times(1)).creaDomanda("Domanda aggiunta", 4);
    }

    @Test
    void testRimuoviDomanda() {
        Domanda domandaDaRimuovere = questionario.getElencoDomande().get(0);
        int numeroDomandeIniziale = questionario.getNumeroDomande();

        questionarioService.rimuoviDomanda(questionario, domandaDaRimuovere);

        assertEquals(numeroDomandeIniziale - 1, questionario.getNumeroDomande());
        assertFalse(questionario.getElencoDomande().contains(domandaDaRimuovere));
    }

    @Test
    void testAggiungiERimuoviDomande() {
        Domanda domanda1 = new Domanda("Domanda 1");
        Domanda domanda2 = new Domanda("Domanda 2");

        when(domandaService.creaDomanda("Domanda 1", 3)).thenReturn(domanda1);
        when(domandaService.creaDomanda("Domanda 2", 3)).thenReturn(domanda2);

        int numeroDomandeIniziale = questionario.getNumeroDomande();

        // Aggiungi due domande
        questionarioService.aggiungiDomanda(questionario, "Domanda 1", 3);
        questionarioService.aggiungiDomanda(questionario, "Domanda 2", 3);

        assertEquals(numeroDomandeIniziale + 2, questionario.getNumeroDomande());

        // Rimuovi una domanda
        questionarioService.rimuoviDomanda(questionario, domanda1);
        assertEquals(numeroDomandeIniziale + 1, questionario.getNumeroDomande());

        assertTrue(questionario.getElencoDomande().contains(domanda2));
        assertFalse(questionario.getElencoDomande().contains(domanda1));
    }

    @Test
    void testCreaQuestionarioConZeroDomande() {
        Questionario questionarioZero = questionarioService.creaQuestionario("Quiz Vuoto", "Senza domande", 0);
        assertEquals(0, questionarioZero.getNumeroDomande());
        assertTrue(questionarioZero.getElencoDomande().isEmpty());
    }

    @Test
    void testModificaDescrizioneNull() {
        assertThrows(IllegalArgumentException.class,
                () -> questionarioService.modificaDescrizione(questionario, null));
    }
}
