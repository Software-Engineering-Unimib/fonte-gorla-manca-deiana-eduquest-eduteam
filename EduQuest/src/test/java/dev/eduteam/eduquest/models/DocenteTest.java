package dev.eduteam.eduquest.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.eduteam.eduquest.models.accounts.Docente;

import static org.junit.jupiter.api.Assertions.*;

class DocenteTest {

    private Docente docente;

    @BeforeEach
    void setUp() {
        docente = new Docente("Giovanni", "Bianchi", "gbianchi", "giovanni.bianchi@example.com", "pwd123");
    }

    @Test
    void testCostruttore() {
        assertNotNull(docente);
        assertEquals("Giovanni", docente.getNome());
        assertEquals("Bianchi", docente.getCognome());
        assertEquals("gbianchi", docente.getUserName());
        assertEquals("giovanni.bianchi@example.com", docente.getEmail());
        assertEquals("pwd123", docente.getPassword());
        // accountID non impostato dal costruttore parametrico
        assertEquals(0, docente.getAccountID());
        // insegnamento inizialmente nullo
        assertNull(docente.getInsegnamento());
    }

    @Test
    void testSettersEGetters() {
        docente.setAccountID(7);
        assertEquals(7, docente.getAccountID());

        docente.setNome("Luca");
        assertEquals("Luca", docente.getNome());

        docente.setCognome("Verdi");
        assertEquals("Verdi", docente.getCognome());

        docente.setUserName("lverdi");
        assertEquals("lverdi", docente.getUserName());

        docente.setEmail("luca.verdi@example.com");
        assertEquals("luca.verdi@example.com", docente.getEmail());

        docente.setPassword("newpass");
        assertEquals("newpass", docente.getPassword());

        docente.setInsegnamento("Matematica");
        assertEquals("Matematica", docente.getInsegnamento());
    }

    @Test
    void testDefaultConstructorNulls() {
        Docente d = new Docente();

        assertNotNull(d);
        assertEquals(0, d.getAccountID());
        assertNull(d.getNome());
        assertNull(d.getCognome());
        assertNull(d.getUserName());
        assertNull(d.getEmail());
        assertNull(d.getPassword());
        assertNull(d.getInsegnamento());
    }

    @Test
    void testIsDocente() {
        assertTrue(docente.isDocente());
    }

}
