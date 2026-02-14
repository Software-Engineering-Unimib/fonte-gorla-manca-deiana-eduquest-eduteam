package dev.eduteam.eduquest.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.repositories.accounts.StudenteRepository;
import dev.eduteam.eduquest.services.accounts.StudenteService;

import java.util.List;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class StudenteServiceTest {

    @Mock
    private StudenteRepository studenteRepository;

    @InjectMocks
    private StudenteService studenteService;

    private Studente studente;

    @BeforeEach
    void setUp() {
        studente = new Studente("Marco", "Verdi", "mverdi123", "mverdi@email.com", "PasswordValida1!");
        studente.setAccountID(1);
    }

    // test di registrazione Studente
    @Test
    void registraStudenteValidoTest() {
        Studente nuovoStudente = new Studente("Marco", "Verdi", "mverdi123", "mverdi@email.com", "PasswordValida1!");
        nuovoStudente.setAccountID(1);
        nuovoStudente.setMediaPunteggio(0.0);
        when(studenteRepository.insertStudente(any(Studente.class))).thenReturn(nuovoStudente);

        Studente result = studenteService.registraStudente(studente);
        assertNotNull(result);
        assertEquals("Marco", result.getNome());
        assertEquals("Verdi", result.getCognome());
        assertEquals("mverdi123", result.getUserName());
        assertEquals("mverdi@email.com", result.getEmail());
        assertEquals(0.0, result.getMediaPunteggio());
        verify(studenteRepository, times(1)).insertStudente(any(Studente.class));
    }

    @Test
    void registraStudenteConMediaTest() {
        Studente studenteConMedia = new Studente("Luca", "Bianchi", "lbianchi123", "lbianchi@email.com",
                "PasswordValida1!");
        studenteConMedia.setMediaPunteggio(8.5);
        studenteConMedia.setAccountID(2);
        when(studenteRepository.insertStudente(any(Studente.class))).thenReturn(studenteConMedia);

        Studente result = studenteService.registraStudente(studente);
        assertNotNull(result);
        assertEquals(8.5, result.getMediaPunteggio());
        verify(studenteRepository, times(1)).insertStudente(any(Studente.class));
    }

    // test di recupero Studente per ID
    @Test
    void getByIdValidoTest() {
        Studente studenteEsistente = new Studente("Giovanni", "Neri", "gneri123", "gneri@email.com",
                "PasswordValida1!");
        studenteEsistente.setAccountID(1);
        studenteEsistente.setMediaPunteggio(0.0);
        when(studenteRepository.getStudenteByAccountID(1)).thenReturn(studenteEsistente);

        Studente result = studenteService.getById(1);
        assertNotNull(result);
        assertEquals("Giovanni", result.getNome());
        assertEquals("Neri", result.getCognome());
        assertEquals("gneri123", result.getUserName());
        assertEquals("gneri@email.com", result.getEmail());
        assertEquals(0.0, result.getMediaPunteggio());
        verify(studenteRepository, times(1)).getStudenteByAccountID(1);
    }

    @Test
    void getByIdNonEsistenteTest() {
        when(studenteRepository.getStudenteByAccountID(99999)).thenReturn(null);

        Studente result = studenteService.getById(99999);
        assertNull(result);
        verify(studenteRepository, times(1)).getStudenteByAccountID(99999);
    }

    // test di recupero tutti gli Studenti
    @Test
    void getAllStudentiTest() {
        List<Studente> studenti = new ArrayList<>();
        studenti.add(studente);
        when(studenteRepository.getAllStudenti()).thenReturn(studenti);

        List<Studente> result = studenteService.getAll();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(studenteRepository, times(1)).getAllStudenti();
    }

    // test di aggiornamento Studente
    @Test
    void aggiornaStudenteNomeTest() {
        Studente studenteOriginale = new Studente("Antonio", "Rossi", "arossi123", "arossi@email.com",
                "PasswordValida1!");
        studenteOriginale.setAccountID(1);

        studenteOriginale.setNome("AntonioBis");
        when(studenteRepository.updateStudente(studenteOriginale)).thenReturn(true);

        boolean result = studenteService.aggiornaStudente(studenteOriginale);
        assertTrue(result);
        assertEquals("AntonioBis", studenteOriginale.getNome());
        verify(studenteRepository, times(1)).updateStudente(studenteOriginale);
    }

    @Test
    void aggiornaStudenteMediaTest() {
        Studente studenteOriginale = new Studente("Paolo", "Gialli", "pgialli123", "pgialli@email.com",
                "PasswordValida1!");
        studenteOriginale.setAccountID(1);

        studenteOriginale.setMediaPunteggio(7.5);
        when(studenteRepository.updateStudente(studenteOriginale)).thenReturn(true);

        boolean result = studenteService.aggiornaStudente(studenteOriginale);
        assertTrue(result);
        assertEquals(7.5, studenteOriginale.getMediaPunteggio());
        verify(studenteRepository, times(1)).updateStudente(studenteOriginale);
    }

    @Test
    void aggiornaStudenteCompleroTest() {
        Studente studenteOriginale = new Studente("Fabio", "Blu", "fblu123", "fblu@email.com", "PasswordValida1!");
        studenteOriginale.setAccountID(1);

        studenteOriginale.setNome("FabioBis");
        studenteOriginale.setCognome("BluBis");
        studenteOriginale.setEmail("fblubis@email.com");
        studenteOriginale.setMediaPunteggio(9.0);

        when(studenteRepository.updateStudente(studenteOriginale)).thenReturn(true);

        boolean result = studenteService.aggiornaStudente(studenteOriginale);
        assertTrue(result);
        assertEquals("FabioBis", studenteOriginale.getNome());
        assertEquals("BluBis", studenteOriginale.getCognome());
        assertEquals("fblubis@email.com", studenteOriginale.getEmail());
        assertEquals(9.0, studenteOriginale.getMediaPunteggio());
        verify(studenteRepository, times(1)).updateStudente(studenteOriginale);
    }

    // test che verifica isDocente ritorna false
    @Test
    void studenteIsDocenteFalseTest() {
        Studente nuovoStudente = new Studente("Carlo", "Arancioni", "carancioni123", "carancioni@email.com",
                "PasswordValida1!");
        nuovoStudente.setAccountID(1);
        when(studenteRepository.insertStudente(any(Studente.class))).thenReturn(nuovoStudente);

        Studente result = studenteService.registraStudente(studente);
        assertFalse(result.isDocente());
        verify(studenteRepository, times(1)).insertStudente(any(Studente.class));
    }
}
