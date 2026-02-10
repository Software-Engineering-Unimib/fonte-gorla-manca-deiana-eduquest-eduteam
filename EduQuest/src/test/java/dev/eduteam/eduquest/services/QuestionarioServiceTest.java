package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Compitino;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.questionari.Questionario.Difficulty;
import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.repositories.questionari.QuestionarioRepository;
import dev.eduteam.eduquest.repositories.accounts.DocenteRepository;
import dev.eduteam.eduquest.services.questionari.DomandaService;
import dev.eduteam.eduquest.services.questionari.QuestionarioService;
import dev.eduteam.eduquest.services.questionari.CompitinoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

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

    @Mock
    private CompitinoService compitinoService;

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

        boolean result = questionarioService.modificaInfoQuestionario(questionario, nuovoNome, nuovaDescrizione,
                nuovaDiff);
        assertTrue(result);
        assertEquals(nuovoNome, questionario.getNome());
        assertEquals(nuovaDescrizione, questionario.getDescrizione());
        assertEquals(nuovaDiff, questionario.getLivelloDifficulty());
        verify(questionarioRepository, times(1)).updateQuestionario(questionario);
    }

    @Test
    void testModificaInfoDescrizioneNull() {
        assertThrows(IllegalArgumentException.class,
                () -> questionarioService.modificaInfoQuestionario(questionario, "Nome", null, Difficulty.Medio));
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

    @Test
    void testGetQuestionariDisponibiliPerStudente_CompitinoCompilabile() {
        int studenteID = 10;
        ArrayList<Questionario> tutti = new ArrayList<>();

        Docente d = new Docente();
        d.setAccountID(1);

        Questionario qNormale = new Questionario("Q1", "Desc", new ArrayList<>(), d, Difficulty.Medio);
        qNormale.setID(1);
        Compitino c = new Compitino("Comp1", "DescC", new ArrayList<>(), d, Difficulty.Medio, LocalDate.now(), 1);
        c.setID(2);

        tutti.add(qNormale);
        tutti.add(c);

        when(questionarioRepository.getQuestionari()).thenReturn(tutti);
        when(compitinoService.isCompilabileByStudente(studenteID, c.getID())).thenReturn(true);

        List<Questionario> result = questionarioService.getQuestionariDisponibliPerStudente(studenteID);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(questionarioRepository, times(1)).getQuestionari();
        verify(compitinoService, times(1)).isCompilabileByStudente(studenteID, c.getID());
    }

    @Test
    void testGetQuestionariDisponibiliPerStudente_CompitinoNonCompilabile() {
        int studenteID = 11;
        ArrayList<Questionario> tutti = new ArrayList<>();

        Docente d = new Docente();
        d.setAccountID(2);

        Questionario qNormale = new Questionario("Q2", "Desc2", new ArrayList<>(), d, Difficulty.Facile);
        qNormale.setID(3);
        Compitino c = new Compitino("Comp2", "DescC2", new ArrayList<>(), d, Difficulty.Facile, LocalDate.now(), 1);
        c.setID(4);

        tutti.add(qNormale);
        tutti.add(c);

        when(questionarioRepository.getQuestionari()).thenReturn(tutti);
        when(compitinoService.isCompilabileByStudente(studenteID, c.getID())).thenReturn(false);

        List<Questionario> result = questionarioService.getQuestionariDisponibliPerStudente(studenteID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(qNormale.getID(), result.get(0).getID());
        verify(questionarioRepository, times(1)).getQuestionari();
        verify(compitinoService, times(1)).isCompilabileByStudente(studenteID, c.getID());
    }

    @Test
    void testGetDomandaInSospeso_InMetà() {
        int id = 1;
        ArrayList<Domanda> domande = new ArrayList<>();
        Domanda d1 = Domanda.createDomandaOfType(Domanda.Type.DOMANDA_MULTIPLA);
        d1.setID(11);
        d1.setTesto("Domanda 1");
        Domanda d2 = Domanda.createDomandaOfType(Domanda.Type.DOMANDA_MULTIPLA);
        d2.setID(12);
        d2.setTesto("Domanda 2");

        domande.add(d1);
        domande.add(d2);
        questionario.setElencoDomande(domande);

        when(questionarioRepository.getQuestionarioByID(id)).thenReturn(questionario);
        when(domandaService.getDomandeComplete(id)).thenReturn(domande);

        Risposta[] risposteDate = new Risposta[3];
        Risposta r1 = new Risposta("Roma");
        r1.setID(501);
        risposteDate[0] = r1;
        risposteDate[1] = null; // In sospeso
        Domanda result = questionarioService.getDomandaInSospeso(id, risposteDate);

        assertNotNull(result);
        assertEquals(12, result.getID()); // Deve restituire la seconda domanda
        assertEquals("Domanda 2", result.getTesto());
    }

    @Test
    void testGetDomandaInSospeso_TutteCompletate() {
        int id = 1;
        ArrayList<Domanda> domande = new ArrayList<>();
        Domanda d1 = Domanda.createDomandaOfType(Domanda.Type.DOMANDA_VERO_FALSO);
        d1.setID(13);
        d1.setTesto("Domanda 1");
        domande.add(d1);
        questionario.setElencoDomande(domande);

        when(questionarioRepository.getQuestionarioByID(id)).thenReturn(questionario);
        when(domandaService.getDomandeComplete(id)).thenReturn(domande);

        // Tutte le domande hanno una risposta
        Risposta r1 = new Risposta("Vero");
        Risposta[] risposteDate = new Risposta[1];
        risposteDate[0] = r1;

        Domanda result = questionarioService.getDomandaInSospeso(id, risposteDate);

        assertNull(result); // Non ci sono domande in sospeso
    }

    @Test
    void testGetDomandaInSospeso_ArrayVuoto() {
        int id = 1;
        ArrayList<Domanda> domande = new ArrayList<>();
        Domanda d1 = Domanda.createDomandaOfType(Domanda.Type.DOMANDA_MULTIPLA);
        d1.setID(14);
        d1.setTesto("Domanda 1");
        domande.add(d1);
        questionario.setElencoDomande(domande);

        when(questionarioRepository.getQuestionarioByID(id)).thenReturn(questionario);
        when(domandaService.getDomandeComplete(id)).thenReturn(domande);

        // Lo studente ha appena iniziato: l'array contiene null
        Risposta[] risposteDate = new Risposta[1];
        risposteDate[0] = null;

        Domanda result = questionarioService.getDomandaInSospeso(id, risposteDate);

        assertNotNull(result);
        assertEquals(14, result.getID());
    }
}
