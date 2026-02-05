package dev.eduteam.eduquest.repositories.questionari;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Feedback;
import dev.eduteam.eduquest.repositories.ConnectionSingleton;

@Repository
public class FeedbackRepository {

    @Autowired
    DomandaRepository domandaRepository;

    /*
     * Recupera il feedback per una determinata domanda
     * Potrebbe essere ottimizzato passando Domanda d al posto di domandaID (fatto
     * con ID per consistenza con gli altri repo)
     */
    public Feedback getFeedbackByDomanda(int domandaID) {
        Feedback f = null;
        String query = "SELECT feedbackID, " +
                "testo, " +
                "domandaID_FK FROM feedback WHERE domandaID_FK = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, domandaID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Domanda d = domandaRepository.getDomandaByID(domandaID);
                    f = new Feedback(d, rs.getString("testo"));
                    f.setID(rs.getInt("feedbackID"));
                }
            }
        } catch (Exception e) {
            System.out.println("Errore recupero feedback: " + e.getMessage());
        }
        return f;
    }

    public Feedback insertFeedback(Feedback feedback) {
        String query = "INSERT INTO feedback (testo, domandaID_FK) VALUES (?, ?)";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, feedback.getTesto());
            ps.setInt(2, feedback.getDomanda().getID());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    feedback.setID(rs.getInt(1));
                }
            }
            return feedback;
        } catch (Exception e) {
            System.out.println("Errore inserimento feedback: " + e.getMessage());
            return null;
        }
    }
}
