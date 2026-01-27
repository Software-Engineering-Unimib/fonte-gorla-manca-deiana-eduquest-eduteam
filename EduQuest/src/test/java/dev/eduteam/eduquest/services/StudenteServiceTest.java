package dev.eduteam.eduquest.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.eduteam.eduquest.models.Studente;
import java.util.List;

@SpringBootTest
public class StudenteServiceTest {

    @Autowired
    private StudenteService studenteService;

    // test di registrazione Studente
    @Test
    void registraStudenteValidoTest() {
        Studente studente = new Studente("Marco", "Verdi", "mverdi123", "mverdi@email.com", "PasswordValida1!");
        Studente studenteRegistrato = studenteService.registraStudente(studente);
        assertNotNull(studenteRegistrato);
        assertEquals("Marco", studenteRegistrato.getNome());
        assertEquals("Verdi", studenteRegistrato.getCognome());
        assertEquals("mverdi123", studenteRegistrato.getUserName());
        assertEquals("mverdi@email.com", studenteRegistrato.getEmail());
        assertEquals(0.0, studenteRegistrato.getMediaPunteggio());
    }

    @Test
    void registraStudenteConMediaTest() {
        Studente studente = new Studente("Luca", "Bianchi", "lbianchi123", "lbianchi@email.com", "PasswordValida1!");
        studente.setMediaPunteggio(8.5);
        Studente studenteRegistrato = studenteService.registraStudente(studente);
        assertNotNull(studenteRegistrato);
        assertEquals(8.5, studenteRegistrato.getMediaPunteggio());
    }

    // test di recupero Studente per ID
    @Test
    void getByIdValidoTest() {
        Studente studente = new Studente("Giovanni", "Neri", "gneri123", "gneri@email.com", "PasswordValida1!");
        Studente studenteRegistrato = studenteService.registraStudente(studente);
        
        Studente studenteRecuperato = studenteService.getById(studenteRegistrato.getAccountID());
        assertNotNull(studenteRecuperato);
        assertEquals("Giovanni", studenteRecuperato.getNome());
        assertEquals("Neri", studenteRecuperato.getCognome());
        assertEquals("gneri123", studenteRecuperato.getUserName());
        assertEquals("gneri@email.com", studenteRecuperato.getEmail());
        assertEquals(0.0, studenteRecuperato.getMediaPunteggio());
    }

    @Test
    void getByIdNonEsistenteTest() {
        Studente studente = studenteService.getById(99999);
        assertNull(studente);
    }

    // test di recupero tutti gli Studenti
    @Test
    void getAllStudentiTest() {
        List<Studente> studenti = studenteService.getAll();
        assertNotNull(studenti);
        assertFalse(studenti.isEmpty());
    }

    // test di aggiornamento Studente
    @Test
    void aggiornaStudenteNomeTest() {
        Studente studente = new Studente("Antonio", "Rossi", "arossi123", "arossi@email.com", "PasswordValida1!");
        Studente studenteRegistrato = studenteService.registraStudente(studente);
        
        studenteRegistrato.setNome("AntonioBis");
        boolean aggiornato = studenteService.aggiornaStudente(studenteRegistrato);
        assertTrue(aggiornato);
        
        Studente studenteAggiornato = studenteService.getById(studenteRegistrato.getAccountID());
        assertEquals("AntonioBis", studenteAggiornato.getNome());
    }

    @Test
    void aggiornaStudenteMediaTest() {
        Studente studente = new Studente("Paolo", "Gialli", "pgialli123", "pgialli@email.com", "PasswordValida1!");
        Studente studenteRegistrato = studenteService.registraStudente(studente);
        
        studenteRegistrato.setMediaPunteggio(7.5);
        boolean aggiornato = studenteService.aggiornaStudente(studenteRegistrato);
        assertTrue(aggiornato);
        
        Studente studenteAggiornato = studenteService.getById(studenteRegistrato.getAccountID());
        assertEquals(7.5, studenteAggiornato.getMediaPunteggio());
    }

    @Test
    void aggiornaStudenteCompleroTest() {
        Studente studente = new Studente("Fabio", "Blu", "fblu123", "fblu@email.com", "PasswordValida1!");
        Studente studenteRegistrato = studenteService.registraStudente(studente);
        
        studenteRegistrato.setNome("FabioBis");
        studenteRegistrato.setCognome("BluBis");
        studenteRegistrato.setEmail("fblubis@email.com");
        studenteRegistrato.setMediaPunteggio(9.0);
        
        boolean aggiornato = studenteService.aggiornaStudente(studenteRegistrato);
        assertTrue(aggiornato);
        
        Studente studenteAggiornato = studenteService.getById(studenteRegistrato.getAccountID());
        assertEquals("FabioBis", studenteAggiornato.getNome());
        assertEquals("BluBis", studenteAggiornato.getCognome());
        assertEquals("fblubis@email.com", studenteAggiornato.getEmail());
        assertEquals(9.0, studenteAggiornato.getMediaPunteggio());
    }

    // test che verifica isDocente ritorna false
    @Test
    void studenteIsDocenteFalseTest() {
        Studente studente = new Studente("Carlo", "Arancioni", "carancioni123", "carancioni@email.com", "PasswordValida1!");
        Studente studenteRegistrato = studenteService.registraStudente(studente);
        assertFalse(studenteRegistrato.isDocente());
    }
}
