package dev.eduteam.eduquest.repositories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.eduteam.eduquest.models.questionari.*;
import dev.eduteam.eduquest.repositories.questionari.DomandaRepository;
import dev.eduteam.eduquest.repositories.questionari.FeedbackRepository;

@ExtendWith(MockitoExtension.class)
public class FeedbackRepositoryTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @Mock
    private DomandaRepository domandaRepository;

    @InjectMocks
    private FeedbackRepository feedbackRepository;

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
    void getFeedbackByDomanda_Success() throws Exception {
        int domandaID = 10;
        int feedbackID = 1;
        String testoFeedback = "Ottima risposta, continua così!";

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("feedbackID")).thenReturn(feedbackID);
        when(mockResultSet.getString("testo")).thenReturn(testoFeedback);

        Domanda mockDomanda = new DomandaMultipla("Testo Domanda");
        mockDomanda.setID(domandaID);
        when(domandaRepository.getDomandaByID(domandaID)).thenReturn(mockDomanda);

        Feedback result = feedbackRepository.getFeedbackByDomanda(domandaID);

        assertNotNull(result);
        assertEquals(feedbackID, result.getID());
        assertEquals(testoFeedback, result.getTesto());
        assertEquals(domandaID, result.getDomanda().getID());

        verify(mockPreparedStatement).setInt(1, domandaID);
        verify(domandaRepository).getDomandaByID(domandaID);
    }

    @Test
    void insertFeedback_Success() throws Exception {
        Domanda d = new DomandaVeroFalso("La terra è piatta?");
        d.setID(50);
        Feedback f = new Feedback(d, "Spiegazione scientifica...");

        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(999);

        Feedback result = feedbackRepository.insertFeedback(f);

        assertNotNull(result);
        assertEquals(999, result.getID());
        verify(mockPreparedStatement).setString(1, "Spiegazione scientifica...");
        verify(mockPreparedStatement).setInt(2, 50);
    }

    @Test
    void getFeedbackByDomanda_NotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Feedback result = feedbackRepository.getFeedbackByDomanda(123);

        assertNull(result);
    }
}