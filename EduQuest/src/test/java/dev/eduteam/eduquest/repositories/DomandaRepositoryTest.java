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

import dev.eduteam.eduquest.models.questionari.*;
import dev.eduteam.eduquest.repositories.questionari.DomandaRepository;
import dev.eduteam.eduquest.repositories.questionari.RispostaRepository;

@ExtendWith(MockitoExtension.class)
public class DomandaRepositoryTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @Mock
    private RispostaRepository rispostaRepository;

    @InjectMocks
    private DomandaRepository domandaRepository;

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
    void getDomandaByID_ReturnsDomandaMultipla() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        when(mockResultSet.getInt("tipo")).thenReturn(1);
        when(mockResultSet.getInt("domandaID")).thenReturn(10);
        when(mockResultSet.getString("testo")).thenReturn("Qual è la capitale d'Italia?");
        when(mockResultSet.getInt("punteggio")).thenReturn(2);

        when(rispostaRepository.getRisposteByDomanda(10)).thenReturn(new ArrayList<>());

        Domanda result = domandaRepository.getDomandaByID(10);

        assertNotNull(result);
        assertTrue(result instanceof DomandaMultipla);
        assertEquals("Qual è la capitale d'Italia?", result.getTesto());
        assertEquals(2, result.getPunteggio());
    }

    @Test
    void getDomandeByQuestionario_ReturnsList() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simuliamo 2 domande nel database (Multipla e Vero/Falso)
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("tipo")).thenReturn(1, 3);
        when(mockResultSet.getInt("domandaID")).thenReturn(101, 102);
        when(mockResultSet.getString("testo")).thenReturn("Testo 1", "Testo 2");

        ArrayList<Domanda> domande = domandaRepository.getDomandeByQuestionario(1);

        assertEquals(2, domande.size());
        assertTrue(domande.get(0) instanceof DomandaMultipla);
        assertTrue(domande.get(1) instanceof DomandaVeroFalso);
    }

    @Test
    void insertDomanda_Success() throws Exception {
        Domanda domanda = new DomandaMultipla("Domanda test");
        domanda.setPunteggio(5);

        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(99);

        Domanda result = domandaRepository.insertDomanda(domanda, 1);

        assertNotNull(result);
        assertEquals(99, result.getID());
        verify(mockPreparedStatement).setInt(eq(1), eq(1));
        verify(mockPreparedStatement).setString(eq(2), eq("Domanda test"));
    }

    @Test
    void removeDomanda_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean deleted = domandaRepository.removeDomanda(10, 1);

        assertTrue(deleted);
        verify(mockPreparedStatement).setInt(1, 10);
    }

    @Test
    void updateDomanda_Success() throws Exception {
        Domanda d = new DomandaVeroFalso("Nuovo Testo");
        d.setID(10);
        d.setPunteggio(10);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean updated = domandaRepository.updateDomanda(d);

        assertTrue(updated);
        verify(mockPreparedStatement).setString(1, "Nuovo Testo");
        verify(mockPreparedStatement).setInt(2, 10);
    }
}