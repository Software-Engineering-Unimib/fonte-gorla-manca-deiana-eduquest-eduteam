package dev.eduteam.eduquest.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.DomandaMultipla;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.accounts.Docente;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class QuestionarioTest {

    private Questionario questionario;
    private ArrayList<Domanda> domande;
    private Docente docente;

    @BeforeEach
    void setUp() {
        domande = new ArrayList<>();
        domande.add(new DomandaMultipla("Qual è la capitale dell'Italia?"));
        domande.add(new DomandaMultipla("Quanti continenti ci sono?"));
        docente = new Docente("Test", "Docente", "tdoc", "t@doc.it", "Pass123!");
        docente.setInsegnamento("Geografia");
        questionario = new Questionario("Geografia", "Questionario base di geografia", domande, docente,
                Questionario.Difficulty.Medio);
    }

    @Test
    void testCostruttore() {
        assertNotNull(questionario);
        assertEquals("Geografia", questionario.getNome());
        assertEquals("Questionario base di geografia", questionario.getDescrizione());
        assertEquals(2, questionario.getNumeroDomande());
        assertNotNull(questionario.getElencoDomande());
        assertEquals(2, questionario.getElencoDomande().size());
        assertEquals("Geografia", questionario.getMateria());
    }

    @Test
    void testDataCreazione() {
        assertEquals(LocalDate.now(), questionario.getDataCreazione());
    }

    @Test
    void testSettersEGetters() {
        // Test ID
        questionario.setID(1);
        assertEquals(1, questionario.getID());

        // Test Nome
        questionario.setNome("Nuovo Nome");
        assertEquals("Nuovo Nome", questionario.getNome());

        // Test Nome null
        questionario.setNome(null);
        assertNull(questionario.getNome());
        questionario.setNome("Nome Valido");

        // Test Descrizione
        questionario.setDescrizione("Nuova descrizione");
        assertEquals("Nuova descrizione", questionario.getDescrizione());

        // Test Difficoltà
        questionario.setLivelloDiff(Questionario.Difficulty.Difficile);
        assertEquals(Questionario.Difficulty.Difficile, questionario.getLivelloDifficulty());

        // Test Materia tramite cambio Docente
        Docente nuovoDocente = new Docente("Mario", "Verdi", "mverdi", "m@prova.eduqeust", "Pw1!");
        nuovoDocente.setInsegnamento("Storia");
        questionario.setDocente(nuovoDocente);
        assertEquals("Storia", questionario.getMateria());

        // Test Data Creazione
        LocalDate nuovaData = LocalDate.of(2025, 1, 15);
        questionario.setDataCreazione(nuovaData);
        assertEquals(nuovaData, questionario.getDataCreazione());

        // Test Elenco Domande - getNumeroDomande() ritorna la dimensione dell'elenco
        ArrayList<Domanda> elencoDomande = questionario.getElencoDomande();
        assertNotNull(elencoDomande);
        assertEquals(2, elencoDomande.size());
        assertEquals(2, questionario.getNumeroDomande());
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
        Questionario questionarioVuoto = new Questionario("Quiz Vuoto", "Un quiz senza domande", domandeVuota,
                docente, Questionario.Difficulty.Facile);
        assertEquals(0, questionarioVuoto.getNumeroDomande());
        assertTrue(questionarioVuoto.getElencoDomande().isEmpty());
        assertEquals(Questionario.Difficulty.Facile, questionarioVuoto.getLivelloDifficulty());

        // Questionario con molte domande
        ArrayList<Domanda> molteDomande = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            molteDomande.add(new DomandaMultipla("Domanda " + (i + 1)));
        }
        Questionario questionarioGrande = new Questionario("Quiz Grande", "Quiz con molte domande", molteDomande,
                docente, Questionario.Difficulty.Difficile);
        assertEquals(30, questionarioGrande.getNumeroDomande());
    }
}
