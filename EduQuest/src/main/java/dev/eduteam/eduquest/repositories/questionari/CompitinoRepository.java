package dev.eduteam.eduquest.repositories.questionari;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.eduteam.eduquest.models.questionari.Compitino;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Questionario.Difficulty;
import dev.eduteam.eduquest.repositories.ConnectionSingleton;
import dev.eduteam.eduquest.repositories.accounts.DocenteRepository;

@Repository
public class CompitinoRepository {

    @Autowired
    private DocenteRepository docenteRepository;

    // serve? visto che c'è il getQuestionario nell'altro Repo
    public Compitino getCompitinoByID(int questionarioID) {
        String query = "SELECT q.*, c.dataFine, c.tentativiMax " +
                "FROM questionari q " +
                "JOIN compitini c ON q.questionarioID = c.questionarioID_FK " +
                "WHERE q.questionarioID = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, questionarioID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {

                Compitino comp = new Compitino(
                        rs.getString("nome"),
                        rs.getString("descrizione"),
                        new ArrayList<Domanda>(),
                        docenteRepository.getDocenteByAccountID(rs.getInt("docenteID_FK")),
                        Difficulty.valueOf(rs.getString("livelloDiff")),
                        rs.getDate("dataFine").toLocalDate(),
                        rs.getInt("tentativiMax"));
                comp.setID(rs.getInt("questionarioID"));
                return comp;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean insertDettagliCompitino(Compitino c, int questionarioID) {
        String query = "INSERT INTO compitini (questionarioID_FK, dataFine, tentativiMax) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, questionarioID);
            // Conversione da LocalDate a java.sql.Date
            ps.setDate(2, java.sql.Date.valueOf(c.getDataFine()));
            ps.setInt(3, c.getTentativiMax());

            int righeInserite = ps.executeUpdate();
            return righeInserite > 0;

        } catch (Exception e) {
            System.out.println("Errore inserimento dettagli compitino: " + e.getMessage());
        }
        return false;
    }

    public int countTentativi(int studenteID, int questionarioID) {
        String query = "SELECT COUNT(*) FROM compilazioni WHERE studenteID_FK = ? AND questionarioID_FK = ?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, studenteID);
            ps.setInt(2, questionarioID);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }
}
