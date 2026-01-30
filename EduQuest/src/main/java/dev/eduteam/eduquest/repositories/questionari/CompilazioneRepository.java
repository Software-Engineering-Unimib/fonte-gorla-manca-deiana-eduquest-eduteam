package dev.eduteam.eduquest.repositories.questionari;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
        String query = "SELECT " +
                "compilazioneID, studenteID_FK, " +
                "questionarioID_FK, completato, punteggio, " +
                "numeroDomande FROM compilazioni WHERE compilazioneID = ?";

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
        List<Risposta> listaRisposte = new ArrayList<>();
        String query = "SELECT " +
                "rispostaID_FK FROM compilazioni_risposte WHERE compilazioneID_FK = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);) {

            ps.setInt(1, compilazioneID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Risposta risposta = rispostaRepository.getRispostaByID(rs.getInt("rispostaID_FK"));
                    if (risposta != null) {
                        listaRisposte.add(risposta);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Errore nel recupero risposte compilazione " + e.getMessage());
        }

        // Lista -> array
        Risposta[] arrayRisposte = new Risposta[numeroDomande];
        for (int i = 0; i < listaRisposte.size() && i < numeroDomande; i++) {
            arrayRisposte[i] = listaRisposte.get(i);
        }
        return arrayRisposte;

    }

    public ArrayList<Compilazione> getCompilazioniCompletate(int studenteID) {
        ArrayList<Compilazione> elencoCompilazioni = new ArrayList<Compilazione>();
        String query = "SELECT " +
                "compilazioneID, studenteID_FK, " +
                "questionarioID_FK, completato, punteggio, " +
                "numeroDomande FROM compilazioni WHERE studenteID_FK = ? AND completato = ?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);) {

            ps.setInt(1, studenteID);
            ps.setBoolean(2, true);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Compilazione compilazione = new Compilazione(null, null);

                    compilazione.setID(rs.getInt("compilazioneID"));
                    compilazione.setStudente(studenteRepository.getStudenteByAccountID(rs.getInt("studenteID_FK")));
                    compilazione.setQuestionario(
                            questionarioRepository.getQuestionarioByID(rs.getInt("questionarioID_FK")));
                    compilazione.setCompletato(rs.getBoolean("completato"));
                    compilazione.setPunteggio(rs.getInt("punteggio"));

                    elencoCompilazioni.add(compilazione);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return elencoCompilazioni;
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
            System.out.println("Errore inserimento compilazione: " + e.getMessage());
            return null;
        }
    }

    public boolean salvaRisposta(int compilazioneID, int rispostaID) {
        String query = "INSERT INTO compilazioni_risposte (compilazioneID_FK, rispostaID_FK) VALUES (?, ?)";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, compilazioneID);
            ps.setInt(2, rispostaID);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Errore salvataggio risposta: " + e.getMessage());
            return false;
        }
    }

    public boolean aggiornaPunteggio(int compilazioneID, int nuovPunteggio) {
        String query = "UPDATE compilazioni SET punteggio = ? WHERE compilazioneID = ?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, nuovPunteggio);
            ps.setInt(2, compilazioneID);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Errore aggiornamento punteggio: " + e.getMessage());
            return false;
        }
    }

}
