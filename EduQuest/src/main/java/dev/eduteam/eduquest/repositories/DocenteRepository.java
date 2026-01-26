package dev.eduteam.eduquest.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.eduteam.eduquest.models.Docente;

@Repository
public class DocenteRepository {

    @Autowired
    private AccountRepository accountRepository;

    public Docente getDocenteByAccountID(int accountID) {
        String query = "SELECT a.*, d.insegnamento FROM account a " +
                "INNER JOIN docenti d ON a.accountID = d.accountID_FK WHERE a.accountID = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, accountID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Docente d = new Docente(rs.getString("nome"), rs.getString("cognome"),
                        rs.getString("userName"), rs.getString("email"), rs.getString("password"));
                d.setAccountID(rs.getInt("accountID"));
                d.setInsegnamento(rs.getString("insegnamento"));
                return d;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public Docente insertDocente(Docente docente) {
        try {
            // Inserimento nella tabella 'account' tramite la repository comune
            int generatedID = accountRepository.insertAccount(docente, "Docente");
            docente.setAccountID(generatedID);
            // Inserimento nella tabella 'docenti'
            try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
                String query = "INSERT INTO docenti (accountID_FK, insegnamento) VALUES (?, ?)";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, docente.getAccountID());
                ps.setString(2, docente.getInsegnamento());
                ps.executeUpdate();
            }
            return docente;
        } catch (Exception e) {
            System.err.println("Errore insertDocente: " + e.getMessage());
            return null;
        }
    }

    public ArrayList<Docente> getAllDocenti() {
        ArrayList<Docente> docenti = new ArrayList<>();
        String query = "SELECT a.*, d.insegnamento FROM account a " +
                "INNER JOIN docenti d ON a.accountID = d.accountID_FK";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Docente d = new Docente(rs.getString("nome"), rs.getString("cognome"),
                        rs.getString("userName"), rs.getString("email"), rs.getString("password"));
                d.setAccountID(rs.getInt("accountID"));
                d.setInsegnamento(rs.getString("insegnamento"));
                docenti.add(d);
            }
        } catch (Exception e) {
            System.err.println("Errore getAllDocenti: " + e.getMessage());
        }
        return docenti;
    }

    public boolean updateDocente(Docente docente) {
        try {
            // Aggiorna i dati comuni
            accountRepository.updateAccount(docente);

            // Aggiorna i dati specifici
            String query = "UPDATE docenti SET insegnamento = ? WHERE accountID_FK = ?";
            try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                    PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, docente.getInsegnamento());
                ps.setInt(2, docente.getAccountID());
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("Errore updateDocente: " + e.getMessage());
            return false;
        }
    }
}
