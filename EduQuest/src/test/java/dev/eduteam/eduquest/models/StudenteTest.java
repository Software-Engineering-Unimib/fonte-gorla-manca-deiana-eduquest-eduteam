package dev.eduteam.eduquest.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.eduteam.eduquest.models.accounts.Studente;

import static org.junit.jupiter.api.Assertions.*;

class StudenteTest {

    private Studente studente;

    @BeforeEach
    void setUp() {
        studente = new Studente("Anna", "Neri", "aneri", "anna.neri@example.com", "pwd123");
    }

    @Test
    void testCostruttore() {
        assertNotNull(studente);
        assertEquals("Anna", studente.getNome());
        assertEquals("Neri", studente.getCognome());
        assertEquals("aneri", studente.getUserName());
        assertEquals("anna.neri@example.com", studente.getEmail());
        assertEquals("pwd123", studente.getPassword());
        // accountID non impostato dal costruttore parametrico
        assertEquals(0, studente.getAccountID());
        // valori specifici di Studente
        assertEquals(0.0, studente.getMediaPunteggio());
        assertEquals(0, studente.getEduPoints());
    }

    @Test
    void testSettersEGetters() {
        studente.setAccountID(13);
        assertEquals(13, studente.getAccountID());

        studente.setNome("Paolo");
        assertEquals("Paolo", studente.getNome());

        studente.setCognome("Rossi");
        assertEquals("Rossi", studente.getCognome());

        studente.setUserName("prossi");
        assertEquals("prossi", studente.getUserName());

        studente.setEmail("paolo.rossi@example.com");
        assertEquals("paolo.rossi@example.com", studente.getEmail());

        studente.setPassword("newpass");
        assertEquals("newpass", studente.getPassword());

        studente.setMediaPunteggio(7.5);
        assertEquals(7.5, studente.getMediaPunteggio());

        studente.setEduPoints(250);
        assertEquals(250, studente.getEduPoints());
    }

    @Test
    void testDefaultConstructorNulls() {
        Studente s = new Studente();

        assertNotNull(s);
        assertEquals(0, s.getAccountID());
        assertNull(s.getNome());
        assertNull(s.getCognome());
        assertNull(s.getUserName());
        assertNull(s.getEmail());
        assertNull(s.getPassword());
        assertEquals(0.0, s.getMediaPunteggio());
        assertEquals(0, s.getEduPoints());
    }

    @Test
    void testSetEduPointsNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> studente.setEduPoints(-10));
    }

    @Test
    void testIsDocente() {
        assertFalse(studente.isDocente());
    }

}
