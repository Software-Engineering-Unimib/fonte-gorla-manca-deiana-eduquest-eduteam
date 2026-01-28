package dev.eduteam.eduquest.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        DomandaMultipla domandaMultipla = (DomandaMultipla) domanda;
        assertNull(domandaMultipla.getRispostaCorretta());

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

        domanda.setNumeroRisposte(4);
        assertEquals(4, domanda.getNumeroRisposte());

        ArrayList<Risposta> elencoRisposte = domanda.getElencoRisposte();
        assertNotNull(elencoRisposte);
        assertTrue(elencoRisposte.isEmpty());
    }

    @Test
    void testValidazioni() {
        // Testo null deve lanciare eccezione
        assertThrows(IllegalArgumentException.class, () -> domanda.setTesto(null),
                "Il testo di una domanda non puo' essere nullo");

        // Numero risposte negativo deve lanciare eccezione
        assertThrows(IllegalArgumentException.class, () -> domanda.setNumeroRisposte(-1),
                "Il numero di risposte non puo' essere negativo");

        // Numero risposte zero è valido
        assertDoesNotThrow(() -> domanda.setNumeroRisposte(0));
    }

    @Test
    void testRispostaCorretta() {
        DomandaMultipla domandaMultipla = (DomandaMultipla) domanda;

        assertNull(domandaMultipla.getRispostaCorretta());

        Risposta rispostaCorretta = new Risposta("Roma");
        domandaMultipla.setRispostaCorretta(rispostaCorretta);
        assertNotNull(domandaMultipla.getRispostaCorretta());
        assertEquals("Roma", domandaMultipla.getRispostaCorretta().getTesto());

        // Test per DomandaMultipla specifica
        DomandaMultipla domandaMultipla2 = new DomandaMultipla("Chi è il presidente?");
        Risposta risposta = new Risposta("Mattarella");
        domandaMultipla2.setRispostaCorretta(risposta);
        assertNotNull(domandaMultipla2.getRispostaCorretta());
        assertEquals("Mattarella", domandaMultipla2.getRispostaCorretta().getTesto());
    }

    @Test
    void testDomandaMultipleRisposteIstanziazione() {
        DomandaMultipleRisposte domandaMultipleRisposte = new DomandaMultipleRisposte("Quali città sono capitali?");

        assertNotNull(domandaMultipleRisposte);
        assertEquals("Quali città sono capitali?", domandaMultipleRisposte.getTesto());
        assertEquals(0, domandaMultipleRisposte.getNumeroRisposte());
        assertNotNull(domandaMultipleRisposte.getRisposteCorrette());
        assertTrue(domandaMultipleRisposte.getRisposteCorrette().isEmpty());
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
        // Ma numeroRisposte rimane 0 perché non è stato incrementato manualmente
        assertEquals(0, domandaMultipleRisposte.getNumeroRisposte());

        // Incremento manuale di numeroRisposte se necessario
        domandaMultipleRisposte.setNumeroRisposte(2);
        assertEquals(2, domandaMultipleRisposte.getNumeroRisposte());
    }

    @Test
    void testDomandaMultipleRisposteSetRisposteCorrette() {
        DomandaMultipleRisposte domandaMultipleRisposte = new DomandaMultipleRisposte("Quali sono numeri pari?");

        Risposta risposta1 = new Risposta("2");
        Risposta risposta2 = new Risposta("4");

        domandaMultipleRisposte.addRisposta(risposta1);
        domandaMultipleRisposte.addRisposta(risposta2);

        domandaMultipleRisposte.setRispostaCorretta(risposta1);
        domandaMultipleRisposte.setRispostaCorretta(risposta2);

        assertEquals(2, domandaMultipleRisposte.getRisposteCorrette().size());
        assertTrue(domandaMultipleRisposte.getRisposteCorrette().contains(risposta1));
        assertTrue(domandaMultipleRisposte.getRisposteCorrette().contains(risposta2));
    }

    // Test specifici per domande vero/falso

    @Test
    void testDomandaVeroFalsoIstanziazione() {
        DomandaVeroFalso domandaVeroFalso = new DomandaVeroFalso("L'Italia è in Europa");

        assertNotNull(domandaVeroFalso);
        assertEquals("L'Italia è in Europa", domandaVeroFalso.getTesto());
        assertEquals(2, domandaVeroFalso.getNumeroRisposte());
        assertNull(domandaVeroFalso.getRispostaCorretta());
        assertEquals(Domanda.Type.DOMANDA_VERO_FALSO, domandaVeroFalso.getTipoDomanda());
    }

    @Test
    void testDomandaVeroFalsoNumeroRisposteAlwaysTwo() {
        DomandaVeroFalso domandaVeroFalso = new DomandaVeroFalso("2+2=4?");
        // Numero risposte deve sempre essere 2 per Vero/Falso
        assertEquals(2, domandaVeroFalso.getNumeroRisposte());
        // Anche se proviamo a cambiarla, rimane 2
        domandaVeroFalso.setNumeroRisposte(5);
        assertEquals(2, domandaVeroFalso.getNumeroRisposte());
    }

    @Test
    void testDomandaVeroFalsoRispostaSingola() {
        DomandaVeroFalso domandaVeroFalso = new DomandaVeroFalso("Parigi è la capitale della Francia?");
        Risposta rispostaVero = new Risposta("Vero");

        domandaVeroFalso.setRispostaCorretta(rispostaVero);

        assertNotNull(domandaVeroFalso.getRispostaCorretta());
        assertEquals("Vero", domandaVeroFalso.getRispostaCorretta().getTesto());
    }

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
