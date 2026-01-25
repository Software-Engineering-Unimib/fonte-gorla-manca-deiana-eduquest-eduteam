package dev.eduteam.eduquest.repositories;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.stereotype.Repository;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Questionario;
import dev.eduteam.eduquest.models.Risposta;

@Repository
public class DomandaRepository {

    public Domanda getDomandaByID(int questionarioID, int domandaID) {
        Domanda domanda = null;

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            String query = "SELECT " +
                    "domandaID, " +
                    "testo, " +
                    "numeroRisposte, " +
                    "rispostaCorrettaID, " +
                    "questionarioID_FK FROM domande WHERE domandaID = ? AND questionarioID_FK = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, domandaID);
            ps.setInt(2, questionarioID);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                domanda = new Domanda("");
                domanda.setID(rs.getInt("domandaID"));
                domanda.setTesto(rs.getString("testo"));
                domanda.setNumeroRisposte(rs.getInt("numeroRisposte"));

                int rispostaCorrettaID = rs.getInt("rispostaCorrettaID");
                if (rispostaCorrettaID > 0) {
                    Risposta rispostaCorretta = new Risposta("");
                    rispostaCorretta.setID(rispostaCorrettaID);
                    domanda.setRispostaCorretta(rispostaCorretta);
                } else {
                    domanda.setRispostaCorretta(null);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return domanda;
    }

    public ArrayList<Domanda> getDomandeByQuestionario(int questionarioID) {
        ArrayList<Domanda> domande = new ArrayList<Domanda>();
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            String query = "SELECT " +
                    "domandaID, " +
                    "testo, " +
                    "numeroRisposte, " +
                    "rispostaCorrettaID, " +
                    "questionarioID_FK FROM domande WHERE questionarioID_FK = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, questionarioID);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Domanda domanda = new Domanda("");
                domanda.setID(rs.getInt("domandaID"));
                domanda.setTesto(rs.getString("testo"));
                domanda.setNumeroRisposte(rs.getInt("numeroRisposte"));

                int rispostaCorrettaID = rs.getInt("rispostaCorrettaID");
                if (rispostaCorrettaID > 0) {
                    Risposta rispostaCorretta = new Risposta("");
                    rispostaCorretta.setID(rispostaCorrettaID);
                    domanda.setRispostaCorretta(rispostaCorretta);
                } else {
                    domanda.setRispostaCorretta(null);
                }

                domande.add(domanda);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return domande;
    }

    public Domanda insertDomanda(Domanda d, int questionarioID) {
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {

            String query = "INSERT INTO domande (testo, numeroRisposte, rispostaCorrettaID, questionarioID_FK) VALUES (?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, d.getTesto());
            ps.setInt(2, d.getNumeroRisposte());
            ps.setInt(3, d.getRispostaCorretta().getID());
            ps.setInt(4, questionarioID);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    d.setID(rs.getInt(1)); // Imposta l'ID generato alla domanda
                }
            }
            return d;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean removeDomanda(int domandaID, int questionarioID) {
        boolean result = false;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {

            String query = "DELETE FROM domande WHERE domandaID = ? AND questionarioID_FK = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, domandaID);
            ps.setInt(2, questionarioID);

            int rowsAffected = ps.executeUpdate();
            result = rowsAffected > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public boolean updateDomanda(Domanda d) {
        boolean result = false;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {

            // Se non erro la correttezza dei dati passati al metodo è già stata verificata
            String query = "UPDATE domande SET testo = ?,  rispostaCorrettaID = ? WHERE domandaID = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, d.getTesto());
            ps.setInt(2, d.getRispostaCorretta().getID());
            ps.setInt(3, d.getID());

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
