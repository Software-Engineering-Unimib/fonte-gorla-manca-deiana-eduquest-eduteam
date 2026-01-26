package dev.eduteam.eduquest.services;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dev.eduteam.eduquest.models.Account;
import dev.eduteam.eduquest.models.AccountFactory;
import dev.eduteam.eduquest.models.Docente;
import dev.eduteam.eduquest.models.Studente;
import dev.eduteam.eduquest.repositories.AccountRepository;
import dev.eduteam.eduquest.repositories.StudenteRepository;
import dev.eduteam.eduquest.repositories.DocenteRepository;

@Service
public class AccountService {

    private static final Pattern EMAILVALIDA = Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private StudenteRepository studenteRepository;

    @Autowired
    private DocenteRepository docenteRepository;

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accountRepository.getAllAccounts());
    }

    public Account getAccountByUserName(String userName) {
        return accountRepository.getAccountByUserName(userName);
    }

    //TODO da sistemare il ritorno di account con id da repository
    public Account registraAccount(String nome, String cognome, String userName, String email, String password,
            boolean docente) {
        validaDati(nome, cognome, userName, email, password);
        verificaUnicitaUserNameEmail(userName, email);
        Account nuovoAccount = AccountFactory.creaAccount(nome, cognome, userName, email, password, docente);
        
        if (docente) {
            docenteRepository.insertDocente((Docente) nuovoAccount);
        } else {
            studenteRepository.insertStudente((Studente) nuovoAccount);
        }
        
        return nuovoAccount;
    }

    public void eliminaAccount(String userName, String password) {
        Account account = accountRepository.getAccountByUserName(userName);
        if (account == null) {
            throw new IllegalArgumentException("Utente non trovato");
        }

        if (!account.getPassword().equals(password)) {
            throw new IllegalArgumentException("Password errata");
        }

        if (account.isDocente()) {
            docenteRepository.removeDocente(getAccountIDByUserName(userName));
        } else {
            studenteRepository.removeStudente(getAccountIDByUserName(userName));
        }
    }

    public Account logIn(String userName, String password) {
        Account accountTrovato = accountRepository.getAccountByUserName(userName);
        if (accountTrovato == null) {
            throw new IllegalArgumentException("Credenziali non valide");
        }
        // per ora è cosi, però in futuro per sicurezza dell'account dovrò usare
        // passwordEncoder.matches(password, accountTrovato.getPassword())
        if (!accountTrovato.getPassword().equals(password)) {
            throw new IllegalArgumentException("Password errata");
        }
        return accountTrovato;
    }

    //--------TODO: da capire a che livello splittare le chiamate di aggiornamento dei singoli campi---------
    // CONVIENE RIFARLA COMPLETAMENTE
    // per ora solo ID viene aggiornato nel DB, gli altri campi vengono aggiornati nell'oggetto in memoria
    // TODO non verifica se la nuova email è già in uso o se i valori sono uguali a quelli attuali
    public Account aggiornaAccount(String userName, String passwordAttuale, String nuovoNome, String nuovoCognome,
            String nuovaEmail, String nuovaPassword) {
        Account accountTrovato = accountRepository.getAccountByUserName(userName);
        if (accountTrovato == null) {
            throw new IllegalArgumentException("Account non trovato");
        }
        if (!accountTrovato.getPassword().equals(passwordAttuale)) {
            throw new IllegalArgumentException("Password attuale errata");
        }

        if (nuovoNome != null && !nuovoNome.isBlank()) {
            aggiornaNome(accountTrovato, nuovoNome);
        }
        if (nuovoCognome != null && !nuovoCognome.isBlank()) {
            aggiornaCognome(accountTrovato, nuovoCognome);
        }
        if (nuovaEmail != null && !nuovaEmail.isBlank()) {
            aggiornaEmail(accountTrovato, nuovaEmail);
        }
        if (nuovaPassword != null && !nuovaPassword.isBlank()) {
            aggiornaPassword(accountTrovato, nuovaPassword);
        }

        int accountID = getAccountIDByUserName(userName);
        if (accountTrovato.isDocente()) {
            docenteRepository.updateDocente((Docente) accountTrovato, accountID);
        } else {
            studenteRepository.updateStudente((Studente) accountTrovato, accountID);
        }

        return accountTrovato;
    }

    private void aggiornaNome(Account account, String nuovoNome) {
        if (!account.getNome().equals(nuovoNome)) {
            account.setNome(nuovoNome);
        } else {
            throw new IllegalArgumentException("Il nuovo nome è uguale a quello attuale");
        }
    }

    private void aggiornaCognome(Account account, String nuovoCognome) {
        if (!account.getCognome().equals(nuovoCognome)) {
            account.setCognome(nuovoCognome);
        } else {
            throw new IllegalArgumentException("Il nuovo cognome è uguale a quello attuale");
        }
    }

    private void aggiornaEmail(Account account, String nuovaEmail) {
        if (!isEmailValida(nuovaEmail)) {
            throw new IllegalArgumentException("Email non valida");
        }
        if (!account.getEmail().equals(nuovaEmail)) {
            account.setEmail(nuovaEmail);
        } else {
            throw new IllegalArgumentException("La nuova email è uguale a quella attuale");
        }
    }

    private void aggiornaPassword(Account account, String nuovaPassword) {
        if (!isPasswordValida(nuovaPassword)) {
            throw new IllegalArgumentException("Password non valida");
        }
        if (!account.getPassword().equals(nuovaPassword)) {
            account.setPassword(nuovaPassword);
        } else {
            throw new IllegalArgumentException("La nuova password è uguale a quella attuale");
        }
    }
    //TODO sistemare chiamate a repository
    // metodo che controlla se i parametri passati in input durante la creazione
    // sono nuovi, in caso contrario lancia una eccezione
    private void verificaUnicitaUserNameEmail(String userName, String email) {
        Account accountPerUserName = accountRepository.getAccountByUserName(userName);
        //da vedere se si può far passare un booleano
        if (accountPerUserName != null) {
            throw new IllegalArgumentException("Username già in uso");
        }
        // anche sta roba va ottimizzata
        List<Account> allAccounts = accountRepository.getAllAccounts();
        boolean emailEsiste = allAccounts.stream()
                .anyMatch(a -> a.getEmail().equalsIgnoreCase(email));
        if (emailEsiste) {
            throw new IllegalArgumentException("Email già in uso");
        }
    }

    // metodo che controlla se i parametri passati in input durante la creazione
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

    // metodo che controlla se i caratteri di un userName sono alfanumerici (ho
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

    // metodo che grazie all'utilizzo di java.util.regex.Pattern controlla se una
    // email è valida
    // ossia controlla che una email sia costituita da (vedere espressione
    // EMAILVALIDA a riga 11):
    // una prima parte di caratteri, seguiti da "@", seguiti dal dominio e infine
    // un'estensione
    private boolean isEmailValida(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAILVALIDA.matcher(email).matches();
    }

    /*
     * metodo che controlla se una password è valida:
     * Lunghezza minima e massima
     * controlla se ci sono spazi
     * controlla se c'è almeno un carattere speciale
     * controlla se c'è almeno una maisucola
     * controlla se c'è almeno un numero
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

    //TODO penso da rimuovere
    // metodo helper per ottenere l'accountID dal userName
    private int getAccountIDByUserName(String userName) {
        Account account = accountRepository.getAccountByUserName(userName);
        if (account == null) {
            throw new IllegalArgumentException("Account non trovato");
        }
        return account.getAccountID();
    }
}
