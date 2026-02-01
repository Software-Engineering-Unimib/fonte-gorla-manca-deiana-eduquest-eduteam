package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.questionari.Compitino;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.questionari.Questionario.Difficulty;
import dev.eduteam.eduquest.repositories.accounts.DocenteRepository;
import dev.eduteam.eduquest.repositories.questionari.CompitinoRepository;
import dev.eduteam.eduquest.repositories.questionari.QuestionarioRepository;
import dev.eduteam.eduquest.services.questionari.CompitinoService;
import dev.eduteam.eduquest.services.questionari.DomandaService;
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
        when(compitinoRepository.getCompitinoByID(id)).thenReturn(compitino);
        when(domandaService.getDomandeComplete(id)).thenReturn(new ArrayList<>());

        Compitino result = compitinoService.getCompitinoCompleto(id);

        assertNotNull(result);
        assertEquals(id, result.getID());
        verify(compitinoRepository).getCompitinoByID(id);
        verify(domandaService).getDomandeComplete(id);
    }

    @Test
    void testCreaCompitinoSuccesso() {
        int docenteID = 1;
        LocalDate scadenza = LocalDate.now().plusDays(5);
        int tentativi = 3;

        when(docenteRepository.getDocenteByAccountID(docenteID)).thenReturn(docente);
        // Mock inserimento base (ritorna un oggetto con ID)
        Questionario qBase = new Questionario("QBase","Descrizione base", new ArrayList<>(), docente, Difficulty.Difficile);
        qBase.setID(10);
        when(questionarioRepository.insertQuestionario(any(Compitino.class))).thenReturn(qBase);
        // Mock inserimento dettagli specifici
        when(compitinoRepository.insertDettagliCompitino(any(Compitino.class), eq(10))).thenReturn(true);

        Compitino result = compitinoService.creaCompitino(docenteID, Difficulty.Difficile, scadenza, tentativi);

        assertNotNull(result);
        assertEquals(10, result.getID());
        assertEquals(scadenza, result.getDataFine());
        verify(docenteRepository).getDocenteByAccountID(docenteID);
        verify(questionarioRepository).insertQuestionario(any(Compitino.class));
        verify(compitinoRepository).insertDettagliCompitino(any(Compitino.class), eq(10));
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

        when(compitinoRepository.getCompitinoByID(compitinoID)).thenReturn(compitino);
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

        when(compitinoRepository.getCompitinoByID(compitinoID)).thenReturn(compitino);

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
        when(compitinoRepository.getCompitinoByID(compitinoID)).thenReturn(compitino);
        when(compitinoRepository.countTentativi(studenteID, compitinoID)).thenReturn(2);

        boolean result = compitinoService.isCompilabileByStudente(studenteID, compitinoID);

        assertFalse(result);
        verify(compitinoRepository).countTentativi(studenteID, compitinoID);
    }
}
