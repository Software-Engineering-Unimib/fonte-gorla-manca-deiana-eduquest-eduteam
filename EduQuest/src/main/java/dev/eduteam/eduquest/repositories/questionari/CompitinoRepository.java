package dev.eduteam.eduquest.repositories.questionari;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.springframework.stereotype.Repository;

import dev.eduteam.eduquest.models.questionari.Compitino;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.repositories.ConnectionSingleton;

@Repository
public class CompitinoRepository extends QuestionarioRepository {

    public Compitino insertCompitino(Compitino c) {
        // Usiamo il metodo padre per salvare nella tabella "questionari" i dati comuni
        Questionario base = super.insertQuestionario(c);

        // Garantiamo che un compitino non può esistere
        // se non esiste il questionario corrispondente
        if (base != null) {
            String query = "INSERT INTO compitini (questionarioID_FK, dataFine, tentativiMax, puntiBonus, assegnatiPtBonus) VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                    PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, base.getID());
                ps.setDate(2, java.sql.Date.valueOf(c.getDataFine()));
                ps.setInt(3, c.getTentativiMax());
                ps.setInt(4, c.getPuntiBonus());
                ps.setBoolean(5, c.getAssegnatiPtBonus());

                ps.executeUpdate();
                return c;
            } catch (Exception e) {
                System.out.println("Errore inserimento dettagli compitino: " + e.getMessage());
            }
        }
        return null;
    }

    public int countTentativi(int studenteID, int questionarioID) {
        String query = "SELECT COUNT(*) FROM compilazioni WHERE studenteID_FK = ? AND questionarioID_FK = ?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, studenteID);
            ps.setInt(2, questionarioID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public boolean updateCompitino(Compitino comp) {
        boolean result = false;
        String query = "UPDATE compitini SET dataFine = ?, tentativiMax = ?, puntiBonus = ?, assegnatiPtBonus = ? WHERE questionarioID_FK = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setDate(1, Date.valueOf(comp.getDataFine()));
            ps.setInt(2, comp.getTentativiMax());
            ps.setInt(3, comp.getPuntiBonus());
            ps.setBoolean(4, comp.getAssegnatiPtBonus());
            ps.setInt(5, comp.getID());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
}
