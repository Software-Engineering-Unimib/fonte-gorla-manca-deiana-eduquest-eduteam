package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.repositories.questionari.RispostaRepository;
import dev.eduteam.eduquest.services.questionari.RispostaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RispostaServiceTest {

    @Mock
    private RispostaRepository rispostaRepository;

    @InjectMocks
    private RispostaService rispostaService;

    private Risposta risposta;

    @BeforeEach
    void setUp() {
        risposta = new Risposta("Roma");
        risposta.setID(1);
    }

    @Test
    void testGetRisposteByDomanda() {
        int domandaID = 1;
        ArrayList<Risposta> risposte = new ArrayList<>();

        Risposta r1 = new Risposta("Roma");
        r1.setID(1);
        Risposta r2 = new Risposta("Milano");
        r2.setID(2);
        Risposta r3 = new Risposta("Napoli");
        r3.setID(3);

        risposte.add(r1);
        risposte.add(r2);
        risposte.add(r3);

        when(rispostaRepository.getRisposteByDomanda(domandaID)).thenReturn(risposte);

        ArrayList<Risposta> result = rispostaService.getRisposteByDomanda(domandaID);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(3, result.size());
        verify(rispostaRepository, times(1)).getRisposteByDomanda(domandaID);
    }

    @Test
    void testGetRispostaById() {
        int rispostaID = 1;
        when(rispostaRepository.getRispostaByID(rispostaID)).thenReturn(risposta);

        Risposta result = rispostaService.getRispostaById(rispostaID);
        assertNotNull(result);
        assertEquals("Roma", result.getTesto());
        assertEquals(1, result.getID());
        verify(rispostaRepository, times(1)).getRispostaByID(rispostaID);
    }

    @Test
    void testAggiungiRisposta() {
        int domandaID = 1;
        Risposta nuovaRisposta = new Risposta("Nuova Risposta");
        nuovaRisposta.setID(2);
        nuovaRisposta.setCorretta(false);

        when(rispostaRepository.insertRisposta(any(Risposta.class), eq(domandaID))).thenReturn(nuovaRisposta);

        Risposta result = rispostaService.aggiungiRisposta(domandaID);
        assertNotNull(result);
        assertEquals(2, result.getID());
        assertEquals("Nuova Risposta", result.getTesto());
        assertFalse(result.isCorretta());
        verify(rispostaRepository, times(1)).insertRisposta(any(Risposta.class), eq(domandaID));
    }

    @Test
    void testRimuoviRisposta() {
        int domandaID = 1;
        int rispostaID = 1;
        when(rispostaRepository.removeRisposta(domandaID, rispostaID)).thenReturn(true);

        boolean result = rispostaService.rimuoviRisposta(domandaID, rispostaID);

        assertTrue(result);
        verify(rispostaRepository, times(1)).removeRisposta(domandaID, rispostaID);
    }

    @Test
    void testRimuoviRispostaFallito() {
        int domandaID = 1;
        int rispostaID = 999;
        when(rispostaRepository.removeRisposta(domandaID, rispostaID)).thenReturn(false);

        boolean result = rispostaService.rimuoviRisposta(domandaID, rispostaID);
        assertFalse(result);
        verify(rispostaRepository, times(1)).removeRisposta(domandaID, rispostaID);
    }

    @Test
    void testModificaTesto() {
        String nuovoTesto = "Milano";
        when(rispostaRepository.updateRisposta(risposta)).thenReturn(true);

        boolean result = rispostaService.modificaTesto(risposta, nuovoTesto);
        assertTrue(result);
        assertEquals(nuovoTesto, risposta.getTesto());
        verify(rispostaRepository, times(1)).updateRisposta(risposta);
    }

    @Test
    void testModificaTestoNull() {
        assertThrows(IllegalArgumentException.class,
                () -> rispostaService.modificaTesto(risposta, null));
    }
}
