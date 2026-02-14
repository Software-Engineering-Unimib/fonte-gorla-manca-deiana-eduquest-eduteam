package dev.eduteam.eduquest.repositories.accounts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Repository;

import dev.eduteam.eduquest.models.accounts.Account;
import dev.eduteam.eduquest.repositories.ConnectionSingleton;
import dev.eduteam.eduquest.services.accounts.AccountFactory;

@Repository
public class AccountRepository {

    // Recupera l'account tramite userName
    public Account getAccountByUserName(String userName) {
        String query = "SELECT * FROM accounts WHERE userName = ?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean isDocente = rs.getString("tipo").equals("Docente");
                    Account acc = AccountFactory.creaAccount(
                            rs.getString("nome"), rs.getString("cognome"),
                            rs.getString("userName"), rs.getString("email"),
                            rs.getString("password"), isDocente);
                    acc.setAccountID(rs.getInt("accountID"));
                    return acc;
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    // Nuovo metodo per verificare se esiste l'email
    public boolean existsByEmail(String email) {
        String query = "SELECT 1 FROM accounts WHERE email = ? LIMIT 1";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            return false;
        }
    }

    // Metodo per aggiungere un nuovo account creato all'interno del database
    public int insertAccount(Account account, String tipo) throws Exception {
        String query = "INSERT INTO accounts (nome, cognome, userName, email, password, tipo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, account.getNome());
            ps.setString(2, account.getCognome());
            ps.setString(3, account.getUserName());
            ps.setString(4, account.getEmail());
            ps.setString(5, account.getPassword());
            ps.setString(6, tipo);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        }
        throw new Exception("Errore durante l'inserimento dell'account base.");
    }

    // Metodo per aggiornare un account già esistente nel database
    public boolean updateAccount(Account account) {
        boolean result = false;
        String query = "UPDATE accounts SET nome = ?, cognome = ?, email = ?, password = ? WHERE accountID = ?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, account.getNome());
            ps.setString(2, account.getCognome());
            ps.setString(3, account.getEmail());
            ps.setString(4, account.getPassword());
            ps.setInt(5, account.getAccountID());
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                result = true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    // Metodo per rimuovere un account già esistente dal database
    public boolean removeAccount(int accountID) {
        String query = "DELETE FROM accounts WHERE accountID = ?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, accountID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

}
