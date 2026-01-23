package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Risposta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomandaServiceTest {

    private DomandaService domandaService;
    private Domanda domanda;

    @BeforeEach
    void setUp() {
        domandaService = new DomandaService();
        domanda = domandaService.creaDomanda("Qual è la capitale dell'Italia?", 4);
    }

    @Test
    void testCreaDomanda() {
        assertNotNull(domanda);
        assertEquals("Qual è la capitale dell'Italia?", domanda.getTesto());
        assertNotNull(domanda.getID());
        assertTrue(domanda.getID() >= 0);
        assertEquals(0, domanda.getNumeroRisposte());
    }

    @Test
    void testCreaDomandaDiverse() {
        Domanda domanda1 = domandaService.creaDomanda("Domanda 1", 3);
        Domanda domanda2 = domandaService.creaDomanda("Domanda 2", 4);

        assertEquals("Domanda 1", domanda1.getTesto());
        assertEquals("Domanda 2", domanda2.getTesto());
        assertNotEquals(domanda1.getTesto(), domanda2.getTesto());
    }

    @Test
    void testModificaTesto() {
        String nuovoTesto = "Quanti giorni mancano a Febbraio?";
        domandaService.modificaTesto(domanda, nuovoTesto);
        assertEquals(nuovoTesto, domanda.getTesto());
    }

    @Test
    void testModificaTestoNull() {
        assertThrows(IllegalArgumentException.class,
                () -> domandaService.modificaTesto(domanda, null));
    }

    @Test
    void testAggiungiRisposta() {
        int numeroRisposteIniziale = domanda.getNumeroRisposte();
        domandaService.aggiungiRisposta(domanda, "Prova");

        assertEquals(numeroRisposteIniziale + 1, domanda.getNumeroRisposte());
        assertTrue(domanda.getElencoRisposte().size() > 0);
    }

    @Test
    void testAggiungiMultipleRisposte() {
        domandaService.aggiungiRisposta(domanda, "Milano");
        domandaService.aggiungiRisposta(domanda, "Roma");
        domandaService.aggiungiRisposta(domanda, "Torino");

        assertEquals(3, domanda.getNumeroRisposte());
        assertEquals(3, domanda.getElencoRisposte().size());
    }

    @Test
    void testRimuoviRisposta() {
        domandaService.aggiungiRisposta(domanda, "Milano");
        domandaService.aggiungiRisposta(domanda, "Roma");

        Risposta rispostaDaRimuovere = domanda.getElencoRisposte().get(0);
        int numeroRisposteIniziale = domanda.getNumeroRisposte();

        domandaService.rimuoviRisposta(domanda, rispostaDaRimuovere);

        assertEquals(numeroRisposteIniziale - 1, domanda.getNumeroRisposte());
        assertFalse(domanda.getElencoRisposte().contains(rispostaDaRimuovere));
    }

    @Test
    void testSetRispostaCorretta() {
        domandaService.aggiungiRisposta(domanda, "Roma");
        Risposta rispostaAggiunta = domanda.getElencoRisposte().get(0);

        domandaService.setRispostaCorretta(domanda, rispostaAggiunta);

        assertNotNull(domanda.getRispostaCorretta());
        assertEquals("Roma", domanda.getRispostaCorretta().getTesto());
    }

    @Test
    void testSetRispostaCorrettaNonPresente() {
        Risposta rispostaNonPresente = new Risposta("Parigi");
        domandaService.setRispostaCorretta(domanda, rispostaNonPresente);

        assertNull(domanda.getRispostaCorretta());
    }

    @Test
    void testAggiungiERimuoviRisposte() {
        domandaService.aggiungiRisposta(domanda, "Risposta 1");
        domandaService.aggiungiRisposta(domanda, "Risposta 2");
        domandaService.aggiungiRisposta(domanda, "Risposta 3");

        assertEquals(3, domanda.getNumeroRisposte());

        Risposta risposta1 = domanda.getElencoRisposte().get(0);
        domandaService.rimuoviRisposta(domanda, risposta1);

        assertEquals(2, domanda.getNumeroRisposte());
        assertFalse(domanda.getElencoRisposte().contains(risposta1));
    }

    @Test
    void testCicloCompletoCreazione() {
        Domanda nuovaDomanda = domandaService.creaDomanda("Capitale d'Italia", 4);

        domandaService.aggiungiRisposta(nuovaDomanda, "Roma");
        domandaService.aggiungiRisposta(nuovaDomanda, "Milano");
        domandaService.aggiungiRisposta(nuovaDomanda, "Napoli");
        domandaService.aggiungiRisposta(nuovaDomanda, "Torino");

        assertEquals(4, nuovaDomanda.getNumeroRisposte());

        Risposta rispostaCorretta = nuovaDomanda.getElencoRisposte().get(0);
        domandaService.setRispostaCorretta(nuovaDomanda, rispostaCorretta);

        assertNotNull(nuovaDomanda.getRispostaCorretta());
        assertEquals("Roma", nuovaDomanda.getRispostaCorretta().getTesto());
    }
}
