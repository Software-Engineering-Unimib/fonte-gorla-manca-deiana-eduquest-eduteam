package dev.eduteam.eduquest.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.eduteam.eduquest.models.questionari.DomandaVeroFalso;
import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.models.questionari.Domanda;

import static org.junit.jupiter.api.Assertions.*;

public class DomandaVeroFalsoTest {

    private DomandaVeroFalso domanda;

    @BeforeEach
    void setUp() {
        domanda = new DomandaVeroFalso("L'acqua bolle a 100°C?");
    }

    @Test
    void testCostruttoreETipo() {
        assertNotNull(domanda);
        assertEquals("L'acqua bolle a 100°C?", domanda.getTesto());
        assertEquals(Domanda.Type.DOMANDA_VERO_FALSO, domanda.getTipoDomanda());
    }

    @Test
    void testAggiungiRisposteVeroFalso() {
        Risposta vero = new Risposta("Vero");
        Risposta falso = new Risposta("Falso");
        vero.setCorretta(true);

        domanda.addRisposta(vero);
        domanda.addRisposta(falso);

        assertEquals(2, domanda.getNumeroRisposte());
        assertTrue(domanda.getElencoRisposte().stream().anyMatch(Risposta::isCorretta));
    }
}
