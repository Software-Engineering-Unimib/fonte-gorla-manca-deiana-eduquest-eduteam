package dev.eduteam.eduquest.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.springframework.stereotype.Repository;

import dev.eduteam.eduquest.models.Account;
import dev.eduteam.eduquest.models.AccountFactory;

@Repository
public class AccountRepository {

    public Account getAccountByID(int accountID) {
        Account account = null;

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            String query = "SELECT " +
                    "accountID, " +
                    "nome, " +
                    "cognome, " +
                    "userName, " +
                    "email, " +
                    "password, " +
                    "tipo FROM account WHERE accountID = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, accountID);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean isDocente = rs.getString("tipo").equalsIgnoreCase("Docente");
                account = AccountFactory.creaAccount(
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("userName"),
                        rs.getString("email"),
                        rs.getString("password"),
                        isDocente
                );
                //da estendere AccountFactory per gestire anche l'ID
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return account;
    }

    public Account getAccountByUserName(String userName) {
        Account account = null;

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            String query = "SELECT " +
                    "accountID, " +
                    "nome, " +
                    "cognome, " +
                    "userName, " +
                    "email, " +
                    "password, " +
                    "tipo FROM account WHERE userName = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userName);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean isDocente = rs.getString("tipo").equalsIgnoreCase("Docente");
                account = AccountFactory.creaAccount(
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("userName"),
                        rs.getString("email"),
                        rs.getString("password"),
                        isDocente
                );
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return account;
    }

    public ArrayList<Account> getAllAccounts() {
        ArrayList<Account> accounts = new ArrayList<Account>();

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            String query = "SELECT " +
                    "accountID, " +
                    "nome, " +
                    "cognome, " +
                    "userName, " +
                    "email, " +
                    "password, " +
                    "tipo FROM account";

            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                boolean isDocente = rs.getString("tipo").equalsIgnoreCase("Docente");
                Account account = AccountFactory.creaAccount(
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("userName"),
                        rs.getString("email"),
                        rs.getString("password"),
                        isDocente
                );
                accounts.add(account);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return accounts;
    }

    public Account insertAccount(Account account, boolean isDocente) {
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {

            String query = "INSERT INTO account (nome, cognome, userName, email, password, tipo) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, account.getNome());
            ps.setString(2, account.getCognome());
            ps.setString(3, account.getUserName());
            ps.setString(4, account.getEmail());
            ps.setString(5, account.getPassword());
            ps.setString(6, isDocente ? "Docente" : "Studente");

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    // L'ID viene generato dal DB,
                    // per ora restituiamo l'account senza
                }
            }
            return account;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean removeAccount(int accountID) {
        boolean result = false;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {

            String query = "DELETE FROM account WHERE accountID = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, accountID);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                result = true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public boolean updateAccount(Account account) {
        boolean result = false;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {

            String query = "UPDATE account SET nome = ?, cognome = ?, email = ?, password = ? WHERE userName = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, account.getNome());
            ps.setString(2, account.getCognome());
            ps.setString(3, account.getEmail());
            ps.setString(4, account.getPassword());
            ps.setString(5, account.getUserName());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                result = true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
}
