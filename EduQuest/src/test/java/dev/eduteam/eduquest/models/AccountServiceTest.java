package dev.eduteam.eduquest.models;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import dev.eduteam.eduquest.services.AccountService;

@SpringBootTest
public class AccountServiceTest {

        @Autowired
        private AccountService accountService;

        @Test
        void creazioneAccountStudenteValidoTest() {
                Account account = accountService.registraAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                                "PasswordValida1!",
                                false);
                assertNotNull(account);
                assertFalse(account.isDocente());
        }

        @Test
        void creazioneAccountDocenteValidoTest() {
                Account account = accountService.registraAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                                "PasswordValida1!",
                                true);
                assertNotNull(account);
                assertTrue(account.isDocente());
        }

        @Test
        void nomeNulloVuotoAccountTest() {
                IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount(null, "Rossi", "mrossi123", "mrossi234@email.com",
                                                "PasswordValida1!",
                                                false));
                IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount(" ", "Rossi", "mrossi123", "mrossi234@email.com",
                                                "PasswordValida1!",
                                                false));
                assertEquals("Nome obbligatorio", e1.getMessage());
                assertEquals("Nome obbligatorio", e2.getMessage());
        }

        @Test
        void cognomeNulloVuotoAccountTest() {
                IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", null, "mrossi123", "mrossi234@email.com",
                                                "PasswordValida1!",
                                                false));
                IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", " ", "mrossi123", "mrossi234@email.com",
                                                "PasswordValida1!",
                                                false));
                assertEquals("Cognome obbligatorio", e1.getMessage());
                assertEquals("Cognome obbligatorio", e2.getMessage());
        }

        @Test
        void userNameNonValido() {
                IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", "Rossi", null, "mrossi234@email.com",
                                                "PasswordValida1!",
                                                false));
                IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", "Rossi", " ", "mrossi234@email.com",
                                                "PasswordValida1!",
                                                false));
                IllegalArgumentException simboli = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", "Rossi", "m_rossi123",
                                                "mrossi234@email.com",
                                                "PasswordValida1!",
                                                false));
                assertEquals("userName non valido", e1.getMessage());
                assertEquals("userName non valido", e2.getMessage());
                assertEquals("userName non valido", simboli.getMessage());
        }

        @Test
        void emailNonValida() {
                IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", "Rossi", "mrossi123",
                                                "mrossi234CHIOCCIOLAemail.com",
                                                "PasswordValida1!", false));
                IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", "Rossi", "mrossi123", null,
                                                "PasswordValida1!",
                                                false));
                IllegalArgumentException e3 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", "Rossi", "mrossi123", "@email.com",
                                                "PasswordValida1!", false));
                IllegalArgumentException e4 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", "Rossi", "mrossi123", "mrossi234@",
                                                "PasswordValida1!", false));
                IllegalArgumentException e5 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", "Rossi", "mrossi123", "mrossi234@email",
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
                                () -> accountService.registraAccount("Mario", "Rossi", "mrossi123",
                                                "mrossi234@email.com", "Pass1!",
                                                false));
                // pw lunga
                IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", "Rossi", "mrossi123",
                                                "mrossi234@email.com",
                                                "Passsssssssssssssss1!",
                                                false));
                // pw senza numero
                IllegalArgumentException e3 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", "Rossi", "mrossi123",
                                                "mrossi234@email.com",
                                                "PasswordNonValida!",
                                                false));
                // pw senza Maiuscola
                IllegalArgumentException e4 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", "Rossi", "mrossi123",
                                                "mrossi234@email.com",
                                                "passwordnonvalida1!",
                                                false));
                // pw senza simbolo
                IllegalArgumentException e5 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", "Rossi", "mrossi123",
                                                "mrossi234@email.com",
                                                "PasswordNonValida1",
                                                false));
                // pw con spazio interno
                IllegalArgumentException e6 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", "Rossi", "mrossi123",
                                                "mrossi234@email.com",
                                                "PasswordN Valida1!",
                                                false));
                // pw caso limite 8 caratteri
                Account accountMinPw = accountService.registraAccount("Mario", "Rossi", "mrossi123",
                                "mrossi234@email.com",
                                "PwVali1!",
                                false);
                // pw caso limite 20 caratteri
                Account accountMaxPw = accountService.registraAccount("Mario", "Rossi", "mrossi123",
                                "mrossi234@email.com",
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

        @Test
        void aggiornaAccountSuccessoTest() {
                Account accountUpdated = accountService.aggiornaAccount("PincoPallino1", "PasswordValida1!",
                                "pinco_aggiornato", "pallo_aggiornato", "nuovaemail@prova.edu", "NuovaPass1!");
                assertNotNull(accountUpdated);
                assertEquals("pinco_aggiornato", accountUpdated.getNome());
                assertEquals("pallo_aggiornato", accountUpdated.getCognome());
                assertEquals("nuovaemail@prova.edu", accountUpdated.getEmail());
                assertEquals("NuovaPass1!", accountUpdated.getPassword());
        }

        @Test
        void aggiornaSoloNomeTest() {
                Account account = accountService.aggiornaAccount("PincoPallino1", "PasswordValida1!",
                                "NuovoNome", null, null, null);
                assertEquals("NuovoNome", account.getNome());
                assertEquals("pallo", account.getCognome());
                assertEquals("PincoPallo@prova.edu", account.getEmail());
                assertEquals("PasswordValida1!", account.getPassword());
        }

        @Test
        void aggiornaSoloCognomeTest() {
                Account account = accountService.aggiornaAccount("PincoPallino1", "PasswordValida1!",
                                null, "NuovoCognome", null, null);
                assertEquals("pinco", account.getNome());
                assertEquals("NuovoCognome", account.getCognome());
                assertEquals("PincoPallo@prova.edu", account.getEmail());
                assertEquals("PasswordValida1!", account.getPassword());
        }

        @Test
        void aggiornaSoloEmailTest() {
                Account account = accountService.aggiornaAccount("PincoPallino1", "PasswordValida1!",
                                null, null, "nuovamail@dominio.com", null);
                assertEquals("pinco", account.getNome());
                assertEquals("pallo", account.getCognome());
                assertEquals("nuovamail@dominio.com", account.getEmail());
                assertEquals("PasswordValida1!", account.getPassword());
        }

        @Test
        void aggiornaSoloPasswordTest() {
                Account account = accountService.aggiornaAccount("PincoPallino1", "PasswordValida1!",
                                null, null, null, "NuovaPassword2!");
                assertEquals("pinco", account.getNome());
                assertEquals("pallo", account.getCognome());
                assertEquals("PincoPallo@prova.edu", account.getEmail());
                assertEquals("NuovaPassword2!", account.getPassword());
        }

        @Test
        void aggiornaAccountPasswordAttualErratTest() {
                IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                                () -> accountService.aggiornaAccount("PincoPallino1", "PasswordSbagliata",
                                                "NuovoNome", null, null, null));
                assertEquals("Password attuale errata", e.getMessage());
        }

        @Test
        void aggiornaAccountNonTrovatoTest() {
                IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                                () -> accountService.aggiornaAccount("UsernameNonEsistente", "PasswordValida1!",
                                                "NuovoNome", null, null, null));
                assertEquals("Account non trovato", e.getMessage());
        }

        @Test
        void aggiornaEmailNonValidaTest() {
                IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.aggiornaAccount("PincoPallino1", "PasswordValida1!",
                                                null, null, "emailNonValida.com", null));
                IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.aggiornaAccount("PincoPallino1", "PasswordValida1!",
                                                null, null, "@email.com", null));
                assertEquals("Email non valida", e1.getMessage());
                assertEquals("Email non valida", e2.getMessage());
        }

        @Test
        void aggiornaPasswordNonValidaTest() {
                // password troppo corta
                IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.aggiornaAccount("PincoPallino1", "PasswordValida1!",
                                                null, null, null, "Pass1!"));
                // password senza maiuscola
                IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.aggiornaAccount("PincoPallino1", "PasswordValida1!",
                                                null, null, null, "passwordvalida1!"));
                // password senza numero
                IllegalArgumentException e3 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.aggiornaAccount("PincoPallino1", "PasswordValida1!",
                                                null, null, null, "PasswordValida!"));
                // password senza simbolo
                IllegalArgumentException e4 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.aggiornaAccount("PincoPallino1", "PasswordValida1!",
                                                null, null, null, "PasswordValida1"));
                assertEquals("Password non valida", e1.getMessage());
                assertEquals("Password non valida", e2.getMessage());
                assertEquals("Password non valida", e3.getMessage());
                assertEquals("Password non valida", e4.getMessage());
        }

}
