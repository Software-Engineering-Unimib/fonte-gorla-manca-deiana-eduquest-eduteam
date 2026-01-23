package dev.eduteam.eduquest.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DomandaTest {

    private Domanda domanda;

    @BeforeEach
    void setUp() {
        domanda = new Domanda("Qual è la capitale dell'Italia?");
    }

    @Test
    void testCostruttore() {
        assertNotNull(domanda);
        assertEquals("Qual è la capitale dell'Italia?", domanda.getTesto());
        assertEquals(0, domanda.getNumeroRisposte());
        assertNotNull(domanda.getElencoRisposte());
        assertTrue(domanda.getElencoRisposte().isEmpty());
        assertNull(domanda.getRispostaCorretta());
    }

    @Test
    void testSettersEGetters() {
        // Test ID
        domanda.setID(1);
        assertEquals(1, domanda.getID());

        // Test Testo
        domanda.setTesto("Quanti giorni mancano a Febbrario?");
        assertEquals("Quanti giorni mancano a Febbrario?", domanda.getTesto());

        // Test Numero Risposte
        domanda.setNumeroRisposte(4);
        assertEquals(4, domanda.getNumeroRisposte());

        // Test Elenco Risposte
        ArrayList<Risposta> elencoRisposte = domanda.getElencoRisposte();
        assertNotNull(elencoRisposte);
        assertTrue(elencoRisposte.isEmpty());
    }

    @Test
    void testValidazioni() {
        // Testo null deve lanciare eccezione
        assertThrows(IllegalArgumentException.class, () -> domanda.setTesto(null),
                "Il testo di una domanda non puo' essere nullo");

        // Numero risposte negativo deve lanciare eccezione
        assertThrows(IllegalArgumentException.class, () -> domanda.setNumeroRisposte(-1),
                "Il numero di risposte non puo' essere negativo");

        // Numero risposte zero è valido
        assertDoesNotThrow(() -> domanda.setNumeroRisposte(0));
    }

    @Test
    void testRispostaCorretta() {
        // Inizialmente null
        assertNull(domanda.getRispostaCorretta());

        // Dopo aver assegnato una risposta corretta
        Risposta rispostaCorretta = new Risposta("Prova");
        domanda.setRispostaCorretta(rispostaCorretta);
        assertNotNull(domanda.getRispostaCorretta());
        assertEquals("Prova", domanda.getRispostaCorretta().getTesto());
    }

    @Test
    void testCostruttoriMultipli() {
        Domanda domanda1 = new Domanda("Prima domanda");
        Domanda domanda2 = new Domanda("Seconda domanda");

        assertEquals("Prima domanda", domanda1.getTesto());
        assertEquals("Seconda domanda", domanda2.getTesto());
        assertNotEquals(domanda1.getTesto(), domanda2.getTesto());
    }

    @Test
    void testModificaTestoMultipla() {
        domanda.setTesto("Primo testo");
        assertEquals("Primo testo", domanda.getTesto());

        domanda.setTesto("Secondo testo");
        assertEquals("Secondo testo", domanda.getTesto());
    }
}
