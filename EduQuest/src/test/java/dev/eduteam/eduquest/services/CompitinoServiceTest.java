package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.questionari.Compitino;
import dev.eduteam.eduquest.models.questionari.Questionario.Difficulty;
import dev.eduteam.eduquest.repositories.accounts.DocenteRepository;
import dev.eduteam.eduquest.repositories.questionari.CompitinoRepository;
import dev.eduteam.eduquest.repositories.questionari.QuestionarioRepository;
import dev.eduteam.eduquest.services.questionari.CompitinoService;
import dev.eduteam.eduquest.services.questionari.DomandaService;
import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.repositories.accounts.StudenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompitinoServiceTest {

    @Mock
    private CompitinoRepository compitinoRepository;

    @Mock
    private QuestionarioRepository questionarioRepository;

    @Mock
    private DocenteRepository docenteRepository;

    @Mock
    private StudenteRepository studenteRepository;

    @Mock
    private DomandaService domandaService;

    @InjectMocks
    private CompitinoService compitinoService;

    private Compitino compitino;
    private Docente docente;

    @BeforeEach
    void setUp() {
        docente = new Docente();
        docente.setAccountID(1);
        docente.setInsegnamento("Informatica");

        compitino = new Compitino("Test Compitino", "Descrizione", new ArrayList<>(),
                docente, Difficulty.Medio, LocalDate.now().plusDays(7), 2);
        compitino.setID(1);
    }

    @Test
    void testGetCompitinoCompleto() {
        int id = 1;
        when(compitinoRepository.getQuestionarioByID(id)).thenReturn(compitino);
        when(domandaService.getDomandeComplete(id)).thenReturn(new ArrayList<>());

        Compitino result = compitinoService.getCompitinoCompleto(id);

        assertNotNull(result);
        assertEquals(id, result.getID());
        assertTrue(result instanceof Compitino);
        verify(compitinoRepository).getQuestionarioByID(id);
        verify(domandaService).getDomandeComplete(id);
    }

    @Test
    void testCreaCompitinoSuccesso() {
        int docenteID = 1;
        LocalDate scadenza = LocalDate.now().plusDays(10);
        int tentativi = 3;

        when(docenteRepository.getDocenteByAccountID(docenteID)).thenReturn(docente);
        when(compitinoRepository.insertCompitino(any(Compitino.class)))
                .thenAnswer(invocation -> {
                    Compitino arg = invocation.getArgument(0);
                    arg.setID(1);
                    return arg;
                });

        Compitino result = compitinoService.creaCompitino(docenteID, Difficulty.Difficile, scadenza, tentativi);

        assertNotNull(result);
        assertEquals(1, result.getID());
        assertEquals(scadenza, result.getDataFine());
        verify(docenteRepository).getDocenteByAccountID(docenteID);
        verify(compitinoRepository).insertCompitino(any(Compitino.class));
    }

    @Test
    void testCreaCompitinoDocenteNull() {
        when(docenteRepository.getDocenteByAccountID(anyInt())).thenReturn(null);

        Compitino result = compitinoService.creaCompitino(99, Difficulty.Facile, LocalDate.now(), 1);

        assertNull(result);
        verify(questionarioRepository, never()).insertQuestionario(any());
    }

    @Test
    void testIsCompilabileSuccesso() {
        int studenteID = 1;
        int compitinoID = 1;

        when(compitinoRepository.getQuestionarioByID(compitinoID)).thenReturn(compitino);
        when(compitinoRepository.countTentativi(studenteID, compitinoID)).thenReturn(0);

        boolean result = compitinoService.isCompilabileByStudente(studenteID, compitinoID);

        assertTrue(result);
    }

    @Test
    void testIsCompilabileScaduto() {
        int studenteID = 1;
        int compitinoID = 1;
        // Impostiamo una data passata
        compitino.setDataFine(LocalDate.now().minusDays(1));

        when(compitinoRepository.getQuestionarioByID(compitinoID)).thenReturn(compitino);

        boolean result = compitinoService.isCompilabileByStudente(studenteID, compitinoID);

        assertFalse(result);
        // Non deve nemmeno controllare i tentativi se la data è già scaduta
        verify(compitinoRepository, never()).countTentativi(anyInt(), anyInt());
    }

    @Test
    void testIsCompilabileTentativiEsauriti() {
        int studenteID = 1;
        int compitinoID = 1;
        // Il compitino nel setUp ha tentativiMax = 2
        when(compitinoRepository.getQuestionarioByID(compitinoID)).thenReturn(compitino);
        when(compitinoRepository.countTentativi(studenteID, compitinoID)).thenReturn(2);

        boolean result = compitinoService.isCompilabileByStudente(studenteID, compitinoID);

        assertFalse(result);
        verify(compitinoRepository).countTentativi(studenteID, compitinoID);
    }

    @Test
    void testElaboraPremiCompitiniScaduti_NessunId() {
        when(compitinoRepository.getIDCompitiniScadutiDaAssegnare()).thenReturn(new ArrayList<>());

        compitinoService.elaboraPremiCompitiniScaduti();

        verify(compitinoRepository).getIDCompitiniScadutiDaAssegnare();
        verify(compitinoRepository, never()).getQuestionarioByID(anyInt());
        verify(studenteRepository, never()).getVincitoriBonusCompitino(anyInt(), anyInt());
    }

    @Test
    void testElaboraPremiCompitiniScaduti_AssegnaPunti() {
        int id = 1;
        compitino.setPuntiBonus(10);
        compitino.setAssegnatiPtBonus(false);

        Studente s1 = new Studente();
        s1.setAccountID(11);
        s1.setEduPoints(5);

        Studente s2 = new Studente();
        s2.setAccountID(12);
        s2.setEduPoints(0);

        when(compitinoRepository.getIDCompitiniScadutiDaAssegnare()).thenReturn(java.util.Arrays.asList(id));
        when(compitinoRepository.getQuestionarioByID(id)).thenReturn(compitino);
        when(studenteRepository.getVincitoriBonusCompitino(id, 3)).thenReturn(java.util.Arrays.asList(s1, s2));

        compitinoService.elaboraPremiCompitiniScaduti();

        // i punti devono essere aggiornati
        assertEquals(15, s1.getEduPoints());
        assertEquals(10, s2.getEduPoints());

        // update chiamati per ogni studente
        verify(studenteRepository).updateStudente(s1);
        verify(studenteRepository).updateStudente(s2);

        // compitino deve essere segnato come assegnato e aggiornato
        assertTrue(compitino.getAssegnatiPtBonus());
        verify(compitinoRepository).updateCompitino(compitino);
    }
}
