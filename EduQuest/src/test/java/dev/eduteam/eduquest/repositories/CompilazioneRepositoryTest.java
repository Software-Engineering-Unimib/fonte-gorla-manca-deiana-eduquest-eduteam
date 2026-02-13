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

    @Test
    void getCompilazioneByID_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("studenteID_FK")).thenReturn(1);
        when(mockResultSet.getInt("questionarioID_FK")).thenReturn(10);
        when(mockResultSet.getInt("compilazioneID")).thenReturn(100);

        Studente s = new Studente("L", "B", "u", "e", "p");
        Questionario q = new Questionario("Test", "Desc", new ArrayList<>(), mockDocente,
                Questionario.Difficulty.Medio);

        when(studenteRepository.getStudenteByAccountID(1)).thenReturn(s);
        when(questionarioRepository.getQuestionarioByID(10)).thenReturn(q);

        Compilazione c = compilazioneRepository.getCompilazioneByID(100);

        assertNotNull(c);
        assertEquals("Informatica", c.getQuestionario().getMateria());
    }

    @Test
    void insertCompilazione_Success() throws Exception {
        Studente s = new Studente("A", "B", "u", "e", "p");
        s.setAccountID(1);
        Questionario q = new Questionario("Quiz", "Descrizione", new ArrayList<>(), mockDocente,
                Questionario.Difficulty.Facile);
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
    }
}