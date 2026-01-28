package dev.eduteam.eduquest.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.springframework.stereotype.Repository;

import dev.eduteam.eduquest.models.Risposta;

@Repository
public class RispostaRepository {

    public Risposta getRispostaByID(int domandaID, int rispostaID) {
        Risposta risposta = null;
        String query = "SELECT " +
                "rispostaID, " +
                "testo, " +
                "isCorretta, " +
                "domandaID_FK FROM risposte WHERE rispostaID = ? AND domandaID_FK = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, rispostaID);
            ps.setInt(2, domandaID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String testoDB = rs.getString("testo");
                    risposta = new Risposta(testoDB);
                    risposta.setID(rs.getInt("rispostaID"));
                    // Questo booleano verrà usato dal Service per popolare i campi
                    // 'rispostaCorretta' o 'risposteCorrette' della Domanda
                    risposta.setCorretta(rs.getBoolean("isCorretta"));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return risposta;
    }

    public ArrayList<Risposta> getRisposteByDomanda(int domandaID) {
        ArrayList<Risposta> risposte = new ArrayList<Risposta>();
        String query = "SELECT " +
                "rispostaID, " +
                "testo, " +
                "isCorretta, " +
                "domandaID_FK FROM risposte WHERE domandaID_FK = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, domandaID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String testoDB = rs.getString("testo");
                    Risposta risposta = new Risposta(testoDB);
                    risposta.setID(rs.getInt("rispostaID"));
                    risposta.setCorretta(rs.getBoolean("isCorretta"));

                    risposte.add(risposta);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return risposte;
    }

    public Risposta insertRisposta(Risposta r, int domandaID) {
        String query = "INSERT INTO risposte (testo, isCorretta, domandaID_FK) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, r.getTesto());
            ps.setBoolean(2, r.isCorretta());
            ps.setInt(3, domandaID);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    r.setID(rs.getInt(1)); // Imposta l'ID generato alla risposta
                }
            }
            return r;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean removeRisposta(int rispostaID, int domandaID) {
        boolean result = false;
        String query = "DELETE FROM risposte WHERE rispostaID = ? AND domandaID_FK = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, rispostaID);
            ps.setInt(2, domandaID);

            int rowsAffected = ps.executeUpdate();
            result = rowsAffected > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public boolean updateRisposta(Risposta r) {
        boolean result = false;
        String query = "UPDATE risposte SET testo = ?, isCorretta = ? WHERE rispostaID = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);) {

            ps.setString(1, r.getTesto());
            ps.setBoolean(2, r.isCorretta());
            ps.setInt(3, r.getID());

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
