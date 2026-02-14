package dev.eduteam.eduquest.services.accounts;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.eduteam.eduquest.models.accounts.Account;
import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.repositories.accounts.AccountRepository;

@Service
public class AccountService {

    private static final Pattern EMAILVALIDA = Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private StudenteService studenteService;

    @Autowired
    private DocenteService docenteService;

    public Account getAccountByUserName(String userName) {
        return accountRepository.getAccountByUserName(userName);
    }

    public Account registraAccount(String nome, String cognome, String userName, String email, String password,
            boolean isDocente) {
        validaDati(nome, cognome, userName, email, password);
        verificaDisponibilitaCredenziali(userName, email);

        // Creazione dell'oggetto tramite Factory
        Account nuovoAccount = AccountFactory.creaAccount(nome, cognome, userName, email, password, isDocente);

        if (isDocente) {
            return docenteService.registraDocente((Docente) nuovoAccount);
        } else {
            return studenteService.registraStudente((Studente) nuovoAccount);
        }
    }

    public void eliminaAccount(String userName, String password) {
        Account acc = logIn(userName, password);
        accountRepository.removeAccount(acc.getAccountID());
        // Grazie al CASCADE nel DB, rimuovere l'account rimuove anche lo
        // Studente/Docente
    }

    public Account logIn(String userName, String password) {
        Account accountTrovato = accountRepository.getAccountByUserName(userName);

        if (accountTrovato == null || !accountTrovato.getPassword().equals(password)) {
            throw new IllegalArgumentException("Credenziali non valide: username o password errati.");
        }

        return accountTrovato;
    }

    public Docente logInDocente(String userName, String password) {
        Account acc = logIn(userName, password);
        if (acc instanceof Docente)
            return (Docente) acc;
        throw new IllegalArgumentException("L'account non è un Docente.");
    }

    public Studente logInStudente(String userName, String password) {
        Account acc = logIn(userName, password);
        if (acc instanceof Studente)
            return (Studente) acc;
        throw new IllegalArgumentException("L'account non è uno Studente.");
    }

    public Account aggiornaAccount(String userName, String passwordAttuale, String nuovoNome, String nuovoCognome,
            String nuovaEmail, String nuovaPassword) {
        // Verifichiamo l'identità dell'utente prima di permettere modifiche
        Account accountEsistente = logIn(userName, passwordAttuale);

        if (nuovoNome != null && !nuovoNome.isBlank()) {
            accountEsistente.setNome(nuovoNome);
        }
        if (nuovoCognome != null && !nuovoCognome.isBlank()) {
            accountEsistente.setCognome(nuovoCognome);
        }
        if (nuovaEmail != null && !nuovaEmail.isBlank()) {
            if (!isEmailValida(nuovaEmail)) {
                throw new IllegalArgumentException("Il formato della nuova email non è valido.");
            }
            if (!nuovaEmail.equalsIgnoreCase(accountEsistente.getEmail())
                    && accountRepository.existsByEmail(nuovaEmail)) {
                throw new IllegalArgumentException("La nuova email è già in uso da un altro account.");
            }
            accountEsistente.setEmail(nuovaEmail);
        }
        if (nuovaPassword != null && !nuovaPassword.isBlank()) {
            if (!isPasswordValida(nuovaPassword)) {
                throw new IllegalArgumentException("La nuova password non rispetta i requisiti di sicurezza.");
            }
            accountEsistente.setPassword(nuovaPassword);
        }

        // Aggiornamento
        if (accountEsistente instanceof Docente) {
            docenteService.aggiornaDocente((Docente) accountEsistente);
        } else {
            studenteService.aggiornaStudente((Studente) accountEsistente);
        }

        return accountEsistente;
    }

    private void verificaDisponibilitaCredenziali(String userName, String email) {
        if (accountRepository.getAccountByUserName(userName) != null) {
            throw new IllegalArgumentException("Lo username '" + userName + "' è già occupato.");
        }
        if (accountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("L'email '" + email + "' è già associata a un account.");
        }
    }

    // Metodo che controlla se i parametri passati in input durante la creazione
    // sono validi, in caso contrario lancia una eccezione
    private void validaDati(String nome, String cognome, String userName, String email, String password) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome obbligatorio");
        } else if (cognome == null || cognome.isBlank()) {
            throw new IllegalArgumentException("Cognome obbligatorio");
        } else if (!isUserNameValido(userName)) {
            throw new IllegalArgumentException("userName non valido");
        } else if (!isEmailValida(email)) {
            throw new IllegalArgumentException("email non valida");
        } else if (!isPasswordValida(password)) {
            throw new IllegalArgumentException("password non valida");
        }
    }

    // Metodo che controlla se i caratteri di un userName sono alfanumerici (ho
    // pensato cosi, potremmo cambiare)
    private boolean isUserNameValido(String userName) {
        if (userName == null || userName.isEmpty()) {
            return false;
        }
        for (char c : userName.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /*
     * Metodo che grazie all'utilizzo di java.util.regex.Pattern controlla se una
     * email è valida ossia controlla che una email sia costituita da (vedere
     * espressione EMAILVALIDA a riga 11): una prima parte di caratteri, seguiti
     * da "@", seguiti dal dominio e infine un'estensione
     */
    private boolean isEmailValida(String email) {
        return email != null && EMAILVALIDA.matcher(email).matches();
    }

    /*
     * Metodo che controlla se una password è valida:
     * - Lunghezza minima e massima
     * - Controlla se ci sono spazi
     * - Controlla se c'è almeno un carattere speciale
     * - Controlla se c'è almeno una maisucola
     * - Controlla se c'è almeno un numero
     */
    private boolean isPasswordValida(String password) {
        // controllo la lunghezza
        if (password == null || password.length() < 8 || password.length() > 20) {
            return false;
        }
        boolean haMaiuscola = false;
        boolean haSimbolo = false;
        boolean haNumero = false;

        for (char c : password.toCharArray()) {
            if (Character.isWhitespace(c)) {
                return false;
            } else if (Character.isUpperCase(c)) {
                haMaiuscola = true;
            } else if (Character.isDigit(c)) {
                haNumero = true;
            } else if (!Character.isLetterOrDigit(c)) {
                haSimbolo = true;
            }
        }
        return haMaiuscola && haSimbolo && haNumero;
    }

}
