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

import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.repositories.questionari.RispostaRepository;

@ExtendWith(MockitoExtension.class)
public class RispostaRepositoryTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private RispostaRepository rispostaRepository;

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

    @Test
    void getRispostaByID_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("rispostaID")).thenReturn(1);
        when(mockResultSet.getString("testo")).thenReturn("Risposta corretta");
        when(mockResultSet.getBoolean("isCorretta")).thenReturn(true);

        Risposta result = rispostaRepository.getRispostaByID(1);

        assertNotNull(result);
        assertEquals(1, result.getID());
        assertEquals("Risposta corretta", result.getTesto());
        assertTrue(result.isCorretta());
    }

    @Test
    void getRisposteByDomanda_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("rispostaID")).thenReturn(10, 11);
        when(mockResultSet.getString("testo")).thenReturn("Opzione A", "Opzione B");
        when(mockResultSet.getBoolean("isCorretta")).thenReturn(false, true);

        ArrayList<Risposta> risposte = rispostaRepository.getRisposteByDomanda(5);

        assertEquals(2, risposte.size());
        assertFalse(risposte.get(0).isCorretta());
        assertTrue(risposte.get(1).isCorretta());
        verify(mockPreparedStatement).setInt(1, 5);
    }

    @Test
    void insertRisposta_Success() throws Exception {
        Risposta r = new Risposta("Nuova Risposta");
        r.setCorretta(true);

        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(99);

        Risposta result = rispostaRepository.insertRisposta(r, 10);

        assertNotNull(result);
        assertEquals(99, result.getID());
        verify(mockPreparedStatement).setString(1, "Nuova Risposta");
        verify(mockPreparedStatement).setBoolean(2, true);
        verify(mockPreparedStatement).setInt(3, 10);
    }

    @Test
    void updateRisposta_Success() throws Exception {
        Risposta r = new Risposta("Testo aggiornato");
        r.setID(50);
        r.setCorretta(false);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean updated = rispostaRepository.updateRisposta(r);

        assertTrue(updated);
        verify(mockPreparedStatement).setString(1, "Testo aggiornato");
        verify(mockPreparedStatement).setBoolean(2, false);
        verify(mockPreparedStatement).setInt(3, 50);
    }

    @Test
    void removeRisposta_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean removed = rispostaRepository.removeRisposta(1, 10);

        assertTrue(removed);
        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).setInt(2, 10);
    }
}