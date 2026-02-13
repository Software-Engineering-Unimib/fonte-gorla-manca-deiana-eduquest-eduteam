package dev.eduteam.eduquest.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.DomandaMultipla;
import dev.eduteam.eduquest.models.questionari.Esercitazione;
import dev.eduteam.eduquest.models.questionari.Questionario;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class EsercitazioneTest {

    private Esercitazione esercitazione;

    @BeforeEach
    void setUp() {
        ArrayList<Domanda> domande = new ArrayList<>();
        Domanda d1 = new DomandaMultipla("Domanda 1");
        Domanda d2 = new DomandaMultipla("Domanda 2");
        d1.setPunteggio(2);
        d2.setPunteggio(3);
        domande.add(d1);
        domande.add(d2);

        Docente docente = new Docente();
        docente.setInsegnamento("Matematica");

        esercitazione = new Esercitazione("Titolo", "Descrizione", domande, docente,
                Questionario.Difficulty.Medio);
    }

    @Test
    void testCostruttoreEProprietaBase() {
        assertNotNull(esercitazione);
        assertEquals("Titolo", esercitazione.getNome());
        assertEquals("Descrizione", esercitazione.getDescrizione());
        assertEquals(2, esercitazione.getNumeroDomande());
        assertEquals("Matematica", esercitazione.getMateria());
        assertEquals(Questionario.Difficulty.Medio, esercitazione.getLivelloDifficulty());
    }

    @Test
    void testNoteDidatticheSetGet() {
        assertNull(esercitazione.getNoteDidattiche());
        esercitazione.setNoteDidattiche("Segui gli appunti");
        assertEquals("Segui gli appunti", esercitazione.getNoteDidattiche());
    }

    @Test
    void testPunteggioMaxCalcolo() {
        // le due domande hanno punteggi 2 e 3 impostati in setUp
        assertEquals(5, esercitazione.getPunteggioMax());
    }

}
