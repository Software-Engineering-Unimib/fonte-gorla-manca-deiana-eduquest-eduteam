package dev.eduteam.eduquest.repositories.questionari;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.questionari.Questionario.Difficulty;
import dev.eduteam.eduquest.repositories.ConnectionSingleton;
import dev.eduteam.eduquest.repositories.accounts.DocenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;

@Repository
public class QuestionarioRepository {

    @Autowired
    private DocenteRepository docenteRepository;

    // NOTA: si potrebbe ottimizzare modificano le query in modo che facciano JOIN,
    // riduce #chiamate alla repo

    // Helper method per mappare il ResultSet all'oggetto (evita ripetizioni di
    // codice)
    private Questionario mapResultSetToQuestionario(ResultSet rs) throws Exception {
        Docente docente = docenteRepository.getDocenteByAccountID(rs.getInt("docenteID_FK"));
        Difficulty diff = Difficulty.valueOf(rs.getString("livelloDiff"));

        Questionario q = new Questionario(
                rs.getString("nome"),
                rs.getString("descrizione"),
                new ArrayList<Domanda>(),
                docente,
                diff);
        q.setID(rs.getInt("questionarioID"));
        q.setNumeroDomande(rs.getInt("numeroDomande"));
        q.setDataCreazione(rs.getDate("dataCreazione").toLocalDate());
        return q;
    }

    // Aggiunto per permettere allo studente di controllare i questionari senza l'ID
    // del docente
    public ArrayList<Questionario> getQuestionari() {
        ArrayList<Questionario> questionari = new ArrayList<Questionario>();
        String query = "SELECT " +
                "questionarioID, " +
                "nome, " +
                "descrizione, " +
                "materia, " +
                "livelloDiff, " +
                "numeroDomande, " +
                "dataCreazione, " +
                "docenteID_FK FROM questionari";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                questionari.add(mapResultSetToQuestionario(rs));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return questionari;
    }

    // la Primary key di un questionario è questionarioID, NON SERVE IL DOCENTE
    public Questionario getQuestionarioByID(int id) {
        Questionario questionario = null;
        String query = "SELECT " +
                "questionarioID, " +
                "nome, " +
                "descrizione, " +
                "materia, " +
                "livelloDiff, " +
                "numeroDomande, " +
                "dataCreazione, " +
                "docenteID_FK FROM questionari WHERE questionarioID = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToQuestionario(rs);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return questionario;
    }

    public ArrayList<Questionario> getQuestionariByDocente(int docenteID) {
        ArrayList<Questionario> questionari = new ArrayList<Questionario>();
        String query = "SELECT " +
                "questionarioID, " +
                "nome, " +
                "descrizione, " +
                "materia, " +
                "livelloDiff, " +
                "numeroDomande, " +
                "dataCreazione, " +
                "docenteID_FK FROM questionari WHERE docenteID_FK = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);) {

            ps.setInt(1, docenteID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    questionari.add(mapResultSetToQuestionario(rs));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return questionari;
    }

    public Questionario insertQuestionario(Questionario questionario) {
        String query = "INSERT INTO questionari (nome, descrizione, materia, livelloDiff, numeroDomande, dataCreazione, docenteID_FK) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, questionario.getNome());
            ps.setString(2, questionario.getDescrizione());
            ps.setString(3, questionario.getMateria());
            ps.setString(4, questionario.getLivelloDifficulty().name());
            ps.setInt(5, questionario.getNumeroDomande());
            ps.setDate(6, java.sql.Date.valueOf(questionario.getDataCreazione()));
            ps.setInt(7, questionario.getDocente().getAccountID());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    questionario.setID(rs.getInt(1)); // Imposta l'ID generato al questionario
                }
            }
            return questionario;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean removeQuestionario(int id) {
        String query = "DELETE FROM questionari WHERE questionarioID = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean updateQuestionario(Questionario questionario) {
        boolean result = false;
        String query = "UPDATE questionari SET nome = ?, descrizione = ?, livelloDiff = ? WHERE questionarioID = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, questionario.getNome());
            ps.setString(2, questionario.getDescrizione());
            ps.setString(3, questionario.getLivelloDifficulty().name());
            ps.setInt(4, questionario.getID());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

}
