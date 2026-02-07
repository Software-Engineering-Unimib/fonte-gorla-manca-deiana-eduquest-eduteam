package dev.eduteam.eduquest.models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.models.questionari.Compilazione;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.questionari.Risposta;

class CompilazioneTest {

    private Compilazione compilazione;
    private Studente studente;
    private Questionario questionario;
    private Docente docente;

    @BeforeEach
    void setUp() {
        studente = new Studente("Mario", "Rossi", "mrossi", "mario@email.com", "password123");
        docente = new Docente("Luigi", "Bianchi", "lbianchi", "luigi@email.com", "password456");
        docente.setInsegnamento("Matematica");

        ArrayList<Domanda> domande = new ArrayList<>();
        // Aggiungi alcune domande di test
        questionario = new Questionario("Test Questionario", "Descrizione test", domande, docente,
                Questionario.Difficulty.Facile);

        compilazione = new Compilazione(studente, questionario);
    }

    @Test
    void testConstructorInitializesFields() {
        assertNotNull(compilazione.getStudente());
        assertNotNull(compilazione.getQuestionario());
        assertEquals(studente, compilazione.getStudente());
        assertEquals(questionario, compilazione.getQuestionario());
        assertEquals("Matematica", compilazione.getQuestionario().getMateria());
    }

    @Test
    void testSetQuestionarioInitializesRisposteArray() {
        int numeroDomande = questionario.getNumeroDomande();
        assertEquals(numeroDomande, compilazione.getNumeroDomande());
        assertNotNull(compilazione.getRisposte());
        assertEquals(numeroDomande, compilazione.getRisposte().length);
    }

    @Test
    void testInitialStateValues() {
        assertFalse(compilazione.isCompletato());
        assertEquals(0, compilazione.getPunteggio());
    }

    @Test
    void testSettersAndGetters() {
        // Test ID
        compilazione.setID(5);
        assertEquals(5, compilazione.getID());

        // Test Studente
        Studente nuovoStudente = new Studente("Anna", "Verdi", "averdi", "anna@email.com", "pass789");
        compilazione.setStudente(nuovoStudente);
        assertEquals(nuovoStudente, compilazione.getStudente());

        // Test Questionario
        ArrayList<Domanda> nuoveDomande = new ArrayList<>();
        Questionario nuovoQuestionario = new Questionario("Nuovo Test", "Descrizione nuova", nuoveDomande, docente,
                Questionario.Difficulty.Difficile);
        compilazione.setQuestionario(nuovoQuestionario);
        assertEquals(nuovoQuestionario, compilazione.getQuestionario());

        // Test Completato
        assertFalse(compilazione.isCompletato());
        compilazione.setCompletato(true);
        assertTrue(compilazione.isCompletato());

        // Test Punteggio
        compilazione.setPunteggio(85);
        assertEquals(85, compilazione.getPunteggio());
    }

    @Test
    void testRisposteArrayPopulation() {
        Risposta[] risposte = compilazione.getRisposte();
        int expectedLength = compilazione.getNumeroDomande();
        assertEquals(expectedLength, risposte.length);

        // Verifica che l'array sia inizialmente vuoto (null elements)
        for (Risposta risposta : risposte) {
            assertNull(risposta);
        }
    }
}
