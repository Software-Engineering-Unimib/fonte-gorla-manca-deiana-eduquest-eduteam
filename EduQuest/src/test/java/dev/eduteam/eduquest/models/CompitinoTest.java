package dev.eduteam.eduquest.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.questionari.Compitino;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.DomandaMultipla;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CompitinoTest {

    private Compitino compitino;
    private ArrayList<Domanda> domande;
    private Docente docente;

    @BeforeEach
    void setUp() {
        domande = new ArrayList<>();
        domande.add(new DomandaMultipla("Domanda 1"));
        domande.add(new DomandaMultipla("Domanda 2"));
        docente = new Docente("Mario", "Rossi", "mrossi", "m@edu.it", "Pass123!");
        docente.setInsegnamento("Matematica");
        compitino = new Compitino("Compitino prova", "Descrizione prova", domande, docente,
                Compitino.Difficulty.Facile, LocalDate.now().plusDays(7), 3);
    }

    @Test
    void testCostruttoreEGetters() {
        assertNotNull(compitino);
        assertEquals("Compitino prova", compitino.getNome());
        assertEquals("Descrizione prova", compitino.getDescrizione());
        assertEquals(2, compitino.getNumeroDomande());
        assertEquals(3, compitino.getTentativiMax());
        assertEquals(Compitino.Difficulty.Facile, compitino.getLivelloDifficulty());
        assertEquals("Matematica", compitino.getMateria());
        assertEquals(LocalDate.now().plusDays(7), compitino.getDataFine());
    }

    @Test
    void testSettersEGetters() {
        compitino.setID(42);
        assertEquals(42, compitino.getID());

        compitino.setNome("Nome nuovo");
        assertEquals("Nome nuovo", compitino.getNome());

        compitino.setDescrizione("Nuova descrizione");
        assertEquals("Nuova descrizione", compitino.getDescrizione());

        compitino.setPuntiBonus(10);
        assertEquals(10, compitino.getPuntiBonus());

        compitino.setAssegnatiPtBonus(true);
        assertTrue(compitino.getAssegnatiPtBonus());

        LocalDate nuovaData = LocalDate.of(2026, 6, 1);
        compitino.setDataFine(nuovaData);
        assertEquals(nuovaData, compitino.getDataFine());
    }

    @Test
    void testValidazioniSetTentativi() {
        assertThrows(IllegalArgumentException.class, () -> compitino.setTentativiMax(0));
        // valore minimo valido
        compitino.setTentativiMax(1);
        assertEquals(1, compitino.getTentativiMax());
    }

    @Test
    void testValidazioniPuntiBonus() {
        assertThrows(IllegalArgumentException.class, () -> compitino.setPuntiBonus(-5));
    }

}
