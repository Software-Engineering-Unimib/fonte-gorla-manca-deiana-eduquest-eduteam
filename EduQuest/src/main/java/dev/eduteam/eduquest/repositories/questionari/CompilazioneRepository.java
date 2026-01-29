package dev.eduteam.eduquest.repositories.questionari;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Repository;

import dev.eduteam.eduquest.models.questionari.Compilazione;
import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.repositories.ConnectionSingleton;

@Repository
public class CompilazioneRepository {

    public Compilazione insertCompilazione(Compilazione compilazione) {
        String query = "INSERT INTO compilazioni (studenteID_FK, questionarioID_FK, completato, punteggio, numeroDomande) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, compilazione.getStudente().getAccountID());
            ps.setInt(2, compilazione.getQuestionario().getID());
            ps.setBoolean(3, false);
            ps.setInt(4, 0);
            ps.setInt(5, compilazione.getNumeroDomande());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    compilazione.setID(rs.getInt(1));
                }
            }
            return compilazione;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // altro metodo che gestisce anche l'array delle risposte
    // controllare quello che è più giusto da inserire
    /*
     * public Compilazione insertCompilazione(Compilazione compilazione) {
     * String queryComp =
     * "INSERT INTO compilazioni (studenteID_FK, questionarioID_FK, completato, punteggio, numeroDomande) VALUES (?, ?, ?, ?, ?)"
     * ;
     * String queryResp =
     * "INSERT INTO compilazioni_risposte (compilazioneID_FK, rispostaID_FK) VALUES (?, ?)"
     * ;
     * 
     * try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
     * conn.setAutoCommit(false); // Inizia transazione
     * 
     * try (PreparedStatement psComp = conn.prepareStatement(queryComp,
     * PreparedStatement.RETURN_GENERATED_KEYS);
     * PreparedStatement psResp = conn.prepareStatement(queryResp)) {
     * 
     * psComp.setInt(1, compilazione.getStudente().getAccountID());
     * psComp.setInt(2, compilazione.getQuestionario().getID());
     * psComp.setBoolean(3, compilazione.isCompletato());
     * psComp.setInt(4, compilazione.getPunteggio());
     * psComp.setInt(5, compilazione.getNumeroDomande());
     * psComp.executeUpdate();
     * 
     * try (ResultSet rs = psComp.getGeneratedKeys()) {
     * if (rs.next()) {
     * compilazione.setID(rs.getInt(1));
     * }
     * }
     * 
     * if (compilazione.getRisposte() != null) {
     * for (Risposta r : compilazione.getRisposte()) {
     * psResp.setInt(1, compilazione.getID());
     * psResp.setInt(2, r.getID());
     * psResp.addBatch();
     * }
     * psResp.executeBatch();
     * }
     * 
     * conn.commit();
     * return compilazione;
     * 
     * } catch (Exception e) {
     * conn.rollback();
     * throw e;
     * }
     * } catch (Exception e) {
     * e.printStackTrace();
     * return null;
     * }
     * }
     * 
     */
}
