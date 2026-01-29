package dev.eduteam.eduquest.repositories.accounts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.repositories.ConnectionSingleton;

@Repository
public class StudenteRepository {

    @Autowired
    private AccountRepository accountRepository;

    public Studente getStudenteByAccountID(int accountID) {
        String query = "SELECT a.*, s.mediaPunteggio FROM accounts a " +
                "INNER JOIN studenti s ON a.accountID = s.accountID_FK WHERE a.accountID = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, accountID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Studente s = new Studente(rs.getString("nome"), rs.getString("cognome"),
                        rs.getString("userName"), rs.getString("email"), rs.getString("password"));
                s.setAccountID(rs.getInt("accountID"));
                s.setMediaPunteggio(rs.getDouble("mediaPunteggio"));
                return s;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public Studente insertStudente(Studente studente) {
        try {
            // Inserimento nella tabella 'account' tramite la repository comune
            int generatedID = accountRepository.insertAccount(studente, "Studente");
            studente.setAccountID(generatedID);
            // Inserimento nella tabella 'studenti'
            try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
                String query = "INSERT INTO studenti (accountID_FK, mediaPunteggio) VALUES (?, ?)";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, studente.getAccountID());
                ps.setDouble(2, studente.getMediaPunteggio());
                ps.executeUpdate();
            }
            return studente;
        } catch (Exception e) {
            System.err.println("Errore insertStudente: " + e.getMessage());
            return null;
        }
    }

    public List<Studente> getAllStudenti() {
        List<Studente> studenti = new ArrayList<>();
        String query = "SELECT a.*, s.mediaPunteggio FROM accounts a " +
                "INNER JOIN studenti s ON a.accountID = s.accountID_FK";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Studente s = new Studente(rs.getString("nome"), rs.getString("cognome"),
                        rs.getString("userName"), rs.getString("email"), rs.getString("password"));
                s.setAccountID(rs.getInt("accountID"));
                s.setMediaPunteggio(rs.getDouble("mediaPunteggio"));
                studenti.add(s);
            }
        } catch (Exception e) {
            System.err.println("Errore getAllStudenti: " + e.getMessage());
        }
        return studenti;
    }

    public boolean updateStudente(Studente studente) {
        try {
            // Aggiorna i dati comuni nella tabella account
            accountRepository.updateAccount(studente);

            // Aggiorna i dati specifici nella tabella studenti
            String query = "UPDATE studenti SET mediaPunteggio = ? WHERE accountID_FK = ?";
            try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                    PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setDouble(1, studente.getMediaPunteggio());
                ps.setInt(2, studente.getAccountID());
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("Errore updateStudente: " + e.getMessage());
            return false;
        }
    }
}
