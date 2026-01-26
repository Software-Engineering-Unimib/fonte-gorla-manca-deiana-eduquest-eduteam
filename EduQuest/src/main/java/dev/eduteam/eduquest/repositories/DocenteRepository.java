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
        Docente docente = null;

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            String query = "SELECT " +
                    "a.accountID, " +
                    "a.nome, " +
                    "a.cognome, " +
                    "a.userName, " +
                    "a.email, " +
                    "a.password, " +
                    "d.insegnamento FROM account a " +
                    "INNER JOIN docenti d ON a.accountID = d.accountID_FK " +
                    "WHERE a.accountID = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, accountID);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                docente = new Docente(
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("userName"),
                        rs.getString("email"),
                        rs.getString("password")
                );
                String insegnamento = rs.getString("insegnamento");
                if (insegnamento != null) {
                    docente.setInsegnamento(insegnamento);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return docente;
    }

    public ArrayList<Docente> getAllDocenti() {
        ArrayList<Docente> docenti = new ArrayList<Docente>();

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            String query = "SELECT " +
                    "a.accountID, " +
                    "a.nome, " +
                    "a.cognome, " +
                    "a.userName, " +
                    "a.email, " +
                    "a.password, " +
                    "d.insegnamento FROM account a " +
                    "INNER JOIN docenti d ON a.accountID = d.accountID_FK";

            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Docente docente = new Docente(
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("userName"),
                        rs.getString("email"),
                        rs.getString("password")
                );
                String insegnamento = rs.getString("insegnamento");
                if (insegnamento != null) {
                    docente.setInsegnamento(insegnamento);
                }
                docenti.add(docente);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return docenti;
    }

    public Docente insertDocente(Docente docente) {
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {

            // Usa AccountRepository per inserire nella tabella account
            accountRepository.insertAccount(docente, true);

            // Poi inserisci nella tabella docenti
            String docenteQuery = "INSERT INTO docenti (accountID_FK, insegnamento) VALUES ((SELECT accountID FROM account WHERE userName = ?), ?)";
            PreparedStatement docentePs = conn.prepareStatement(docenteQuery);
            docentePs.setString(1, docente.getUserName());
            docentePs.setString(2, docente.getInsegnamento());
            docentePs.executeUpdate();

            return docente;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean removeDocente(int accountID) {
        // Delega a AccountRepository che gestisce il DELETE CASCADE
        return accountRepository.removeAccount(accountID);
    }

    public boolean updateDocente(Docente docente, int accountID) {
        boolean result = false;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {

            // Usa AccountRepository per aggiornare i dati comuni
            accountRepository.updateAccount(docente);

            // Aggiorna solo i dati specifici del docente
            String docenteQuery = "UPDATE docenti SET insegnamento = ? WHERE accountID_FK = ?";
            PreparedStatement docentePs = conn.prepareStatement(docenteQuery);
            docentePs.setString(1, docente.getInsegnamento());
            docentePs.setInt(2, accountID);
            int rowsAffected = docentePs.executeUpdate();

            if (rowsAffected > 0) {
                result = true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
}
