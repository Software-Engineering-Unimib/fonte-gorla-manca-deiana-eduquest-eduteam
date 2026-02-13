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
import dev.eduteam.eduquest.models.questionari.Esercitazione;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.repositories.questionari.EsercitazioneRepository;

@ExtendWith(MockitoExtension.class)
public class EsercitazioneRepositoryTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private EsercitazioneRepository esercitazioneRepository;

    private MockedStatic<ConnectionSingleton> mockedStatic;
    private Docente mockDocente;

    @BeforeEach
    void setUp() throws Exception {
        mockedStatic = mockStatic(ConnectionSingleton.class);
        ConnectionSingleton cs = mock(ConnectionSingleton.class);
        when(cs.getConnection()).thenReturn(mockConnection);
        mockedStatic.when(ConnectionSingleton::getInstance).thenReturn(cs);

        mockDocente = new Docente("Luigi", "Verdi", "lverdi", "l@test.it", "password");
        mockDocente.setInsegnamento("Fisica");
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    void insertEsercitazione_Success() throws Exception {

        Esercitazione es = new Esercitazione(
                "Esercizio Moto",
                "Descrizione",
                new ArrayList<>(),
                mockDocente,
                Questionario.Difficulty.Facile);
        es.setNoteDidattiche("Ripassa le leggi di Newton");

        PreparedStatement psPadre = mock(PreparedStatement.class);
        PreparedStatement psFiglio = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(psPadre);
        when(mockConnection.prepareStatement(anyString())).thenReturn(psFiglio);
        when(psPadre.executeUpdate()).thenReturn(1);
        when(psPadre.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(77);

        when(psFiglio.executeUpdate()).thenReturn(1);

        Esercitazione result = esercitazioneRepository.insertEsercitazione(es);

        assertNotNull(result);
        assertEquals(77, result.getID());
        verify(psFiglio).setInt(1, 77);
        verify(psFiglio).setString(2, "Ripassa le leggi di Newton");
    }

    @Test
    void insertEsercitazione_FatherFails() throws Exception {
        Esercitazione es = new Esercitazione("A", "B", new ArrayList<>(), mockDocente, Questionario.Difficulty.Facile);

        when(mockConnection.prepareStatement(anyString(), anyInt())).thenThrow(new SQLException());

        Esercitazione result = esercitazioneRepository.insertEsercitazione(es);
        assertNull(result);
    }

    @Test
    void updateNoteEsercitazione_Success() throws Exception {
        Esercitazione es = new Esercitazione("A", "B", new ArrayList<>(), mockDocente, Questionario.Difficulty.Facile);
        es.setID(77);
        es.setNoteDidattiche("Nuove note");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean updated = esercitazioneRepository.updateNoteEsercitazione(es);

        assertTrue(updated);
        verify(mockPreparedStatement).setString(1, "Nuove note");
        verify(mockPreparedStatement).setInt(2, 77);
    }
}