package dev.eduteam.eduquest.models;

import java.util.regex.Pattern;

public class UtenteNonLoggato {
    private static final String EMAILVALIDA = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";

    // simple factory che crea l'account a seconda del parametro boolean docente
    // passato in input
    public Account creaAccount(String nome, String cognome, String userName, String email, String password,
            boolean docente) {
        validaDati(nome, cognome, userName, email, password);
        return AccountFactory.creaAccount(nome, cognome, userName, email, password, docente);
    }

    // placeholder per il login
    public void logIn(String userName, String password) {
        // TBD
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
        return Pattern.matches(EMAILVALIDA, email);
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
