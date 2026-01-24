package dev.eduteam.eduquest.services;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import dev.eduteam.eduquest.models.Account;
import dev.eduteam.eduquest.models.AccountFactory;
import dev.eduteam.eduquest.models.Docente;
import dev.eduteam.eduquest.models.Studente;

@Service
public class AccountService {

    private static final Pattern EMAILVALIDA = Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    private List<Account> accountProvvis = new ArrayList<Account>() {
        {
            add(new Studente("pinco", "pallo", "PincoPallino1", "PincoPallo@prova.edu", "PasswordValida1!"));
            add(new Docente("Franco", "Rossi", "FrancoRossi2", "FrancoRossi@prova.edu", "PasswordValida2!"));
        }
    };

    public List<Account> getAllAccounts() {
        return accountProvvis;
    }

    public Account getAccountByNomeECognome(String nom, String cog) {
        return accountProvvis.stream()
                .filter(a -> a.getNome().equalsIgnoreCase(nom) && a.getCognome().equalsIgnoreCase(cog))
                .findFirst()
                .orElse(null);
    }

    public Account registraAccount(String nome, String cognome, String userName, String email, String password,
            boolean docente) {
        validaDati(nome, cognome, userName, email, password);
        verificaUnicitaUserNameEmail(userName, email);
        Account nuovoAccount = AccountFactory.creaAccount(nome, cognome, userName, email, password, docente);
        accountProvvis.add(nuovoAccount); // poi da sostituire con repositories
        return nuovoAccount;
    }

    public void eliminaAccount(String userName, String password) {
        accountProvvis.removeIf(a -> a.getUserName().equalsIgnoreCase(userName) && a.getPassword().equals(password));
    }

    public Account logIn(String userName, String password) {
        Account accountTrovato = accountProvvis.stream().filter(a -> a.getUserName().equals(userName)).findFirst()
                .orElse(null);
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

    // metodo per aggiornare i dati di un account
    //non verifica se i dati nuovi sono uguali a quelli vecchi(TODO)
    public Account aggiornaAccount(String userName, String passwordAttuale, String nuovoNome, String nuovoCognome,
            String nuovaEmail, String nuovaPassword) {
        Account accountTrovato = accountProvvis.stream().filter(a -> a.getUserName().equals(userName)).findFirst()
                .orElse(null);
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

    // metodo che controlla se i parametri passati in input durante la creazione
    // sono nuovi, in caso contrario lancia una eccezione
    private void verificaUnicitaUserNameEmail(String userName, String email) {
        boolean userNameEsiste = accountProvvis.stream()
                .anyMatch(a -> a.getUserName().equalsIgnoreCase(userName));
        if (userNameEsiste) {
            throw new IllegalArgumentException("Username già in uso");
        }
        boolean emailEsiste = accountProvvis.stream()
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
}
