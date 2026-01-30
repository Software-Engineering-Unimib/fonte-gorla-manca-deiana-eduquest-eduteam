package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.repositories.questionari.DomandaRepository;
import dev.eduteam.eduquest.repositories.questionari.RispostaRepository;
import dev.eduteam.eduquest.services.questionari.DomandaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DomandaServiceTest {

    @Mock
    private DomandaRepository domandaRepository;

    @Mock
    private RispostaRepository rispostaRepository;

    @InjectMocks
    private DomandaService domandaService;

    private Domanda domanda;

    @BeforeEach
    void setUp() {
        domanda = Domanda.createDomandaOfType(Domanda.Type.DOMANDA_MULTIPLA);
        domanda.setTesto("Qual è la capitale dell'Italia?");
        domanda.setID(1);
    }

    @Test
    void testGetDomandeComplete() {
        int questionarioID = 1;
        ArrayList<Domanda> domande = new ArrayList<>();
        domande.add(domanda);
        when(domandaRepository.getDomandeByQuestionario(questionarioID)).thenReturn(domande);
        when(rispostaRepository.getRisposteByDomanda(domanda.getID())).thenReturn(new ArrayList<>());

        ArrayList<Domanda> result = domandaService.getDomandeComplete(questionarioID);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(domandaRepository, times(1)).getDomandeByQuestionario(questionarioID);
    }

    @Test
    void testGetDomandaByIdCompleta() {
        int questionarioID = 1;
        int domandaID = 1;
        when(domandaRepository.getDomandaByID(questionarioID, domandaID)).thenReturn(domanda);
        when(rispostaRepository.getRisposteByDomanda(domandaID)).thenReturn(new ArrayList<>());

        Domanda result = domandaService.getDomandaByIdCompleta(questionarioID, domandaID);
        assertNotNull(result);
        assertEquals("Qual è la capitale dell'Italia?", result.getTesto());
    }

    @Test
    void testAggiungiDomanda() {
        int questionarioID = 1;
        Domanda.Type tipo = Domanda.Type.DOMANDA_MULTIPLA;

        when(domandaRepository.insertDomanda(any(Domanda.class), eq(questionarioID))).thenReturn(domanda);

        Domanda result = domandaService.aggiungiDomanda(questionarioID, tipo);
        assertNotNull(result);
        verify(domandaRepository).insertDomanda(any(Domanda.class), eq(questionarioID));
    }

    @Test
    void testRimuoviDomanda() {
        int questionarioID = 1;
        int domandaID = 1;
        when(domandaRepository.removeDomanda(domandaID, questionarioID)).thenReturn(true);

        boolean result = domandaService.rimuoviDomanda(questionarioID, domandaID);
        assertTrue(result);
    }

    @Test
    void testModificaTesto() {
        int domandaID = 1;
        String nuovoTesto = "Quanti giorni mancano a Febbraio?";

        // Mock del caricamento dal DB e del salvataggio
        when(domandaRepository.getDomandaByID(domandaID)).thenReturn(domanda);
        when(domandaRepository.updateDomanda(domanda)).thenReturn(true);

        boolean result = domandaService.modificaTesto(domandaID, nuovoTesto);
        assertTrue(result);
        assertEquals(nuovoTesto, domanda.getTesto());
        verify(domandaRepository).updateDomanda(domanda);
    }

    @Test
    void testSetRispostaCorretta() {
        int domandaID = 1;
        int rispostaID = 10;

        Risposta r = new Risposta("Roma");
        r.setID(rispostaID);
        r.setCorretta(false);

        ArrayList<Risposta> elenco = new ArrayList<>();
        elenco.add(r);

        when(domandaRepository.getDomandaByID(domandaID)).thenReturn(domanda);
        when(rispostaRepository.getRispostaByID(rispostaID)).thenReturn(r);
        when(rispostaRepository.getRisposteByDomanda(domandaID)).thenReturn(elenco);
        when(rispostaRepository.updateRisposta(any(Risposta.class))).thenReturn(true);

        boolean result = domandaService.setRispostaCorretta(domandaID, rispostaID);

        assertTrue(result);
        assertTrue(r.isCorretta());
        verify(rispostaRepository, atLeastOnce()).updateRisposta(any(Risposta.class));
    }

    @Test
    void testModificaTestoNull() {
        int domandaID = 1;
        when(domandaRepository.getDomandaByID(domandaID)).thenReturn(domanda);

        assertThrows(IllegalArgumentException.class,
                () -> domandaService.modificaTesto(domandaID, null));
    }

    @Test
    void testSetRispostaCorretta_RispostaInesistente() {
        int domandaID = 1;
        int rispostaID = 99;
        when(domandaRepository.getDomandaByID(domandaID)).thenReturn(domanda);
        // Se la risposta non esiste nel DB
        when(rispostaRepository.getRispostaByID(rispostaID)).thenReturn(null);

        boolean result = domandaService.setRispostaCorretta(domandaID, rispostaID);
        assertFalse(result);
    }

    @Test
    void testSetRispostaCorretta_VeroFalso() {
        Domanda domandaVF = Domanda.createDomandaOfType(Domanda.Type.DOMANDA_VERO_FALSO);
        domandaVF.setID(10);

        Risposta vero = new Risposta("Vero");
        vero.setID(1);
        vero.setCorretta(false);
        Risposta falso = new Risposta("Falso");
        falso.setID(2);
        falso.setCorretta(true); // Inizialmente la corretta è "Falso"

        ArrayList<Risposta> opzioni = new ArrayList<>();
        opzioni.add(vero);
        opzioni.add(falso);

        when(domandaRepository.getDomandaByID(10)).thenReturn(domandaVF);
        when(rispostaRepository.getRispostaByID(1)).thenReturn(vero);
        when(rispostaRepository.getRisposteByDomanda(10)).thenReturn(opzioni);
        when(rispostaRepository.updateRisposta(any(Risposta.class))).thenReturn(true);

        // Impostiamo "vero" come corretta
        boolean result = domandaService.setRispostaCorretta(10, 1);

        assertTrue(result);
        assertTrue(vero.isCorretta(), "Vero dovrebbe essere corretta");
        assertFalse(falso.isCorretta(), "Falso non dovrebbe più essere corretta");
        // Verifica che entrambi siano stati salvati nel DB
        verify(rispostaRepository, times(2)).updateRisposta(any(Risposta.class));
    }
}
