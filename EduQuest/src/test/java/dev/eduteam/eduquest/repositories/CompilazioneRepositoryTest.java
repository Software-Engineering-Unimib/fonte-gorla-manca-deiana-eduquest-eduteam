package dev.eduteam.eduquest.repositories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.*;
import java.util.ArrayList;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.models.questionari.*;
import dev.eduteam.eduquest.repositories.accounts.StudenteRepository;
import dev.eduteam.eduquest.repositories.questionari.CompilazioneRepository;
import dev.eduteam.eduquest.repositories.questionari.DomandaRepository;
import dev.eduteam.eduquest.repositories.questionari.QuestionarioRepository;

@ExtendWith(MockitoExtension.class)
public class CompilazioneRepositoryTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;
    @Mock
    private StudenteRepository studenteRepository;
    @Mock
    private QuestionarioRepository questionarioRepository;
    @Mock
    private DomandaRepository domandaRepository;

    @InjectMocks
    private CompilazioneRepository compilazioneRepository;

    private MockedStatic<ConnectionSingleton> mockedStatic;
    private Docente mockDocente;

    @BeforeEach
    void setUp() throws Exception {
        mockedStatic = mockStatic(ConnectionSingleton.class);
        ConnectionSingleton cs = mock(ConnectionSingleton.class);
        when(cs.getConnection()).thenReturn(mockConnection);
        mockedStatic.when(ConnectionSingleton::getInstance).thenReturn(cs);

        mockDocente = new Docente("Mario", "Rossi", "mrossi", "m@t.it", "pw");
        mockDocente.setInsegnamento("Informatica");
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    private void mockMapperDependencies() {
        Studente s = new Studente("L", "B", "u", "e", "p");
        Questionario q = new Questionario("Test", "Desc", new ArrayList<>(), mockDocente,
                Questionario.Difficulty.Medio);

        // lenient() evita l'errore UnnecessaryStubbingException
        lenient().when(studenteRepository.getStudenteByAccountID(anyInt())).thenReturn(s);
        lenient().when(questionarioRepository.getQuestionarioByID(anyInt())).thenReturn(q);
    }

    @Test
    void getCompilazioneByID_Success() throws Exception {

        mockMapperDependencies();
        lenient().when(domandaRepository.getDomandeByQuestionario(anyInt())).thenReturn(new ArrayList<>());
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("studenteID_FK")).thenReturn(1);
        when(mockResultSet.getInt("questionarioID_FK")).thenReturn(10);
        when(mockResultSet.getInt("compilazioneID")).thenReturn(100);
        when(mockResultSet.getBoolean("completato")).thenReturn(true);
        when(mockResultSet.getInt("punteggio")).thenReturn(50);

        Compilazione c = compilazioneRepository.getCompilazioneByID(100);

        assertNotNull(c);
        assertEquals(100, c.getID());
        assertEquals("Informatica", c.getQuestionario().getMateria());

        verify(mockPreparedStatement).setInt(1, 100);

    }

    @Test
    void insertCompilazione_Success() throws Exception {
        mockMapperDependencies();

        Studente s = studenteRepository.getStudenteByAccountID(1);
        s.setAccountID(1);

        Questionario q = questionarioRepository.getQuestionarioByID(10);
        q.setID(10);

        Compilazione comp = new Compilazione(s, q);

        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(500);

        Compilazione result = compilazioneRepository.insertCompilazione(comp);

        assertNotNull(result);
        assertEquals(500, result.getID());

        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).setInt(2, 10);
    }

    @Test
    void getTopCompilazioniGlobale_Success() throws Exception {
        mockMapperDependencies();

        lenient().when(domandaRepository.getDomandeByQuestionario(anyInt())).thenReturn(new ArrayList<>());

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("punteggio")).thenReturn(100, 95);
        when(mockResultSet.getInt("compilazioneID")).thenReturn(1, 2);
        when(mockResultSet.getInt("studenteID_FK")).thenReturn(1, 1);
        when(mockResultSet.getInt("questionarioID_FK")).thenReturn(10, 10);
        when(mockResultSet.getBoolean("completato")).thenReturn(true, true);

        ArrayList<Compilazione> classifica = compilazioneRepository.getTopCompilazioniGlobale(2);

        assertNotNull(classifica);
        assertEquals(2, classifica.size());
        assertEquals(100, classifica.get(0).getPunteggio());
    }

    @Test
    void getTopCompilazioniPerQuestionario_Success() throws Exception {
        mockMapperDependencies();
        lenient().when(domandaRepository.getDomandeByQuestionario(anyInt())).thenReturn(new ArrayList<>());

        int questionarioID = 10;
        int limit = 5;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("punteggio")).thenReturn(80);

        when(mockResultSet.getInt("compilazioneID")).thenReturn(1);
        when(mockResultSet.getInt("studenteID_FK")).thenReturn(1);
        when(mockResultSet.getInt("questionarioID_FK")).thenReturn(questionarioID);
        when(mockResultSet.getBoolean("completato")).thenReturn(true);

        ArrayList<Compilazione> classifica = compilazioneRepository.getTopCompilazioniPerQuestionario(questionarioID,
                limit);

        assertNotNull(classifica);
        assertEquals(1, classifica.size());
        assertEquals(80, classifica.get(0).getPunteggio());

        verify(mockPreparedStatement).setInt(1, questionarioID);
        verify(mockPreparedStatement).setInt(2, limit);
    }
}