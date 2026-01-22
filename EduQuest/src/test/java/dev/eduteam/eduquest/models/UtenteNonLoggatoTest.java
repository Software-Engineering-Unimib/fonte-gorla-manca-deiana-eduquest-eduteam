package dev.eduteam.eduquest.models;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class UtenteNonLoggatoTest {
        private UtenteNonLoggato utente;

        @BeforeEach
        void setup() {
                utente = new UtenteNonLoggato();
        }

        @Test
        void creazioneAccountStudenteValidoTest() {
                Account account = utente.creaAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                                "PasswordValida1!",
                                false);
                assertNotNull(account);
                assertFalse(account.isDocente());
        }

        @Test
        void creazioneAccountDocenteValidoTest() {
                Account account = utente.creaAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                                "PasswordValida1!",
                                true);
                assertNotNull(account);
                assertTrue(account.isDocente());
        }

        @Test
        void nomeNulloVuotoAccountTest() {
                IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class,
                                () -> utente.creaAccount(null, "Rossi", "mrossi123", "mrossi234@email.com",
                                                "PasswordValida1!",
                                                false));
                IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class,
                                () -> utente.creaAccount(" ", "Rossi", "mrossi123", "mrossi234@email.com",
                                                "PasswordValida1!",
                                                false));
                assertEquals("Nome obbligatorio", e1.getMessage());
                assertEquals("Nome obbligatorio", e2.getMessage());
        }

        @Test
        void cognomeNulloVuotoAccountTest() {
                IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class,
                                () -> utente.creaAccount("Mario", null, "mrossi123", "mrossi234@email.com",
                                                "PasswordValida1!",
                                                false));
                IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class,
                                () -> utente.creaAccount("Mario", " ", "mrossi123", "mrossi234@email.com",
                                                "PasswordValida1!",
                                                false));
                assertEquals("Cognome obbligatorio", e1.getMessage());
                assertEquals("Cognome obbligatorio", e2.getMessage());
        }

        @Test
        void userNameNonValido() {
                IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class,
                                () -> utente.creaAccount("Mario", "Rossi", null, "mrossi234@email.com",
                                                "PasswordValida1!",
                                                false));
                IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class,
                                () -> utente.creaAccount("Mario", "Rossi", " ", "mrossi234@email.com",
                                                "PasswordValida1!",
                                                false));
                IllegalArgumentException simboli = assertThrows(IllegalArgumentException.class,
                                () -> utente.creaAccount("Mario", "Rossi", "m_rossi123", "mrossi234@email.com",
                                                "PasswordValida1!",
                                                false));
                assertEquals("userName non valido", e1.getMessage());
                assertEquals("userName non valido", e2.getMessage());
                assertEquals("userName non valido", simboli.getMessage());
        }

        @Test
        void emailNonValida() {
                IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class,
                                () -> utente.creaAccount("Mario", "Rossi", "mrossi123", "mrossi234CHIOCCIOLAemail.com",
                                                "PasswordValida1!", false));
                IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class,
                                () -> utente.creaAccount("Mario", "Rossi", "mrossi123", null, "PasswordValida1!",
                                                false));
                IllegalArgumentException e3 = assertThrows(IllegalArgumentException.class,
                                () -> utente.creaAccount("Mario", "Rossi", "mrossi123", "@email.com",
                                                "PasswordValida1!", false));
                IllegalArgumentException e4 = assertThrows(IllegalArgumentException.class,
                                () -> utente.creaAccount("Mario", "Rossi", "mrossi123", "mrossi234@",
                                                "PasswordValida1!", false));
                IllegalArgumentException e5 = assertThrows(IllegalArgumentException.class,
                                () -> utente.creaAccount("Mario", "Rossi", "mrossi123", "mrossi234@email",
                                                "PasswordValida1!", false));
                assertEquals("email non valida", e1.getMessage());
                assertEquals("email non valida", e2.getMessage());
                assertEquals("email non valida", e3.getMessage());
                assertEquals("email non valida", e4.getMessage());
                assertEquals("email non valida", e5.getMessage());
        }

        @Test
        void passwordNonValida() {
                // pw corta
                IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class,
                                () -> utente.creaAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com", "Pass1!",
                                                false));
                // pw lunga
                IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class,
                                () -> utente.creaAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                                                "Passsssssssssssssss1!",
                                                false));
                // pw senza numero
                IllegalArgumentException e3 = assertThrows(IllegalArgumentException.class,
                                () -> utente.creaAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                                                "PasswordNonValida!",
                                                false));
                // pw senza Maiuscola
                IllegalArgumentException e4 = assertThrows(IllegalArgumentException.class,
                                () -> utente.creaAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                                                "passwordnonvalida1!",
                                                false));
                // pw senza simbolo
                IllegalArgumentException e5 = assertThrows(IllegalArgumentException.class,
                                () -> utente.creaAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                                                "PasswordNonValida1",
                                                false));
                // pw con spazio interno
                IllegalArgumentException e6 = assertThrows(IllegalArgumentException.class,
                                () -> utente.creaAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                                                "PasswordN Valida1!",
                                                false));
                // pw caso limite 8 caratteri
                Account accountMinPw = utente.creaAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                                "PwVali1!",
                                false);
                // pw caso limite 20 caratteri
                Account accountMaxPw = utente.creaAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                                "PasswordValidaaaaa1!",
                                false);
                assertEquals("password non valida", e1.getMessage());
                assertEquals("password non valida", e2.getMessage());
                assertEquals("password non valida", e3.getMessage());
                assertEquals("password non valida", e4.getMessage());
                assertEquals("password non valida", e5.getMessage());
                assertEquals("password non valida", e6.getMessage());
                assertNotNull(accountMinPw);
                assertNotNull(accountMaxPw);
        }
}
