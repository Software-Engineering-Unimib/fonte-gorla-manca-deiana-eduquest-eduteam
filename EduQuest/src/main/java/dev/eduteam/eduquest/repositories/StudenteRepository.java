package dev.eduteam.eduquest.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.eduteam.eduquest.models.Studente;

@Repository
public class StudenteRepository {

    @Autowired
    private AccountRepository accountRepository;

    public Studente getStudenteByAccountID(int accountID) {
        Studente studente = null;

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            String query = "SELECT " +
                    "a.accountID, " +
                    "a.nome, " +
                    "a.cognome, " +
                    "a.userName, " +
                    "a.email, " +
                    "a.password, " +
                    "s.mediaPunteggio FROM account a " +
                    "INNER JOIN studenti s ON a.accountID = s.accountID_FK " +
                    "WHERE a.accountID = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, accountID);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                studente = new Studente(
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("userName"),
                        rs.getString("email"),
                        rs.getString("password")
                );
                studente.setAccountID(rs.getInt("accountID"));
                studente.setMediaPunteggio(rs.getDouble("mediaPunteggio"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return studente;
    }

    public ArrayList<Studente> getAllStudenti() {
        ArrayList<Studente> studenti = new ArrayList<Studente>();

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            String query = "SELECT " +
                    "a.accountID, " +
                    "a.nome, " +
                    "a.cognome, " +
                    "a.userName, " +
                    "a.email, " +
                    "a.password, " +
                    "s.mediaPunteggio FROM account a " +
                    "INNER JOIN studenti s ON a.accountID = s.accountID_FK";

            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Studente studente = new Studente(
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("userName"),
                        rs.getString("email"),
                        rs.getString("password")
                );
                studente.setAccountID(rs.getInt("accountID"));
                studente.setMediaPunteggio(rs.getDouble("mediaPunteggio"));
                studenti.add(studente);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return studenti;
    }

    public Studente insertStudente(Studente studente) {
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {

            // Usa AccountRepository per inserire nella tabella account
            // L'ID viene generato dal DB e settato su studente
            Studente studenteInserito = (Studente) accountRepository.insertAccount(studente, false);
            
            if (studenteInserito == null) {
                return null;
            }

            // Poi inserisci nella tabella studenti usando l'ID generato
            String studenteQuery = "INSERT INTO studenti (accountID_FK, mediaPunteggio) VALUES (?, ?)";
            PreparedStatement studentePs = conn.prepareStatement(studenteQuery);
            studentePs.setInt(1, studenteInserito.getAccountID());
            studentePs.setDouble(2, studenteInserito.getMediaPunteggio());
            studentePs.executeUpdate();

            return studente;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean removeStudente(int accountID) {
        // Delega a AccountRepository che gestisce il DELETE CASCADE
        return accountRepository.removeAccount(accountID);
    }

    public boolean updateStudente(Studente studente, int accountID) {
        boolean result = false;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {

            // Usa AccountRepository per aggiornare i dati comuni
            accountRepository.updateAccount(studente);

            // Aggiorna solo i dati specifici dello studente
            String studenteQuery = "UPDATE studenti SET mediaPunteggio = ? WHERE accountID_FK = ?";
            PreparedStatement studentePs = conn.prepareStatement(studenteQuery);
            studentePs.setDouble(1, studente.getMediaPunteggio());
            studentePs.setInt(2, accountID);
            int rowsAffected = studentePs.executeUpdate();

            if (rowsAffected > 0) {
                result = true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
}
