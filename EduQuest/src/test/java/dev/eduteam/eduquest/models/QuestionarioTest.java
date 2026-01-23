package dev.eduteam.eduquest.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class QuestionarioTest {

    private Questionario questionario;
    private ArrayList<Domanda> domande;

    @BeforeEach
    void setUp() {
        domande = new ArrayList<>();
        domande.add(new Domanda("Qual è la capitale dell'Italia?"));
        domande.add(new Domanda("Quanti continenti ci sono?"));
        questionario = new Questionario("Geografia", "Questionario base di geografia", domande);
    }

    @Test
    void testCostruttore() {
        assertNotNull(questionario);
        assertEquals("Geografia", questionario.getNome());
        assertEquals("Questionario base di geografia", questionario.getDescrizione());
        assertEquals(2, questionario.getNumeroDomande());
        assertNotNull(questionario.getElencoDomande());
        assertEquals(2, questionario.getElencoDomande().size());
    }

    /*
     * Da rivedere
     * 
     * @Test
     * void testDataCreazione() {
     * assertEquals(LocalDate.now(), questionario.getDataCreazione());
     * }
     */

    @Test
    void testSettersEGetters() {
        // Test ID
        questionario.setID(1);
        assertEquals(1, questionario.getID());

        // Test Nome
        questionario.setNome("Nuovo Nome");
        assertEquals("Nuovo Nome", questionario.getNome());

        // Test Descrizione
        questionario.setDescrizione("Nuova descrizione");
        assertEquals("Nuova descrizione", questionario.getDescrizione());

        // Test Numero Domande
        questionario.setNumeroDomande(5);
        assertEquals(5, questionario.getNumeroDomande());

        // Test Elenco Domande
        ArrayList<Domanda> elencoDomande = questionario.getElencoDomande();
        assertNotNull(elencoDomande);
        assertEquals(2, elencoDomande.size());
    }

    @Test
    void testValidazioniSetters() {
        // Descrizione null deve lanciare eccezione
        assertThrows(IllegalArgumentException.class, () -> questionario.setDescrizione(null));

        // Numero domande negativo deve lanciare eccezione
        assertThrows(IllegalArgumentException.class, () -> questionario.setNumeroDomande(-1));

        // Numero domande zero è valido
        assertDoesNotThrow(() -> questionario.setNumeroDomande(0));
    }

    @Test
    void testCostruttoriEdgeCases() {
        // Questionario con domande vuote
        ArrayList<Domanda> domandeVuota = new ArrayList<>();
        Questionario questionarioVuoto = new Questionario("Quiz Vuoto", "Un quiz senza domande", domandeVuota);
        assertEquals(0, questionarioVuoto.getNumeroDomande());
        assertTrue(questionarioVuoto.getElencoDomande().isEmpty());

        // Questionario con molte domande
        ArrayList<Domanda> molteDomande = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            molteDomande.add(new Domanda("Domanda " + (i + 1)));
        }
        Questionario questionarioGrande = new Questionario("Quiz Grande", "Quiz con molte domande", molteDomande);
        assertEquals(30, questionarioGrande.getNumeroDomande());
        assertEquals(30, questionarioGrande.getElencoDomande().size());
    }
}
