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
    private QuestionarioRepository questionarioRepository;

    @InjectMocks
    private QuestionarioService questionarioService;

    private Questionario questionario;

    @BeforeEach
    void setUp() {
        ArrayList<Domanda> domande = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Domanda d = new Domanda("Domanda " + i);
            d.setID(i + 1);
            domande.add(d);
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
    void testGetQuestionario() {
        when(questionarioRepository.getQuestionarioByID(1)).thenReturn(questionario);

        Questionario result = questionarioService.getQuestionario(1);
        assertNotNull(result);
        assertEquals("Test Questionario", result.getNome());
        verify(questionarioRepository, times(1)).getQuestionarioByID(1);
    }

    @Test
    void testCreaQuestionario() {
        Questionario nuovoQuestionario = new Questionario("", "", new ArrayList<>());
        nuovoQuestionario.setID(2);
        when(questionarioRepository.insertQuestionario(any(Questionario.class))).thenReturn(nuovoQuestionario);

        Questionario result = questionarioService.creaQuestionario();
        assertNotNull(result);
        assertEquals(2, result.getID());
        verify(questionarioRepository, times(1)).insertQuestionario(any(Questionario.class));
    }

    @Test
    void testRimuoviQuestionario() {
        when(questionarioRepository.removeQuestionario(1)).thenReturn(true);

        boolean result = questionarioService.rimuoviQuestionario(1);
        assertTrue(result);
        verify(questionarioRepository, times(1)).removeQuestionario(1);
    }

    @Test
    void testModificaNome() {
        String nuovoNome = "Nuovo Nome";
        when(questionarioRepository.updateQuestionario(questionario)).thenReturn(true);

        boolean result = questionarioService.modificaNome(questionario, nuovoNome);
        assertTrue(result);
        assertEquals(nuovoNome, questionario.getNome());
        verify(questionarioRepository, times(1)).updateQuestionario(questionario);
    }

    @Test
    void testModificaNomeNull() {
        assertThrows(IllegalArgumentException.class,
                () -> questionarioService.modificaNome(questionario, null));
    }

    @Test
    void testModificaDescrizione() {
        String nuovaDescrizione = "Nuova descrizione";
        when(questionarioRepository.updateQuestionario(questionario)).thenReturn(true);

        boolean result = questionarioService.modificaDescrizione(questionario, nuovaDescrizione);
        assertTrue(result);
        assertEquals(nuovaDescrizione, questionario.getDescrizione());
        verify(questionarioRepository, times(1)).updateQuestionario(questionario);
    }

    @Test
    void testModificaDescrizioneNull() {
        assertThrows(IllegalArgumentException.class,
                () -> questionarioService.modificaDescrizione(questionario, null));
    }
}
