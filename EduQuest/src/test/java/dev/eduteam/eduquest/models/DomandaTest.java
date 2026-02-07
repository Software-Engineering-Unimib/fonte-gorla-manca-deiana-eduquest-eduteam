package dev.eduteam.eduquest.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.DomandaMultipla;
import dev.eduteam.eduquest.models.questionari.DomandaMultipleRisposte;
import dev.eduteam.eduquest.models.questionari.DomandaVeroFalso;
import dev.eduteam.eduquest.models.questionari.Risposta;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DomandaTest {

    private Domanda domanda;

    @BeforeEach
    void setUp() {
        domanda = new DomandaMultipla("Qual è la capitale dell'Italia?");
    }

    @Test
    void testCostruttore() {
        assertNotNull(domanda);
        assertEquals("Qual è la capitale dell'Italia?", domanda.getTesto());
        assertEquals(0, domanda.getNumeroRisposte());
        assertNotNull(domanda.getElencoRisposte());
        assertTrue(domanda.getElencoRisposte().isEmpty());

        // Verifica che si possono creare più istanze con testi diversi
        Domanda domanda1 = new DomandaMultipla("Prima domanda");
        Domanda domanda2 = new DomandaMultipla("Seconda domanda");
        assertEquals("Prima domanda", domanda1.getTesto());
        assertEquals("Seconda domanda", domanda2.getTesto());
    }

    @Test
    void testSettersEGetters() {
        domanda.setID(1);
        assertEquals(1, domanda.getID());

        domanda.setTesto("Quanti giorni mancano a Febbrario?");
        assertEquals("Quanti giorni mancano a Febbrario?", domanda.getTesto());

        // Aggiungere risposte per testare getNumeroRisposte
        Risposta risposta1 = new Risposta("Roma");
        domanda.addRisposta(risposta1);
        assertEquals(1, domanda.getNumeroRisposte());

        Risposta risposta2 = new Risposta("Milano");
        domanda.addRisposta(risposta2);
        assertEquals(2, domanda.getNumeroRisposte());

        ArrayList<Risposta> elencoRisposte = domanda.getElencoRisposte();
        assertNotNull(elencoRisposte);
        assertFalse(elencoRisposte.isEmpty());
        assertEquals(2, elencoRisposte.size());
    }

    @Test
    void testValidazioni() {
        // Testo null deve lanciare eccezione
        assertThrows(IllegalArgumentException.class, () -> domanda.setTesto(null),
                "Il testo di una domanda non puo' essere nullo");
    }

    // testRispostaCorretta eliminato - richiede metodi non disponibili in
    // DomandaMultipla

    @Test
    void testDomandaMultipleRisposteIstanziazione() {
        DomandaMultipleRisposte domandaMultipleRisposte = new DomandaMultipleRisposte("Quali città sono capitali?");

        assertNotNull(domandaMultipleRisposte);
        assertEquals("Quali città sono capitali?", domandaMultipleRisposte.getTesto());
        assertEquals(0, domandaMultipleRisposte.getNumeroRisposte());
        assertEquals(Domanda.Type.DOMANDA_MULTIPLE_RISPOSTE, domandaMultipleRisposte.getTipoDomanda());
    }

    @Test
    void testDomandaMultipleRisposteAggiungiRisposte() {
        DomandaMultipleRisposte domandaMultipleRisposte = new DomandaMultipleRisposte("Quali sono numeri pari?");

        Risposta risposta1 = new Risposta("2");
        Risposta risposta2 = new Risposta("4");

        domandaMultipleRisposte.addRisposta(risposta1);
        domandaMultipleRisposte.addRisposta(risposta2);

        // L'elenco contiene le risposte aggiunte
        assertEquals(2, domandaMultipleRisposte.getElencoRisposte().size());
        // getNumeroRisposte() restituisce la size dell'elenco
        assertEquals(2, domandaMultipleRisposte.getNumeroRisposte());
    }

    // testDomandaMultipleRisposteSetRisposteCorrette eliminato - richiede metodi
    // non disponibili

    // Test specifici per domande vero/falso

    @Test
    void testDomandaVeroFalsoIstanziazione() {
        DomandaVeroFalso domandaVeroFalso = new DomandaVeroFalso("L'Italia è in Europa");

        assertNotNull(domandaVeroFalso);
        assertEquals("L'Italia è in Europa", domandaVeroFalso.getTesto());
        assertEquals(Domanda.Type.DOMANDA_VERO_FALSO, domandaVeroFalso.getTipoDomanda());
    }

    // testDomandaVeroFalsoNumeroRisposteAlwaysTwo eliminato - richiede metodo
    // setNumeroRisposte non disponibile

    // testDomandaVeroFalsoRispostaSingola eliminato - richiede metodi
    // getRispostaCorretta/setRispostaCorretta non disponibili

    @Test
    void testCreateDomandaViaFactory() {
        Domanda domandaMultipla = Domanda.createDomandaOfType(Domanda.Type.DOMANDA_MULTIPLA);
        Domanda domandaMultipleRisposte = Domanda.createDomandaOfType(Domanda.Type.DOMANDA_MULTIPLE_RISPOSTE);
        Domanda domandaVeroFalso = Domanda.createDomandaOfType(Domanda.Type.DOMANDA_VERO_FALSO);

        assertNotNull(domandaMultipla);
        assertNotNull(domandaMultipleRisposte);
        assertNotNull(domandaVeroFalso);

        assertEquals(Domanda.Type.DOMANDA_MULTIPLA, domandaMultipla.getTipoDomanda());
        assertEquals(Domanda.Type.DOMANDA_MULTIPLE_RISPOSTE, domandaMultipleRisposte.getTipoDomanda());
        assertEquals(Domanda.Type.DOMANDA_VERO_FALSO, domandaVeroFalso.getTipoDomanda());
    }
}
