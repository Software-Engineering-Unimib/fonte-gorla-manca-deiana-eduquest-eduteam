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
import dev.eduteam.eduquest.models.questionari.Compitino;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.repositories.questionari.CompitinoRepository;

@ExtendWith(MockitoExtension.class)
public class CompitinoRepositoryTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    @Spy // Necessario per mockare il metodo 'super' della classe padre
    private CompitinoRepository compitinoRepository;

    private MockedStatic<ConnectionSingleton> mockedStatic;
    private Docente mockDocente;

    @BeforeEach
    void setUp() throws Exception {
        mockedStatic = mockStatic(ConnectionSingleton.class);
        ConnectionSingleton cs = mock(ConnectionSingleton.class);
        when(cs.getConnection()).thenReturn(mockConnection);
        mockedStatic.when(ConnectionSingleton::getInstance).thenReturn(cs);

        mockDocente = new Docente("Admin", "User", "admin", "a@t.com", "pw");
        mockDocente.setInsegnamento("Storia");
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    void insertCompitino_Success() throws Exception {
        Compitino c = new Compitino(
                "Parziale 1",
                "Test di metà corso",
                new ArrayList<>(),
                mockDocente,
                Questionario.Difficulty.Medio,
                LocalDate.now().plusDays(7),
                3);
        c.setID(50);

        PreparedStatement ps1 = mock(PreparedStatement.class);
        PreparedStatement ps2 = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps1);
        when(mockConnection.prepareStatement(anyString())).thenReturn(ps2);

        when(ps1.executeUpdate()).thenReturn(1);
        when(ps1.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(50);

        when(ps2.executeUpdate()).thenReturn(1);

        Compitino result = compitinoRepository.insertCompitino(c);

        assertNotNull(result);
        assertEquals(50, result.getID());

        verify(ps2).setDate(eq(2), any(Date.class));
        verify(ps2).setInt(3, 3);
    }

    @Test
    void countTentativi_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(2);

        int count = compitinoRepository.countTentativi(1, 50);

        assertEquals(2, count);
    }
}