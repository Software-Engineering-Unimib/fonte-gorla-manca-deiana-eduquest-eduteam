package dev.eduteam.eduquest.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.DomandaMultipla;
import dev.eduteam.eduquest.models.questionari.Risposta;

import static org.junit.jupiter.api.Assertions.*;

public class DomandaMultiplaTest {

    private Domanda domanda;

    @BeforeEach
    void setUp() {
        domanda = new DomandaMultipla("Qual è la capitale?");
    }

    @Test
    void testCostruttoreETipo() {
        assertNotNull(domanda);
        assertEquals("Qual è la capitale?", domanda.getTesto());
        assertEquals(Domanda.Type.DOMANDA_MULTIPLA, domanda.getTipoDomanda());
        assertEquals(0, domanda.getNumeroRisposte());
    }

    @Test
    void testAggiungiRisposte() {
        Risposta r1 = new Risposta("Roma");
        Risposta r2 = new Risposta("Milano");
        domanda.addRisposta(r1);
        domanda.addRisposta(r2);
        assertEquals(2, domanda.getNumeroRisposte());
        assertEquals(2, domanda.getElencoRisposte().size());
    }

    @Test
    void testSetTestoValidazione() {
        assertThrows(IllegalArgumentException.class, () -> domanda.setTesto(null));
    }
}
