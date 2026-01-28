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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionarioServiceTest {

    @Mock
    private QuestionarioRepository questionarioRepository;

    @Mock
    private DomandaService domandaService;

    @InjectMocks
    private QuestionarioService questionarioService;

    private Questionario questionario;

    @BeforeEach
    void setUp() {
        questionario = new Questionario("Test Questionario", "Descrizione test", new ArrayList<>());
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
    void testGetQuestionarioCompleto() {
        int id = 1;
        ArrayList<Domanda> domandeMock = new ArrayList<>();

        when(questionarioRepository.getQuestionarioByID(1)).thenReturn(questionario);
        when(domandaService.getDomandeComplete(id)).thenReturn(domandeMock);

        Questionario result = questionarioService.getQuestionarioCompleto(1);
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
    void testModificaInfo() {
        String nuovoNome = "Nuovo Nome";
        String nuovaDescrizione = "Nuova Descrizione";
        when(questionarioRepository.updateQuestionario(questionario)).thenReturn(true);

        boolean result = questionarioService.modificaInfo(questionario, nuovoNome, nuovaDescrizione);
        assertTrue(result);
        assertEquals(nuovoNome, questionario.getNome());
        assertEquals(nuovaDescrizione, questionario.getDescrizione());
        verify(questionarioRepository, times(1)).updateQuestionario(questionario);
    }

    @Test
    void testModificaInfoDescrizioneNull() {
        String nuovoNome = "Nuovo Nome";
        assertThrows(IllegalArgumentException.class,
                () -> questionarioService.modificaInfo(questionario, nuovoNome, null));
    }

    @Test
    void testModificaInfoNome() {
        String nuovoNome = "Nuovo Nome";
        when(questionarioRepository.updateQuestionario(questionario)).thenReturn(true);

        boolean result = questionarioService.modificaInfo(questionario, nuovoNome, questionario.getDescrizione());
        assertTrue(result);
        assertEquals(nuovoNome, questionario.getNome());
        verify(questionarioRepository, times(1)).updateQuestionario(questionario);
    }
}
