package dev.eduteam.eduquest.models;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AccountFactoryTest {
    @Test
    void creaDocenteQuandoTrue() {
        Account docente = AccountFactory.creaAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                "PasswordValida1!",
                true);
        assertNotNull(docente);
        assertTrue(docente instanceof Docente);
        assertTrue(docente.isDocente());
    }

    @Test
    void creaStudenteQuandoFalse() {
        Account studente = AccountFactory.creaAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                "PasswordValida1!",
                false);
        assertNotNull(studente);
        assertTrue(studente instanceof Studente);
        assertFalse(studente.isDocente());
    }

    @Test
    void correttezzaFlagDocente() {
        Account studente = AccountFactory.creaAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                "PasswordValida1!",
                false);
        Account docente = AccountFactory.creaAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                "PasswordValida1!",
                true);
        assertTrue(studente instanceof Studente);
        assertTrue(docente instanceof Docente);

    }

}
