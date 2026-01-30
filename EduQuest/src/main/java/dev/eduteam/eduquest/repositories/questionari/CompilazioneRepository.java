package dev.eduteam.eduquest.repositories.questionari;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.eduteam.eduquest.models.questionari.Compilazione;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.repositories.ConnectionSingleton;
import dev.eduteam.eduquest.repositories.accounts.StudenteRepository;

@Repository
public class CompilazioneRepository {

    @Autowired
    StudenteRepository studenteRepository;

    @Autowired
    QuestionarioRepository questionarioRepository;

    @Autowired
    RispostaRepository rispostaRepository;

    public Compilazione getCompilazioneByID(int compilazioneID) {
        Compilazione compilazione = null;
        String query = "SELECT compilazioneID, studenteID_FK, questionarioID_FK, completato, punteggio, numeroDomande FROM compilazioni WHERE compilazioneID = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, compilazioneID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    compilazione = new Compilazione(null, null);

                    compilazione.setID(rs.getInt("compilazioneID"));
                    compilazione.setStudente(studenteRepository.getStudenteByAccountID(rs.getInt("studenteID_FK")));
                    compilazione.setQuestionario(
                            questionarioRepository.getQuestionarioByID(rs.getInt("questionarioID_FK")));
                    compilazione.setCompletato(rs.getBoolean("completato"));
                    compilazione.setPunteggio(rs.getInt("punteggio"));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return compilazione;
    }

    public Risposta[] getRisposteCompilazione(int compilazioneID, int numeroDomande) {
        Risposta[] risposte = new Risposta[numeroDomande];
        String query = "SELECT " +
                "compilazioneID_FK, " +
                "rispostaID_FK FROM compilazioni_risposte WHERE compilazioneID_FK = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);) {

            ps.setInt(1, compilazioneID);

            try (ResultSet rs = ps.executeQuery()) {
                int i = 0;
                while (rs.next()) {
                    Risposta risposta = rispostaRepository.getRispostaByID(rs.getInt("rispostaID_FK"));
                    risposte[i] = risposta;
                    i++;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return risposte;

    }

    public Compilazione insertCompilazione(Compilazione compilazione) {
        String query = "INSERT INTO compilazioni (studenteID_FK, questionarioID_FK, completato, punteggio, numeroDomande) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, compilazione.getStudente().getAccountID());
            ps.setInt(2, compilazione.getQuestionario().getID());
            ps.setBoolean(3, false); // = compilazione.isCompletato() per valore di default
            ps.setInt(4, 0); // = compilazione.getPunteggio() per valore di default
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

}
