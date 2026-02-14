package dev.eduteam.eduquest.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.DomandaMultipla;
import dev.eduteam.eduquest.models.questionari.Feedback;

import static org.junit.jupiter.api.Assertions.*;

public class FeedbackTest {

    private Feedback feedback;
    private Domanda domanda;

    @BeforeEach
    void setUp() {
        domanda = new DomandaMultipla("Test domanda");
        feedback = new Feedback(domanda, "Testo feedback");
    }

    @Test
    void testCostruttoreEGetters() {
        assertNotNull(feedback);
        assertEquals(domanda, feedback.getDomanda());
        assertEquals("Testo feedback", feedback.getTesto());
        // default ID dovrebbe essere 0
        assertEquals(0, feedback.getID());
    }

    @Test
    void testSetters() {
        feedback.setID(5);
        assertEquals(5, feedback.getID());

        Domanda altra = new DomandaMultipla("Altra");
        feedback.setDomanda(altra);
        assertEquals(altra, feedback.getDomanda());

        feedback.setTesto("Nuovo testo");
        assertEquals("Nuovo testo", feedback.getTesto());
    }

}
