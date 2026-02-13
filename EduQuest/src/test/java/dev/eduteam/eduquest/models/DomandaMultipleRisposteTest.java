package dev.eduteam.eduquest.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import dev.eduteam.eduquest.models.questionari.DomandaMultipleRisposte;
import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.models.questionari.Domanda;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class DomandaMultipleRisposteTest {

    private DomandaMultipleRisposte domanda;

    @BeforeEach
    void setUp() {
        domanda = new DomandaMultipleRisposte("Seleziona i numeri primi");
    }

    @Test
    void testCostruttoreETipo() {
        assertNotNull(domanda);
        assertEquals("Seleziona i numeri primi", domanda.getTesto());
        assertEquals(Domanda.Type.DOMANDA_MULTIPLE_RISPOSTE, domanda.getTipoDomanda());
        assertEquals(0, domanda.getNumeroRisposte());
    }

    @Test
    void testAggiungiERicercaRisposteCorrette() {
        Risposta r1 = new Risposta("2");
        Risposta r2 = new Risposta("3");
        Risposta r3 = new Risposta("4");

        r1.setCorretta(true);
        r2.setCorretta(true);

        domanda.addRisposta(r1);
        domanda.addRisposta(r2);
        domanda.addRisposta(r3);

        assertEquals(3, domanda.getNumeroRisposte());
        ArrayList<Risposta> elenco = domanda.getElencoRisposte();
        long countCorrette = elenco.stream().filter(Risposta::isCorretta).count();
        assertEquals(2, countCorrette);
    }
}
