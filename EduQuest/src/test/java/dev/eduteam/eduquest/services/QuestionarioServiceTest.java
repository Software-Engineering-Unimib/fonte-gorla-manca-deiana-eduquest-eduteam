package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.questionari.Questionario.Difficulty;
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
        docente.setInsegnamento("Informatica");
        questionario = new Questionario("Test Questionario", "Descrizione test", new ArrayList<>(), docente,
                Difficulty.Medio);
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
        assertEquals("Informatica", result.getMateria());
        verify(questionarioRepository, times(1)).getQuestionarioByID(id);
    }

    @Test
    void testCreaQuestionario() {
        int docenteID = 1;
        Difficulty diffTest = Difficulty.Difficile;
        when(docenteRepository.getDocenteByAccountID(docenteID)).thenReturn(docente);
        when(questionarioRepository.insertQuestionario(any(Questionario.class))).thenAnswer(i -> i.getArguments()[0]);

        Questionario result = questionarioService.creaQuestionario(docenteID, diffTest);

        assertNotNull(result);
        assertEquals(diffTest, result.getLivelloDifficulty());
        assertEquals("Informatica", result.getMateria());
        verify(docenteRepository).getDocenteByAccountID(docenteID);
        verify(questionarioRepository).insertQuestionario(any(Questionario.class));
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
        Difficulty nuovaDiff = Difficulty.Facile;
        when(questionarioRepository.updateQuestionario(questionario)).thenReturn(true);

        boolean result = questionarioService.modificaInfo(questionario, nuovoNome, nuovaDescrizione, nuovaDiff);
        assertTrue(result);
        assertEquals(nuovoNome, questionario.getNome());
        assertEquals(nuovaDescrizione, questionario.getDescrizione());
        assertEquals(nuovaDiff, questionario.getLivelloDifficulty());
        verify(questionarioRepository, times(1)).updateQuestionario(questionario);
    }

    @Test
    void testModificaInfoDescrizioneNull() {
        assertThrows(IllegalArgumentException.class,
                () -> questionarioService.modificaInfo(questionario, "Nome", null, Difficulty.Medio));
    }

    @Test
    void testCreaQuestionarioConDocenteID() {
        int docenteID = 5;
        Docente d = new Docente();
        d.setAccountID(docenteID);
        d.setInsegnamento("Matematica");
        Questionario nuovoQuestionario = new Questionario("Nuovo Questionario", "Nuova Descrizione", new ArrayList<>(),
                d, Difficulty.Difficile);
        nuovoQuestionario.setID(3);

        when(docenteRepository.getDocenteByAccountID(docenteID)).thenReturn(d);
        when(questionarioRepository.insertQuestionario(any(Questionario.class))).thenReturn(nuovoQuestionario);

        Questionario result = questionarioService.creaQuestionario(docenteID, Difficulty.Facile);
        assertNotNull(result);
        assertEquals(3, result.getID());
        assertEquals(docenteID, result.getDocente().getAccountID());
        verify(docenteRepository, times(1)).getDocenteByAccountID(docenteID);
        verify(questionarioRepository, times(1)).insertQuestionario(any(Questionario.class));
    }

    @Test
    void testGetQuestionariByDocente() {
        int docenteID = 5;
        ArrayList<Questionario> questionariDocente = new ArrayList<>();
        Docente d1 = new Docente();
        d1.setAccountID(docenteID);
        Questionario q1 = new Questionario("Questionario 1", "Descrizione 1", new ArrayList<>(), d1, Difficulty.Medio);
        q1.setID(1);
        Docente d2 = new Docente();
        d2.setAccountID(docenteID);
        Questionario q2 = new Questionario("Questionario 2", "Descrizione 2", new ArrayList<>(), d2, Difficulty.Facile);
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
