package dev.eduteam.eduquest.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.eduteam.eduquest.models.accounts.Account;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account("Mario", "Rossi", "mrossi", "mario.rossi@example.com", "pwd123") {
            @Override
            public boolean isDocente() {
                return false;
            }
        };
    }

    @Test
    void testCostruttore() {
        assertNotNull(account);
        assertEquals("Mario", account.getNome());
        assertEquals("Rossi", account.getCognome());
        assertEquals("mrossi", account.getUserName());
        assertEquals("mario.rossi@example.com", account.getEmail());
        assertEquals("pwd123", account.getPassword());
        // accountID non impostato dal costruttore parametrico
        assertEquals(0, account.getAccountID());
    }

    @Test
    void testSettersEGetters() {
        account.setAccountID(42);
        assertEquals(42, account.getAccountID());

        account.setNome("Luigi");
        assertEquals("Luigi", account.getNome());

        account.setCognome("Verdi");
        assertEquals("Verdi", account.getCognome());

        account.setUserName("lverdi");
        assertEquals("lverdi", account.getUserName());

        account.setEmail("luigi.verdi@example.com");
        assertEquals("luigi.verdi@example.com", account.getEmail());

        account.setPassword("newpass");
        assertEquals("newpass", account.getPassword());
    }

    @Test
    void testDefaultConstructorAccountIDZero() {
        Account a = new Account() {
            @Override
            public boolean isDocente() {
                return true;
            }
        };

        assertNotNull(a);
        assertEquals(0, a.getAccountID());
        assertNull(a.getNome());
        assertNull(a.getCognome());
        assertNull(a.getUserName());
        assertNull(a.getEmail());
        assertNull(a.getPassword());
    }

    @Test
    void testIsDocenteImplementation() {
        Account docente = new Account() {
            @Override
            public boolean isDocente() {
                return true;
            }
        };
        Account studente = new Account() {
            @Override
            public boolean isDocente() {
                return false;
            }
        };

        assertTrue(docente.isDocente());
        assertFalse(studente.isDocente());
    }

}
