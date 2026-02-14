package dev.eduteam.eduquest.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.eduteam.eduquest.models.accounts.Account;
import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.repositories.accounts.AccountRepository;
import dev.eduteam.eduquest.services.accounts.AccountFactory;
import dev.eduteam.eduquest.services.accounts.AccountService;
import dev.eduteam.eduquest.services.accounts.DocenteService;
import dev.eduteam.eduquest.services.accounts.StudenteService;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

        @Mock
        private AccountRepository accountRepository;

        @Mock
        private StudenteService studenteService;

        @Mock
        private DocenteService docenteService;

        @InjectMocks
        private AccountService accountService;

        private Account account;

        @BeforeEach
        void setUp() {
                account = AccountFactory.creaAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                                "PasswordValida1!", false);
                account.setAccountID(1);
        }

        // test di registrazione account

        @Test
        void creazioneAccountStudenteValidoTest() {
                Studente nuovoStudente = new Studente("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                                "PasswordValida1!");
                nuovoStudente.setAccountID(1);
                when(studenteService.registraStudente(any(Studente.class))).thenReturn(nuovoStudente);

                Account result = accountService.registraAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                                "PasswordValida1!",
                                false);
                assertNotNull(result);
                assertFalse(result.isDocente());
                verify(studenteService, times(1)).registraStudente(any(Studente.class));
        }

        @Test
        void creazioneAccountDocenteValidoTest() {
                Docente nuovoDocente = new Docente("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                                "PasswordValida1!");
                nuovoDocente.setAccountID(1);
                when(docenteService.registraDocente(any(Docente.class))).thenReturn(nuovoDocente);

                Account result = accountService.registraAccount("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                                "PasswordValida1!",
                                true);
                assertNotNull(result);
                assertTrue(result.isDocente());
                verify(docenteService, times(1)).registraDocente(any(Docente.class));
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
                Studente studente1 = new Studente("Mario", "Rossi", "mrossi123", "mrossi234@email.com", "PwVali1!");
                studente1.setAccountID(1);
                when(studenteService.registraStudente(any(Studente.class))).thenReturn(studente1);
                Account accountMinPw = accountService.registraAccount("Mario", "Rossi", "mrossi123",
                                "mrossi234@email.com",
                                "PwVali1!",
                                false);
                // pw caso limite 20 caratteri
                Studente studente2 = new Studente("Mario", "Rossi", "mrossi123", "mrossi234@email.com",
                                "PasswordValidaaaaa1!");
                studente2.setAccountID(2);
                when(studenteService.registraStudente(any(Studente.class))).thenReturn(studente2);
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

        // test di aggiornamento account
        @Test
        void aggiornaAccountSuccessoTest() {
                Studente accountEsistente = new Studente("pinco", "pallo", "PincoPallino1",
                                "PincoPallo@prova.edu", "PasswordValida1!");
                accountEsistente.setAccountID(1);
                when(accountRepository.getAccountByUserName("PincoPallino1")).thenReturn(accountEsistente);
                when(accountRepository.existsByEmail("nuovaemail@prova.edu")).thenReturn(false);
                when(studenteService.aggiornaStudente(any(Studente.class))).thenReturn(true);

                Account result = accountService.aggiornaAccount("PincoPallino1", "PasswordValida1!",
                                "pinco_aggiornato", "pallo_aggiornato", "nuovaemail@prova.edu", "NuovaPass1!");
                assertNotNull(result);
                assertEquals("pinco_aggiornato", result.getNome());
                assertEquals("pallo_aggiornato", result.getCognome());
                assertEquals("nuovaemail@prova.edu", result.getEmail());
                assertEquals("NuovaPass1!", result.getPassword());
                verify(accountRepository, times(1)).getAccountByUserName("PincoPallino1");
                verify(accountRepository, times(1)).existsByEmail("nuovaemail@prova.edu");
                verify(studenteService, times(1)).aggiornaStudente(any(Studente.class));
        }

        @Test
        void aggiornaSoloNomeTest() {
                Studente accountEsistente = new Studente("pinco", "pallo", "PincoPallino1",
                                "PincoPallo@prova.edu", "PasswordValida1!");
                accountEsistente.setAccountID(1);
                when(accountRepository.getAccountByUserName("PincoPallino1")).thenReturn(accountEsistente);
                when(studenteService.aggiornaStudente(any(Studente.class))).thenReturn(true);

                Account result = accountService.aggiornaAccount("PincoPallino1", "PasswordValida1!",
                                "NuovoNome", null, null, null);
                assertEquals("NuovoNome", result.getNome());
                assertEquals("pallo", result.getCognome());
                assertEquals("PincoPallo@prova.edu", result.getEmail());
                assertEquals("PasswordValida1!", result.getPassword());
                verify(accountRepository, times(1)).getAccountByUserName("PincoPallino1");
                verify(studenteService, times(1)).aggiornaStudente(any(Studente.class));
        }

        @Test
        void aggiornaSoloCognomeTest() {
                Studente accountEsistente = new Studente("pinco", "pallo", "PincoPallino1",
                                "PincoPallo@prova.edu", "PasswordValida1!");
                accountEsistente.setAccountID(1);
                when(accountRepository.getAccountByUserName("PincoPallino1")).thenReturn(accountEsistente);
                when(studenteService.aggiornaStudente(any(Studente.class))).thenReturn(true);

                Account result = accountService.aggiornaAccount("PincoPallino1", "PasswordValida1!",
                                null, "NuovoCognome", null, null);
                assertEquals("pinco", result.getNome());
                assertEquals("NuovoCognome", result.getCognome());
                assertEquals("PincoPallo@prova.edu", result.getEmail());
                assertEquals("PasswordValida1!", result.getPassword());
                verify(accountRepository, times(1)).getAccountByUserName("PincoPallino1");
                verify(studenteService, times(1)).aggiornaStudente(any(Studente.class));
        }

        @Test
        void aggiornaSoloEmailTest() {
                Studente accountEsistente = new Studente("pinco", "pallo", "PincoPallino1",
                                "PincoPallo@prova.edu", "PasswordValida1!");
                accountEsistente.setAccountID(1);
                when(accountRepository.getAccountByUserName("PincoPallino1")).thenReturn(accountEsistente);
                when(accountRepository.existsByEmail("nuovamail@dominio.com")).thenReturn(false);
                when(studenteService.aggiornaStudente(any(Studente.class))).thenReturn(true);

                Account result = accountService.aggiornaAccount("PincoPallino1", "PasswordValida1!",
                                null, null, "nuovamail@dominio.com", null);
                assertEquals("pinco", result.getNome());
                assertEquals("pallo", result.getCognome());
                assertEquals("nuovamail@dominio.com", result.getEmail());
                assertEquals("PasswordValida1!", result.getPassword());
                verify(accountRepository, times(1)).getAccountByUserName("PincoPallino1");
                verify(accountRepository, times(1)).existsByEmail("nuovamail@dominio.com");
                verify(studenteService, times(1)).aggiornaStudente(any(Studente.class));
        }

        @Test
        void aggiornaSoloPasswordTest() {
                Studente accountEsistente = new Studente("pinco", "pallo", "PincoPallino1",
                                "PincoPallo@prova.edu", "PasswordValida1!");
                accountEsistente.setAccountID(1);
                when(accountRepository.getAccountByUserName("PincoPallino1")).thenReturn(accountEsistente);
                when(studenteService.aggiornaStudente(any(Studente.class))).thenReturn(true);

                Account result = accountService.aggiornaAccount("PincoPallino1", "PasswordValida1!",
                                null, null, null, "NuovaPassword2!");
                assertEquals("pinco", result.getNome());
                assertEquals("pallo", result.getCognome());
                assertEquals("PincoPallo@prova.edu", result.getEmail());
                assertEquals("NuovaPassword2!", result.getPassword());
                verify(accountRepository, times(1)).getAccountByUserName("PincoPallino1");
                verify(studenteService, times(1)).aggiornaStudente(any(Studente.class));
        }

        @Test
        void aggiornaAccountPasswordAttualErratTest() {
                when(accountRepository.getAccountByUserName("PincoPallino1")).thenReturn(null);

                IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                                () -> accountService.aggiornaAccount("PincoPallino1", "PasswordSbagliata",
                                                "NuovoNome", null, null, null));
                assertEquals("Credenziali non valide: username o password errati.", e.getMessage());
                verify(accountRepository, times(1)).getAccountByUserName("PincoPallino1");
        }

        @Test
        void aggiornaAccountNonTrovatoTest() {
                when(accountRepository.getAccountByUserName("UsernameNonEsistente")).thenReturn(null);

                IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                                () -> accountService.aggiornaAccount("UsernameNonEsistente", "PasswordValida1!",
                                                "NuovoNome", null, null, null));
                assertEquals("Credenziali non valide: username o password errati.", e.getMessage());
                verify(accountRepository, times(1)).getAccountByUserName("UsernameNonEsistente");
        }

        @Test
        void aggiornaEmailNonValidaTest() {
                Account accountEsistente = AccountFactory.creaAccount("pinco", "pallo", "PincoPallino1",
                                "PincoPallo@prova.edu", "PasswordValida1!", false);
                accountEsistente.setAccountID(1);
                when(accountRepository.getAccountByUserName("PincoPallino1")).thenReturn(accountEsistente);

                IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.aggiornaAccount("PincoPallino1", "PasswordValida1!",
                                                null, null, "emailNonValida.com", null));
                IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class,
                                () -> accountService.aggiornaAccount("PincoPallino1", "PasswordValida1!",
                                                null, null, "@email.com", null));
                assertEquals("Il formato della nuova email non è valido.", e1.getMessage());
                assertEquals("Il formato della nuova email non è valido.", e2.getMessage());
        }

        @Test
        void aggiornaPasswordNonValidaTest() {
                Account accountEsistente = AccountFactory.creaAccount("pinco", "pallo", "PincoPallino1",
                                "PincoPallo@prova.edu", "PasswordValida1!", false);
                accountEsistente.setAccountID(1);
                when(accountRepository.getAccountByUserName("PincoPallino1")).thenReturn(accountEsistente);

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
                assertEquals("La nuova password non rispetta i requisiti di sicurezza.", e1.getMessage());
                assertEquals("La nuova password non rispetta i requisiti di sicurezza.", e2.getMessage());
                assertEquals("La nuova password non rispetta i requisiti di sicurezza.", e3.getMessage());
                assertEquals("La nuova password non rispetta i requisiti di sicurezza.", e4.getMessage());
        }

        // test di registraAccount con username o email già esistenti

        @Test
        void registraAccountUserNameGiaEsistenteTest() {
                Account accountEsistente = AccountFactory.creaAccount("pinco", "pallo", "PincoPallino1",
                                "PincoPallo@prova.edu", "PasswordValida1!", false);
                when(accountRepository.getAccountByUserName("PincoPallino1")).thenReturn(accountEsistente);

                IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", "Rossi", "PincoPallino1",
                                                "mario123@email.com", "PasswordValida1!", false));
                assertEquals("Lo username 'PincoPallino1' è già occupato.", e.getMessage());
                verify(accountRepository, times(1)).getAccountByUserName("PincoPallino1");
        }

        @Test
        void registraAccountEmailGiaEsistenteTest() {
                when(accountRepository.getAccountByUserName("mariorossi123")).thenReturn(null);
                when(accountRepository.existsByEmail("PincoPallo@prova.edu")).thenReturn(true);

                IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", "Rossi", "mariorossi123",
                                                "PincoPallo@prova.edu", "PasswordValida1!", false));
                assertEquals("L'email 'PincoPallo@prova.edu' è già associata a un account.", e.getMessage());
                verify(accountRepository, times(1)).getAccountByUserName("mariorossi123");
                verify(accountRepository, times(1)).existsByEmail("PincoPallo@prova.edu");
        }

        @Test
        void registraAccountUserNameCaseInsensitiveTest() {
                Account accountEsistente = AccountFactory.creaAccount("pinco", "pallo", "PincoPallino1",
                                "PincoPallo@prova.edu", "PasswordValida1!", false);
                when(accountRepository.getAccountByUserName("pincopallino1")).thenReturn(accountEsistente);

                IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", "Rossi", "pincopallino1",
                                                "mario123@email.com", "PasswordValida1!", false));
                assertEquals("Lo username 'pincopallino1' è già occupato.", e.getMessage());
        }

        @Test
        void registraAccountEmailCaseInsensitiveTest() {
                when(accountRepository.getAccountByUserName("mariorossi123")).thenReturn(null);
                when(accountRepository.existsByEmail("pincopallo@prova.edu")).thenReturn(true);

                IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                                () -> accountService.registraAccount("Mario", "Rossi", "mariorossi123",
                                                "pincopallo@prova.edu", "PasswordValida1!", false));
                assertEquals("L'email 'pincopallo@prova.edu' è già associata a un account.", e.getMessage());
        }

        // test di cancellazione account

        @Test
        void eliminaAccountValidoTest() {
                Studente nuovoStudente = new Studente("Giovanni", "Bianchi", "gbianchi123",
                                "gbianchi@email.com", "PasswordValida1!");
                nuovoStudente.setAccountID(1);
                when(studenteService.registraStudente(any(Studente.class))).thenReturn(nuovoStudente);
                when(accountRepository.getAccountByUserName("gbianchi123")).thenReturn(null).thenReturn(nuovoStudente);
                when(accountRepository.existsByEmail("gbianchi@email.com")).thenReturn(false);
                when(accountRepository.removeAccount(1)).thenReturn(true);

                Account account = accountService.registraAccount("Giovanni", "Bianchi", "gbianchi123",
                                "gbianchi@email.com", "PasswordValida1!", false);
                assertNotNull(account);

                accountService.eliminaAccount("gbianchi123", "PasswordValida1!");

                verify(accountRepository, times(1)).removeAccount(1);
        }

        @Test
        void eliminaAccountPasswordErratTest() {
                Studente nuovoStudente = new Studente("Fabio", "Gialli", "fgialli123",
                                "fgialli@email.com", "PasswordValida1!");
                nuovoStudente.setAccountID(2);
                when(studenteService.registraStudente(any(Studente.class))).thenReturn(nuovoStudente);
                when(accountRepository.getAccountByUserName("fgialli123")).thenReturn(null).thenReturn(nuovoStudente);
                when(accountRepository.existsByEmail("fgialli@email.com")).thenReturn(false);

                Account account = accountService.registraAccount("Fabio", "Gialli", "fgialli123",
                                "fgialli@email.com", "PasswordValida1!", false);
                assertNotNull(account);

                IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                                () -> accountService.eliminaAccount("fgialli123", "PasswordSbagliata"));
                assertEquals("Credenziali non valide: username o password errati.", e.getMessage());

                // Verifichiamo che removeAccount non è stato chiamato
                verify(accountRepository, times(0)).removeAccount(anyInt());
        }

        @Test
        void eliminaAccountNonEsistenteTest() {
                when(accountRepository.getAccountByUserName("usernameNonEsistente")).thenReturn(null);

                IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                                () -> accountService.eliminaAccount("usernameNonEsistente", "PasswordValida1!"));
                assertEquals("Credenziali non valide: username o password errati.", e.getMessage());
                verify(accountRepository, times(1)).getAccountByUserName("usernameNonEsistente");
        }

}
