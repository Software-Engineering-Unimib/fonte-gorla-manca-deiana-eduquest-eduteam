package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.Risposta;
import dev.eduteam.eduquest.repositories.RispostaRepository;

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
    void testGetDomande() {
        int domandaID = 1;
        ArrayList<Risposta> risposte = new ArrayList<>();
        risposte.add(risposta);
        when(rispostaRepository.getRisposteByDomanda(domandaID)).thenReturn(risposte);

        ArrayList<Risposta> result = rispostaService.getDomande(domandaID);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(rispostaRepository, times(1)).getRisposteByDomanda(domandaID);
    }

    @Test
    void testGetRispostaById() {
        int domandaID = 1;
        int rispostaID = 1;
        when(rispostaRepository.getRispostaByID(domandaID, rispostaID)).thenReturn(risposta);

        Risposta result = rispostaService.getRispostaById(domandaID, rispostaID);
        assertNotNull(result);
        assertEquals("Roma", result.getTesto());
        assertEquals(1, result.getID());
        verify(rispostaRepository, times(1)).getRispostaByID(domandaID, rispostaID);
    }

    @Test
    void testAggiungiRisposta() {
        int domandaID = 1;
        Risposta nuovaRisposta = new Risposta("");
        nuovaRisposta.setID(2);
        when(rispostaRepository.insertRisposta(any(Risposta.class), eq(domandaID))).thenReturn(nuovaRisposta);

        Risposta result = rispostaService.aggiungiRisposta(1, domandaID);
        assertNotNull(result);
        assertEquals(2, result.getID());
        verify(rispostaRepository, times(1)).insertRisposta(any(Risposta.class), eq(domandaID));
    }

    @Test
    void testRimuoviRisposta() {
        int domandaID = 1;
        int rispostaID = 1;
        when(rispostaRepository.removeRisposta(rispostaID, domandaID)).thenReturn(true);

        boolean result = rispostaService.rimuoviRisposta(1, domandaID, rispostaID);
        assertTrue(result);
        verify(rispostaRepository, times(1)).removeRisposta(rispostaID, domandaID);
    }

    @Test
    void testRimuoviRispostaFallito() {
        int domandaID = 1;
        int rispostaID = 999;
        when(rispostaRepository.removeRisposta(rispostaID, domandaID)).thenReturn(false);

        boolean result = rispostaService.rimuoviRisposta(1, domandaID, rispostaID);
        assertFalse(result);
        verify(rispostaRepository, times(1)).removeRisposta(rispostaID, domandaID);
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

    @Test
    void testGetMultipleRisposteByDomanda() {
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

        ArrayList<Risposta> result = rispostaService.getDomande(domandaID);
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(rispostaRepository, times(1)).getRisposteByDomanda(domandaID);
    }
}
