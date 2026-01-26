package dev.eduteam.eduquest.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Repository;

import dev.eduteam.eduquest.models.Account;
import dev.eduteam.eduquest.models.AccountFactory;

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

    public void updateAccount(Account account) throws Exception {
        String query = "UPDATE accounts SET nome = ?, cognome = ?, email = ?, password = ? WHERE accountID = ?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, account.getNome());
            ps.setString(2, account.getCognome());
            ps.setString(3, account.getEmail());
            ps.setString(4, account.getPassword());
            ps.setInt(5, account.getAccountID());
            ps.executeUpdate();
        }
    }

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
