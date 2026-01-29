package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.repositories.questionari.QuestionarioRepository;
import dev.eduteam.eduquest.repositories.accounts.DocenteRepository;
import dev.eduteam.eduquest.services.questionari.DomandaService;
import dev.eduteam.eduquest.services.questionari.QuestionarioService;

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
    private DocenteRepository docenteRepository;

    @Mock
    private DomandaService domandaService;

    @InjectMocks
    private QuestionarioService questionarioService;

    private Questionario questionario;
    private Docente docente;

    @BeforeEach
    void setUp() {
        docente = new Docente();
        docente.setAccountID(1);
        questionario = new Questionario("Test Questionario", "Descrizione test", new ArrayList<>(), docente);
        questionario.setID(1);
    }

    @Test
    void testGetQuestionari() {
        ArrayList<Questionario> questionari = new ArrayList<>();
        questionari.add(questionario);
        int docenteID = 1;
        when(questionarioRepository.getQuestionariByDocente(docenteID)).thenReturn(questionari);

        ArrayList<Questionario> result = questionarioService.getQuestionariByDocente(docenteID);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(questionarioRepository, times(1)).getQuestionariByDocente(docenteID);
    }

    @Test
    void testGetQuestionarioCompleto() {
        int id = 1;
        ArrayList<Domanda> domandeMock = new ArrayList<>();

        when(questionarioRepository.getQuestionarioByID(id)).thenReturn(questionario);
        when(domandaService.getDomandeComplete(id)).thenReturn(domandeMock);

        Questionario result = questionarioService.getQuestionarioCompleto(id);
        assertNotNull(result);
        assertEquals("Test Questionario", result.getNome());
        verify(questionarioRepository, times(1)).getQuestionarioByID(id);
    }

    @Test
    void testCreaQuestionario() {
        Questionario nuovoQuestionario = new Questionario("", "", new ArrayList<>(), new Docente());
        nuovoQuestionario.setID(2);
        when(questionarioRepository.insertQuestionario(any(Questionario.class))).thenReturn(nuovoQuestionario);

        Questionario result = questionarioService.creaQuestionario(0);
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

    @Test
    void testCreaQuestionarioConDocenteID() {
        int docenteID = 5;
        Docente d = new Docente();
        d.setAccountID(docenteID);
        Questionario nuovoQuestionario = new Questionario("Nuovo Questionario", "Nuova Descrizione", new ArrayList<>(),
                d);
        nuovoQuestionario.setID(3);

        when(questionarioRepository.insertQuestionario(any(Questionario.class))).thenReturn(nuovoQuestionario);

        Questionario result = questionarioService.creaQuestionario(docenteID);
        assertNotNull(result);
        assertEquals(3, result.getID());
        assertEquals(docenteID, result.getDocente().getAccountID());
        verify(questionarioRepository, times(1)).insertQuestionario(any(Questionario.class));
    }

    @Test
    void testGetQuestionariByDocente() {
        int docenteID = 5;
        ArrayList<Questionario> questionariDocente = new ArrayList<>();
        Docente d1 = new Docente();
        d1.setAccountID(docenteID);
        Questionario q1 = new Questionario("Questionario 1", "Descrizione 1", new ArrayList<>(), d1);
        q1.setID(1);
        Docente d2 = new Docente();
        d2.setAccountID(docenteID);
        Questionario q2 = new Questionario("Questionario 2", "Descrizione 2", new ArrayList<>(), d2);
        q2.setID(2);
        questionariDocente.add(q1);
        questionariDocente.add(q2);

        when(questionarioRepository.getQuestionariByDocente(docenteID)).thenReturn(questionariDocente);

        ArrayList<Questionario> result = questionarioService.getQuestionariByDocente(docenteID);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(docenteID, result.get(0).getDocente().getAccountID());
        assertEquals(docenteID, result.get(1).getDocente().getAccountID());
        verify(questionarioRepository, times(1)).getQuestionariByDocente(docenteID);
    }

    @Test
    void testGetQuestionariByDocenteVuoto() {
        int docenteID = 99;
        when(questionarioRepository.getQuestionariByDocente(docenteID)).thenReturn(new ArrayList<>());

        ArrayList<Questionario> result = questionarioService.getQuestionariByDocente(docenteID);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(questionarioRepository, times(1)).getQuestionariByDocente(docenteID);
    }
}
