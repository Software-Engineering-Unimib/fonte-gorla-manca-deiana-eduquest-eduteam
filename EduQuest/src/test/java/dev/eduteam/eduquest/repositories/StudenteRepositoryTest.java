package dev.eduteam.eduquest.repositories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.repositories.accounts.StudenteRepository;
import dev.eduteam.eduquest.repositories.accounts.AccountRepository;

@ExtendWith(MockitoExtension.class)
public class StudenteRepositoryTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private StudenteRepository studenteRepository;

    private MockedStatic<ConnectionSingleton> mockedStatic;

    @BeforeEach
    void setUp() throws Exception {
        mockedStatic = mockStatic(ConnectionSingleton.class);
        ConnectionSingleton cs = mock(ConnectionSingleton.class);
        when(cs.getConnection()).thenReturn(mockConnection);
        mockedStatic.when(ConnectionSingleton::getInstance).thenReturn(cs);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    private void setupMockResultSet(String nome, double media, int points) throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("nome")).thenReturn(nome);
        when(mockResultSet.getString("cognome")).thenReturn("Test");
        when(mockResultSet.getString("userName")).thenReturn("user");
        when(mockResultSet.getString("email")).thenReturn("email@test.it");
        when(mockResultSet.getString("password")).thenReturn("pwd");
        when(mockResultSet.getInt("accountID")).thenReturn(1);
        when(mockResultSet.getDouble("mediaPunteggio")).thenReturn(media);
        when(mockResultSet.getInt("eduPoints")).thenReturn(points);
    }

    @Test
    void getStudenteByAccountID_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        setupMockResultSet("Giulia", 9.0, 500);

        Studente s = studenteRepository.getStudenteByAccountID(1);

        assertNotNull(s);
        assertEquals("Giulia", s.getNome());
        assertEquals(9.0, s.getMediaPunteggio());
        verify(mockPreparedStatement).setInt(1, 1);
    }

    @Test
    void insertStudente_Success() throws Exception {
        Studente s = new Studente("Mario", "Rossi", "mario", "m@r.it", "pwd");
        s.setMediaPunteggio(7.5);
        s.setEduPoints(100);

        when(accountRepository.insertAccount(any(Studente.class), eq("Studente"))).thenReturn(10);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Studente result = studenteRepository.insertStudente(s);

        assertNotNull(result);
        assertEquals(10, result.getAccountID());

        verify(mockPreparedStatement).setInt(1, 10);
        verify(mockPreparedStatement).setDouble(2, 7.5);
        verify(mockPreparedStatement).setInt(3, 100);
    }

    @Test
    void getTopStudentiPerMedia_ReturnsList() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        setupMockResultSet("Marco", 9.5, 1000);

        ArrayList<Studente> top = studenteRepository.getTopStudentiPerMedia(1);

        assertFalse(top.isEmpty());
        assertEquals(9.5, top.get(0).getMediaPunteggio());
        verify(mockPreparedStatement).setInt(1, 1);
    }

    @Test
    void getStatisticheBaseStudente_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getDouble("mediaPunteggio")).thenReturn(8.0);
        when(mockResultSet.getInt("eduPoints")).thenReturn(200);
        when(mockResultSet.getInt("totale")).thenReturn(15);

        StudenteRepository.RiepilogoStudente stats = studenteRepository.getStatisticheBaseStudente(1);

        assertNotNull(stats);
        assertEquals(8.0, stats.media);
        assertEquals(200, stats.eduPoints);
        assertEquals(15, stats.numeroCompilazioni);
    }

    @Test
    void updateStudente_Success() throws Exception {
        Studente s = new Studente("N", "C", "u", "e", "p");
        s.setAccountID(1);
        s.setMediaPunteggio(8.5);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean updated = studenteRepository.updateStudente(s);

        assertTrue(updated);

        verify(accountRepository).updateAccount(s);
        verify(mockPreparedStatement).setDouble(1, 8.5);
        verify(mockPreparedStatement).setInt(3, 1);
    }

    @Test
    void getVincitoriBonusCompitino_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        setupMockResultSet("Luigi", 0.0, 0);

        List<Studente> vincitori = studenteRepository.getVincitoriBonusCompitino(101, 3);

        assertEquals(1, vincitori.size());
        assertEquals("Luigi", vincitori.get(0).getNome());
    }
}