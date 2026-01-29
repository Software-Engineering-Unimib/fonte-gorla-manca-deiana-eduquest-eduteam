package dev.eduteam.eduquest.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.eduteam.eduquest.models.questionari.Risposta;

import static org.junit.jupiter.api.Assertions.*;

class RispostaTest {

    private Risposta risposta;

    @BeforeEach
    void setUp() {
        risposta = new Risposta("Prova");
    }

    @Test
    void testCostruttore() {
        assertNotNull(risposta);
        assertEquals("Prova", risposta.getTesto());
    }

    @Test
    void testSettersEGetters() {
        // Test ID
        risposta.setID(1);
        assertEquals(1, risposta.getID());

        // Test Testo
        risposta.setTesto("Milano");
        assertEquals("Milano", risposta.getTesto());

        // Test ID multiplo
        risposta.setID(99);
        assertEquals(99, risposta.getID());
    }

    @Test
    void testValidazioni() {
        // Testo null deve lanciare eccezione
        assertThrows(IllegalArgumentException.class, () -> risposta.setTesto(null),
                "Il testo di una risposta non puo' essere nullo");

        // Testo vuoto è valido
        assertDoesNotThrow(() -> risposta.setTesto(""));
    }

    @Test
    void testCostruttoriMultipli() {
        Risposta risposta1 = new Risposta("Parigi");
        Risposta risposta2 = new Risposta("Berlino");

        assertEquals("Parigi", risposta1.getTesto());
        assertEquals("Berlino", risposta2.getTesto());
        assertNotEquals(risposta1.getTesto(), risposta2.getTesto());
    }

    @Test
    void testModificaTestoMultipla() {
        risposta.setTesto("Primo testo");
        assertEquals("Primo testo", risposta.getTesto());

        risposta.setTesto("Secondo testo");
        assertEquals("Secondo testo", risposta.getTesto());

        risposta.setTesto("Terzo testo");
        assertEquals("Terzo testo", risposta.getTesto());
    }

    @Test
    void testCostruttoreConDiversiTesti() {
        String[] testi = { "Risposta A", "Risposta B", "Risposta C" };
        for (String testo : testi) {
            Risposta r = new Risposta(testo);
            assertEquals(testo, r.getTesto());
        }
    }
}
