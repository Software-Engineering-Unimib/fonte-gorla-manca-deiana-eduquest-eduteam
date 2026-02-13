package dev.eduteam.eduquest.repositories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.*;
import dev.eduteam.eduquest.models.accounts.Account;
import dev.eduteam.eduquest.repositories.accounts.AccountRepository;
import dev.eduteam.eduquest.services.accounts.AccountFactory;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

class AccountRepositoryTest {

    private AccountRepository repository;
    private MockedStatic<ConnectionSingleton> mockedSingleton;

    @Mock
    private ConnectionSingleton connectionSingletonInstance;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        repository = new AccountRepository();

        mockedSingleton = mockStatic(ConnectionSingleton.class);
        mockedSingleton.when(ConnectionSingleton::getInstance).thenReturn(connectionSingletonInstance);
        when(connectionSingletonInstance.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        when(connection.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
    }

    @AfterEach
    void tearDown() {
        mockedSingleton.close();
    }

    @Test
    void testGetAccountByUserName_Success() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("tipo")).thenReturn("Studente");
        when(resultSet.getString("nome")).thenReturn("Mario");
        when(resultSet.getString("cognome")).thenReturn("Rossi");
        when(resultSet.getString("userName")).thenReturn("mario.rossi");
        when(resultSet.getString("email")).thenReturn("mario@test.com");
        when(resultSet.getString("password")).thenReturn("pass123");
        when(resultSet.getInt("accountID")).thenReturn(1);

        Account result = repository.getAccountByUserName("mario.rossi");

        assertNotNull(result);
        assertEquals("mario.rossi", result.getUserName());
        verify(preparedStatement).setString(1, "mario.rossi");
    }

    @Test
    void testExistsByEmail_True() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        boolean exists = repository.existsByEmail("test@email.com");

        assertTrue(exists);
    }

    @Test
    void testInsertAccount_Success() throws Exception {
        Account newAccount = AccountFactory.creaAccount("Luigi", "Verdi", "l.verdi", "luigi@test.com", "pass", false);

        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(100);

        int id = repository.insertAccount(newAccount, "Studente");

        assertEquals(100, id);
        verify(preparedStatement).setString(1, "Luigi");
    }

    @Test
    void testRemoveAccount_Success() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = repository.removeAccount(5);

        assertTrue(result);
        verify(preparedStatement).setInt(1, 5);
    }
}