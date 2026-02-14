package dev.eduteam.eduquest.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.repositories.accounts.DocenteRepository;
import dev.eduteam.eduquest.services.accounts.DocenteService;

import java.util.List;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class DocenteServiceTest {

    @Mock
    private DocenteRepository docenteRepository;

    @InjectMocks
    private DocenteService docenteService;

    private Docente docente;

    @BeforeEach
    void setUp() {
        docente = new Docente("Marco", "Verdi", "mverdi123", "mverdi@email.com", "PasswordValida1!");
        docente.setAccountID(1);
    }

    // test di registrazione Docente
    @Test
    void registraDocenteValidoTest() {
        Docente nuovoDocente = new Docente("Marco", "Verdi", "mverdi123", "mverdi@email.com", "PasswordValida1!");
        nuovoDocente.setAccountID(1);
        when(docenteRepository.insertDocente(any(Docente.class))).thenReturn(nuovoDocente);

        Docente result = docenteService.registraDocente(docente);
        assertNotNull(result);
        assertEquals("Marco", result.getNome());
        assertEquals("Verdi", result.getCognome());
        assertEquals("mverdi123", result.getUserName());
        assertEquals("mverdi@email.com", result.getEmail());
        assertNull(result.getInsegnamento());
        verify(docenteRepository, times(1)).insertDocente(any(Docente.class));
    }

    @Test
    void registraDocenteConInsegnamentoTest() {
        Docente docenteConMateria = new Docente("Luca", "Bianchi", "lbianchi123", "lbianchi@email.com",
                "PasswordValida1!");
        docenteConMateria.setInsegnamento("Matematica");
        docenteConMateria.setAccountID(2);
        when(docenteRepository.insertDocente(any(Docente.class))).thenReturn(docenteConMateria);

        Docente result = docenteService.registraDocente(docente);
        assertNotNull(result);
        assertEquals("Matematica", result.getInsegnamento());
        verify(docenteRepository, times(1)).insertDocente(any(Docente.class));
    }

    // test di recupero Docente per ID
    @Test
    void getByIDValidoTest() {
        Docente docenteConMateria = new Docente("Giovanni", "Neri", "gneri123", "gneri@email.com", "PasswordValida1!");
        docenteConMateria.setInsegnamento("Italiano");
        docenteConMateria.setAccountID(1);
        when(docenteRepository.getDocenteByAccountID(1)).thenReturn(docenteConMateria);

        Docente result = docenteService.getByID(1);
        assertNotNull(result);
        assertEquals("Giovanni", result.getNome());
        assertEquals("Neri", result.getCognome());
        assertEquals("gneri123", result.getUserName());
        assertEquals("gneri@email.com", result.getEmail());
        assertEquals("Italiano", result.getInsegnamento());
        verify(docenteRepository, times(1)).getDocenteByAccountID(1);
    }

    @Test
    void getByIDNonEsistenteTest() {
        when(docenteRepository.getDocenteByAccountID(99999)).thenReturn(null);

        Docente result = docenteService.getByID(99999);
        assertNull(result);
        verify(docenteRepository, times(1)).getDocenteByAccountID(99999);
    }

    // test di recupero tutti i Docenti
    @Test
    void getAllDocentiTest() {
        List<Docente> docenti = new ArrayList<>();
        docenti.add(docente);
        when(docenteRepository.getAllDocenti()).thenReturn(docenti);

        List<Docente> result = docenteService.getAll();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(docenteRepository, times(1)).getAllDocenti();
    }

    // test di aggiornamento Docente
    @Test
    void aggiornaDocenteNomeTest() {
        Docente docenteOriginale = new Docente("Antonio", "Rossi", "arossi123", "arossi@email.com", "PasswordValida1!");
        docenteOriginale.setInsegnamento("Scienze");
        docenteOriginale.setAccountID(1);

        docenteOriginale.setNome("AntonioBis");
        when(docenteRepository.updateDocente(docenteOriginale)).thenReturn(true);

        boolean result = docenteService.aggiornaDocente(docenteOriginale);
        assertTrue(result);
        assertEquals("AntonioBis", docenteOriginale.getNome());
        verify(docenteRepository, times(1)).updateDocente(docenteOriginale);
    }

    @Test
    void aggiornaDocenteInsegnamentoTest() {
        Docente docenteOriginale = new Docente("Paolo", "Gialli", "pgialli123", "pgialli@email.com",
                "PasswordValida1!");
        docenteOriginale.setInsegnamento("Storia");
        docenteOriginale.setAccountID(1);

        docenteOriginale.setInsegnamento("Geografia");
        when(docenteRepository.updateDocente(docenteOriginale)).thenReturn(true);

        boolean result = docenteService.aggiornaDocente(docenteOriginale);
        assertTrue(result);
        assertEquals("Geografia", docenteOriginale.getInsegnamento());
        verify(docenteRepository, times(1)).updateDocente(docenteOriginale);
    }

    @Test
    void aggiornaDocenteCompletoTest() {
        Docente docenteOriginale = new Docente("Fabio", "Blu", "fblu123", "fblu@email.com", "PasswordValida1!");
        docenteOriginale.setInsegnamento("Fisica");
        docenteOriginale.setAccountID(1);

        docenteOriginale.setNome("FabioBis");
        docenteOriginale.setCognome("BluBis");
        docenteOriginale.setEmail("fblubis@email.com");
        docenteOriginale.setInsegnamento("Chimica");

        when(docenteRepository.updateDocente(docenteOriginale)).thenReturn(true);

        boolean result = docenteService.aggiornaDocente(docenteOriginale);
        assertTrue(result);
        assertEquals("FabioBis", docenteOriginale.getNome());
        assertEquals("BluBis", docenteOriginale.getCognome());
        assertEquals("fblubis@email.com", docenteOriginale.getEmail());
        assertEquals("Chimica", docenteOriginale.getInsegnamento());
        verify(docenteRepository, times(1)).updateDocente(docenteOriginale);
    }

    // test metodo aggiornaInsegnamento
    @Test
    void aggiornaInsegnamentoTest() {
        Docente docenteOriginale = new Docente("Claudio", "Rossi", "crossi123", "crossi@email.com", "PasswordValida1!");
        docenteOriginale.setInsegnamento("Letteratura");
        docenteOriginale.setAccountID(1);
        when(docenteRepository.getDocenteByAccountID(1)).thenReturn(docenteOriginale);
        when(docenteRepository.updateDocente(any(Docente.class))).thenReturn(true);

        boolean result = docenteService.aggiornaInsegnamento(1, "Filosofia");
        assertTrue(result);
        assertEquals("Filosofia", docenteOriginale.getInsegnamento());
        verify(docenteRepository, times(1)).getDocenteByAccountID(1);
        verify(docenteRepository, times(1)).updateDocente(any(Docente.class));
    }

    @Test
    void aggiornaInsegnamentoDocenteNonEsistenteTest() {
        when(docenteRepository.getDocenteByAccountID(99999)).thenReturn(null);

        boolean result = docenteService.aggiornaInsegnamento(99999, "Matematica");
        assertFalse(result);
        verify(docenteRepository, times(1)).getDocenteByAccountID(99999);
    }

    // test che verifica isDocente ritorna true
    @Test
    void docenteIsDocenteTrueTest() {
        Docente nuovoDocente = new Docente("Carlo", "Arancioni", "carancioni123", "carancioni@email.com",
                "PasswordValida1!");
        nuovoDocente.setAccountID(1);
        when(docenteRepository.insertDocente(any(Docente.class))).thenReturn(nuovoDocente);

        Docente result = docenteService.registraDocente(docente);
        assertTrue(result.isDocente());
        verify(docenteRepository, times(1)).insertDocente(any(Docente.class));
    }
}
