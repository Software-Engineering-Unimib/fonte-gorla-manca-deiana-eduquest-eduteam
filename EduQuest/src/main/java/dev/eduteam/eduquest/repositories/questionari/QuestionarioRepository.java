package dev.eduteam.eduquest.repositories.questionari;

import com.mysql.cj.jdbc.MysqlDataSource;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.repositories.ConnectionSingleton;
import dev.eduteam.eduquest.repositories.accounts.DocenteRepository;
import dev.eduteam.eduquest.services.questionari.DomandaService;
import dev.eduteam.eduquest.services.questionari.QuestionarioService;

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

    public Questionario getQuestionarioByID(int docenteID, int id) {
        Questionario questionario = null;
        String query = "SELECT " +
                "questionarioID, " +
                "nome, " +
                "descrizione, " +
                "numeroDomande, " +
                "dataCreazione FROM questionari WHERE questionarioID = ? AND docenteID_FK = '" + docenteID + "'";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Docente docente = docenteRepository.getDocenteByAccountID(docenteID);
                    questionario = new Questionario("", "", new ArrayList<Domanda>(), docente);

                    questionario.setID(rs.getInt("questionarioID"));
                    questionario.setNome(rs.getString("nome"));
                    questionario.setDescrizione(rs.getString("descrizione"));
                    questionario.setNumeroDomande(rs.getInt("numeroDomande"));
                    questionario.setDataCreazione(rs.getDate("dataCreazione").toLocalDate());
                    questionario.setDocente(docente);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return questionario;
    }

    // MOLTO PROBABILMENTE QUESTO METODO DIVENTERA' "getQuestionariByDocente" DOPO
    // IL MERGE QUINDI CAMBIERA'
    public ArrayList<Questionario> getQuestionariByDocente(int docenteID) {
        ArrayList<Questionario> questionari = new ArrayList<Questionario>();
        String query = "SELECT " +
                "questionarioID, " +
                "nome, " +
                "descrizione, " +
                "numeroDomande, " +
                "dataCreazione FROM questionari WHERE questionarioID = ? AND docenteID_FK = '" + docenteID + "'";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Docente docente = docenteRepository.getDocenteByAccountID(docenteID);
                    Questionario questionario = new Questionario("", "", new ArrayList<Domanda>(), docente);

                    questionario.setID(rs.getInt("questionarioID"));
                    questionario.setNome(rs.getString("nome"));
                    questionario.setDescrizione(rs.getString("descrizione"));
                    questionario.setNumeroDomande(rs.getInt("numeroDomande"));
                    questionario.setDataCreazione(rs.getDate("dataCreazione").toLocalDate());
                    questionario.setDocente(docente);

                    questionari.add(questionario);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return questionari;
    }

    public Questionario insertQuestionario(Questionario questionario) {
        String query = "INSERT INTO questionari (nome, descrizione, numeroDomande, dataCreazione, docenteID_FK) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, questionario.getNome());
            ps.setString(2, questionario.getDescrizione());
            ps.setInt(3, questionario.getNumeroDomande());
            ps.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
            ps.setInt(5, questionario.getDocente().getAccountID());

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
        boolean result = false;
        String query = "DELETE FROM questionari WHERE questionarioID = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                result = true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public boolean updateQuestionario(Questionario questionario) {
        boolean result = false;
        String query = "UPDATE questionari SET nome = ?, descrizione = ? WHERE questionarioID = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, questionario.getNome());
            ps.setString(2, questionario.getDescrizione());
            ps.setInt(3, questionario.getID());

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
