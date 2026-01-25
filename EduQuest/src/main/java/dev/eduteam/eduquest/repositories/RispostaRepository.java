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

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            String query = "SELECT " +
                    "rispostaID, " +
                    "testo, " +
                    "domandaID_FK FROM risposte WHERE rispostaID = ? AND domandaID_FK = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, rispostaID);
            ps.setInt(2, domandaID);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                risposta = new Risposta("");
                risposta.setID(rs.getInt("rispostaID"));
                risposta.setTesto(rs.getString("testo"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return risposta;
    }

    public ArrayList<Risposta> getRisposteByDomanda(int domandaID) {
        ArrayList<Risposta> risposte = new ArrayList<Risposta>();
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            String query = "SELECT " +
                    "rispostaID, " +
                    "testo, " +
                    "domandaID_FK FROM risposte WHERE domandaID_FK = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, domandaID);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Risposta risposta = new Risposta("");
                risposta.setID(rs.getInt("rispostaID"));
                risposta.setTesto(rs.getString("testo"));

                risposte.add(risposta);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return risposte;
    }

    public Risposta insertRisposta(Risposta r, int domandaID) {
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {

            String query = "INSERT INTO risposte (testo, domandaID_FK) VALUES (?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, r.getTesto());
            ps.setInt(2, domandaID);

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
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {

            String query = "DELETE FROM risposte WHERE rispostaID = ? AND domandaID_FK = ?";

            PreparedStatement ps = conn.prepareStatement(query);
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
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {

            String query = "UPDATE risposte SET testo = ? WHERE rispostaID = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, r.getTesto());
            ps.setInt(2, r.getID());

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
