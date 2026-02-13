package dev.eduteam.eduquest.repositories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.questionari.*;
import dev.eduteam.eduquest.repositories.accounts.DocenteRepository;
import dev.eduteam.eduquest.repositories.questionari.QuestionarioRepository;

@ExtendWith(MockitoExtension.class)
public class QuestionarioRepositoryTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @Mock
    private DocenteRepository docenteRepository;

    @InjectMocks
    private QuestionarioRepository questionarioRepository;

    private MockedStatic<ConnectionSingleton> mockedStatic;
    private Docente mockDocente;

    @BeforeEach
    void setUp() throws Exception {
        mockedStatic = mockStatic(ConnectionSingleton.class);
        ConnectionSingleton cs = mock(ConnectionSingleton.class);
        when(cs.getConnection()).thenReturn(mockConnection);
        mockedStatic.when(ConnectionSingleton::getInstance).thenReturn(cs);

        mockDocente = new Docente("Mario", "Rossi", "mrossi", "m@r.it", "pw");
        mockDocente.setInsegnamento("Matematica");
        mockDocente.setAccountID(1);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    void getQuestionarioByID_ReturnsCompitino() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        when(mockResultSet.getInt("docenteID_FK")).thenReturn(1);
        when(mockResultSet.getString("livelloDiff")).thenReturn("Medio");
        when(mockResultSet.getString("nome")).thenReturn("Parziale 1");
        when(mockResultSet.getString("descrizione")).thenReturn("Desc");
        when(mockResultSet.getInt("questionarioID")).thenReturn(100);
        when(mockResultSet.getDate("dataCreazione")).thenReturn(Date.valueOf(LocalDate.now()));

        when(mockResultSet.getObject("dataFine")).thenReturn(Date.valueOf(LocalDate.now().plusDays(5)));
        when(mockResultSet.getDate("dataFine")).thenReturn(Date.valueOf(LocalDate.now().plusDays(5)));
        when(mockResultSet.getInt("tentativiMax")).thenReturn(2);

        when(docenteRepository.getDocenteByAccountID(1)).thenReturn(mockDocente);

        Questionario result = questionarioRepository.getQuestionarioByID(100);

        assertNotNull(result);
        assertTrue(result instanceof Compitino, "Dovrebbe essere un'istanza di Compitino");
        assertEquals(2, ((Compitino) result).getTentativiMax());
    }

    @Test
    void getQuestionarioByID_ReturnsEsercitazione() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        when(mockResultSet.getInt("docenteID_FK")).thenReturn(1);
        when(mockResultSet.getString("livelloDiff")).thenReturn("Facile");
        when(mockResultSet.getString("nome")).thenReturn("Esercizi Algebra");
        when(mockResultSet.getString("descrizione")).thenReturn("Desc");
        when(mockResultSet.getDate("dataCreazione")).thenReturn(Date.valueOf(LocalDate.now()));

        when(mockResultSet.getObject("noteDidattiche")).thenReturn("Usa il libro pag. 20");
        when(mockResultSet.getString("noteDidattiche")).thenReturn("Usa il libro pag. 20");

        when(mockResultSet.getObject("dataFine")).thenReturn(null);

        when(docenteRepository.getDocenteByAccountID(1)).thenReturn(mockDocente);

        Questionario result = questionarioRepository.getQuestionarioByID(101);

        assertTrue(result instanceof Esercitazione);
        assertEquals("Usa il libro pag. 20", ((Esercitazione) result).getNoteDidattiche());
    }

    @Test
    void insertQuestionario_Success() throws Exception {
        Questionario q = new Questionario("Test", "Desc", new ArrayList<>(), mockDocente,
                Questionario.Difficulty.Difficile);

        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(500);

        Questionario result = questionarioRepository.insertQuestionario(q);

        assertNotNull(result);
        assertEquals(500, result.getID());
        assertEquals("Matematica", result.getMateria());

        verify(mockPreparedStatement).setString(eq(4), eq("Difficile"));
    }

    @Test
    void updateQuestionario_Success() throws Exception {
        Questionario q = new Questionario("Nuovo", "Desc", new ArrayList<>(), mockDocente,
                Questionario.Difficulty.Medio);
        q.setID(500);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean updated = questionarioRepository.updateQuestionario(q);

        assertTrue(updated);
        verify(mockPreparedStatement).setString(1, "Nuovo");
        verify(mockPreparedStatement).setInt(4, 500);
    }
}