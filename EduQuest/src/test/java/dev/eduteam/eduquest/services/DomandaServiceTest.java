package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Risposta;
import dev.eduteam.eduquest.repositories.DomandaRepository;

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
class DomandaServiceTest {

    @Mock
    private DomandaRepository domandaRepository;

    @InjectMocks
    private DomandaService domandaService;

    private Domanda domanda;

    @BeforeEach
    void setUp() {
        domanda = new Domanda("Qual è la capitale dell'Italia?");
        domanda.setID(1);
    }

    @Test
    void testGetDomande() {
        int questionarioID = 1;
        ArrayList<Domanda> domande = new ArrayList<>();
        domande.add(domanda);
        when(domandaRepository.getDomandeByQuestionario(questionarioID)).thenReturn(domande);

        ArrayList<Domanda> result = domandaService.getDomande(questionarioID);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(domandaRepository, times(1)).getDomandeByQuestionario(questionarioID);
    }

    @Test
    void testGetDomandaById() {
        int questionarioID = 1;
        int domandaID = 1;
        when(domandaRepository.getDomandaByID(questionarioID, domandaID)).thenReturn(domanda);

        Domanda result = domandaService.getDomandaById(questionarioID, domandaID);
        assertNotNull(result);
        assertEquals("Qual è la capitale dell'Italia?", result.getTesto());
        verify(domandaRepository, times(1)).getDomandaByID(questionarioID, domandaID);
    }

    @Test
    void testAggiungiDomanda() {
        int questionarioID = 1;
        Domanda nuovaDomanda = new Domanda("");
        nuovaDomanda.setID(2);
        when(domandaRepository.insertDomanda(any(Domanda.class), eq(questionarioID))).thenReturn(nuovaDomanda);

        Domanda result = domandaService.aggiungiDomanda(questionarioID);
        assertNotNull(result);
        assertEquals(2, result.getID());
        verify(domandaRepository, times(1)).insertDomanda(any(Domanda.class), eq(questionarioID));
    }

    @Test
    void testRimuoviDomanda() {
        int questionarioID = 1;
        int domandaID = 1;
        when(domandaRepository.removeDomanda(domandaID, questionarioID)).thenReturn(true);

        boolean result = domandaService.rimuoviDomanda(questionarioID, domandaID);
        assertTrue(result);
        verify(domandaRepository, times(1)).removeDomanda(domandaID, questionarioID);
    }

    @Test
    void testModificaTesto() {
        String nuovoTesto = "Quanti giorni mancano a Febbraio?";
        when(domandaRepository.updateDomanda(domanda)).thenReturn(true);

        boolean result = domandaService.modificaTesto(domanda, nuovoTesto);
        assertTrue(result);
        assertEquals(nuovoTesto, domanda.getTesto());
        verify(domandaRepository, times(1)).updateDomanda(domanda);
    }

    @Test
    void testModificaTestoNull() {
        assertThrows(IllegalArgumentException.class,
                () -> domandaService.modificaTesto(domanda, null));
    }

    @Test
    void testModificaRispostaCorretta() {
        Risposta risposta = new Risposta("Roma");
        risposta.setID(1);
        domanda.getElencoRisposte().add(risposta);
        when(domandaRepository.updateDomanda(domanda)).thenReturn(true);

        boolean result = domandaService.modificaRispostaCorretta(domanda, risposta);
        assertTrue(result);
        assertNotNull(domanda.getRispostaCorretta());
        assertEquals("Roma", domanda.getRispostaCorretta().getTesto());
        verify(domandaRepository, times(1)).updateDomanda(domanda);
    }

    @Test
    void testModificaRispostaNonPresente() {
        Risposta risposta = new Risposta("Parigi");
        when(domandaRepository.updateDomanda(domanda)).thenReturn(true);

        boolean result = domandaService.modificaRispostaCorretta(domanda, risposta);
        assertTrue(result);
        assertNull(domanda.getRispostaCorretta());
    }
}
