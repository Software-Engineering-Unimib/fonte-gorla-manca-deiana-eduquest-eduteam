package dev.eduteam.eduquest.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Repository;

import dev.eduteam.eduquest.models.Account;

@Repository
public class AccountRepository {

    public int insertAccount(Account account, String tipo) throws Exception {
        String query = "INSERT INTO account (nome, cognome, userName, email, password, tipo) VALUES (?, ?, ?, ?, ?, ?)";

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
        String query = "UPDATE account SET nome = ?, cognome = ?, email = ?, password = ? WHERE accountID = ?";
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
        String query = "DELETE FROM account WHERE accountID = ?";
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
