package dev.eduteam.eduquest.repositories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.repositories.accounts.DocenteRepository;
import dev.eduteam.eduquest.repositories.accounts.AccountRepository;

@ExtendWith(MockitoExtension.class)
public class DocenteRepositoryTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private DocenteRepository docenteRepository;

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
    void getDocenteByAccountID_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        when(mockResultSet.getString("nome")).thenReturn("Luca");
        when(mockResultSet.getString("cognome")).thenReturn("Bianchi");
        when(mockResultSet.getString("userName")).thenReturn("lbianchi");
        when(mockResultSet.getString("email")).thenReturn("luca@test.it");
        when(mockResultSet.getString("password")).thenReturn("password123");

        when(mockResultSet.getInt("accountID")).thenReturn(1);
        when(mockResultSet.getString("insegnamento")).thenReturn("Matematica");

        Docente d = docenteRepository.getDocenteByAccountID(1);

        assertNotNull(d);
        assertEquals("Luca", d.getNome());
        assertEquals("Matematica", d.getInsegnamento());
    }

    @Test
    void insertDocente_Success() throws Exception {
        Docente docente = new Docente("Anna", "Rossi", "arossi", "a@t.com", "pw");
        docente.setInsegnamento("Storia");

        when(accountRepository.insertAccount(any(), anyString())).thenReturn(50);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Docente result = docenteRepository.insertDocente(docente);

        assertNotNull(result);
        assertEquals(50, result.getAccountID());
        verify(mockPreparedStatement).setInt(1, 50);
    }

    @Test
    void getNumeroTotQuestCreati_ReturnsCount() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(5);

        int count = docenteRepository.getNumeroTotQuestCreati(11);

        assertEquals(5, count);
    }

    @Test
    void getDocente_HandlesException() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

        Docente d = docenteRepository.getDocenteByAccountID(1);

        assertNull(d);
    }
}