package dev.eduteam.eduquest.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.eduteam.eduquest.models.Docente;
import java.util.List;

@SpringBootTest
public class DocenteServiceTest {

    @Autowired
    private DocenteService docenteService;

    // test di registrazione Docente
    @Test
    void registraDocenteValidoTest() {
        Docente docente = new Docente("Marco", "Verdi", "mverdi123", "mverdi@email.com", "PasswordValida1!");
        Docente docenteRegistrato = docenteService.registraDocente(docente);
        assertNotNull(docenteRegistrato);
        assertEquals("Marco", docenteRegistrato.getNome());
        assertEquals("Verdi", docenteRegistrato.getCognome());
        assertEquals("mverdi123", docenteRegistrato.getUserName());
        assertEquals("mverdi@email.com", docenteRegistrato.getEmail());
        assertNull(docenteRegistrato.getInsegnamento());
    }

    @Test
    void registraDocenteConInsegnamentoTest() {
        Docente docente = new Docente("Luca", "Bianchi", "lbianchi123", "lbianchi@email.com", "PasswordValida1!");
        docente.setInsegnamento("Matematica");
        Docente docenteRegistrato = docenteService.registraDocente(docente);
        assertNotNull(docenteRegistrato);
        assertEquals("Matematica", docenteRegistrato.getInsegnamento());
    }

    // test di recupero Docente per ID
    @Test
    void getByIDValidoTest() {
        Docente docente = new Docente("Giovanni", "Neri", "gneri123", "gneri@email.com", "PasswordValida1!");
        docente.setInsegnamento("Italiano");
        Docente docenteRegistrato = docenteService.registraDocente(docente);
        
        Docente docenteRecuperato = docenteService.getByID(docenteRegistrato.getAccountID());
        assertNotNull(docenteRecuperato);
        assertEquals("Giovanni", docenteRecuperato.getNome());
        assertEquals("Neri", docenteRecuperato.getCognome());
        assertEquals("gneri123", docenteRecuperato.getUserName());
        assertEquals("gneri@email.com", docenteRecuperato.getEmail());
        assertEquals("Italiano", docenteRecuperato.getInsegnamento());
    }

    @Test
    void getByIDNonEsistenteTest() {
        Docente docente = docenteService.getByID(99999);
        assertNull(docente);
    }

    // test di recupero tutti i Docenti
    @Test
    void getAllDocentiTest() {
        List<Docente> docenti = docenteService.getAll();
        assertNotNull(docenti);
        assertFalse(docenti.isEmpty());
    }

    // test di aggiornamento Docente
    @Test
    void aggiornaDocenteNomeTest() {
        Docente docente = new Docente("Antonio", "Rossi", "arossi123", "arossi@email.com", "PasswordValida1!");
        docente.setInsegnamento("Scienze");
        Docente docenteRegistrato = docenteService.registraDocente(docente);
        
        docenteRegistrato.setNome("AntonioBis");
        boolean aggiornato = docenteService.aggiornaDocente(docenteRegistrato);
        assertTrue(aggiornato);
        
        Docente docenteAggiornato = docenteService.getByID(docenteRegistrato.getAccountID());
        assertEquals("AntonioBis", docenteAggiornato.getNome());
    }

    @Test
    void aggiornaDocenteInsegnamentoTest() {
        Docente docente = new Docente("Paolo", "Gialli", "pgialli123", "pgialli@email.com", "PasswordValida1!");
        docente.setInsegnamento("Storia");
        Docente docenteRegistrato = docenteService.registraDocente(docente);
        
        docenteRegistrato.setInsegnamento("Geografia");
        boolean aggiornato = docenteService.aggiornaDocente(docenteRegistrato);
        assertTrue(aggiornato);
        
        Docente docenteAggiornato = docenteService.getByID(docenteRegistrato.getAccountID());
        assertEquals("Geografia", docenteAggiornato.getInsegnamento());
    }

    @Test
    void aggiornaDocenteCompletoTest() {
        Docente docente = new Docente("Fabio", "Blu", "fblu123", "fblu@email.com", "PasswordValida1!");
        docente.setInsegnamento("Fisica");
        Docente docenteRegistrato = docenteService.registraDocente(docente);
        
        docenteRegistrato.setNome("FabioBis");
        docenteRegistrato.setCognome("BluBis");
        docenteRegistrato.setEmail("fblubis@email.com");
        docenteRegistrato.setInsegnamento("Chimica");
        
        boolean aggiornato = docenteService.aggiornaDocente(docenteRegistrato);
        assertTrue(aggiornato);
        
        Docente docenteAggiornato = docenteService.getByID(docenteRegistrato.getAccountID());
        assertEquals("FabioBis", docenteAggiornato.getNome());
        assertEquals("BluBis", docenteAggiornato.getCognome());
        assertEquals("fblubis@email.com", docenteAggiornato.getEmail());
        assertEquals("Chimica", docenteAggiornato.getInsegnamento());
    }

    // test metodo aggiornaInsegnamento
    @Test
    void aggiornaInsegnamentoTest() {
        Docente docente = new Docente("Claudio", "Rossi", "crossi123", "crossi@email.com", "PasswordValida1!");
        docente.setInsegnamento("Letteratura");
        Docente docenteRegistrato = docenteService.registraDocente(docente);
        
        boolean aggiornato = docenteService.aggiornaInsegnamento(docenteRegistrato.getAccountID(), "Filosofia");
        assertTrue(aggiornato);
        
        Docente docenteAggiornato = docenteService.getByID(docenteRegistrato.getAccountID());
        assertEquals("Filosofia", docenteAggiornato.getInsegnamento());
    }

    @Test
    void aggiornaInsegnamentoDocenteNonEsistenteTest() {
        boolean aggiornato = docenteService.aggiornaInsegnamento(99999, "Matematica");
        assertFalse(aggiornato);
    }

    // test che verifica isDocente ritorna true
    @Test
    void docenteIsDocenteTrueTest() {
        Docente docente = new Docente("Carlo", "Arancioni", "carancioni123", "carancioni@email.com", "PasswordValida1!");
        Docente docenteRegistrato = docenteService.registraDocente(docente);
        assertTrue(docenteRegistrato.isDocente());
    }
}
