package dev.eduteam.eduquest.repositories.questionari;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.models.questionari.Compilazione;
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
    DomandaRepository domandaRepository;

    @Autowired
    RispostaRepository rispostaRepository;

    private Compilazione mapResultSetToCompilazione(ResultSet rs) throws Exception {
        int studenteID = rs.getInt("studenteID_FK");
        int questionarioID = rs.getInt("questionarioID_FK");

        Studente studente = studenteRepository.getStudenteByAccountID(studenteID);
        Questionario questionario = questionarioRepository.getQuestionarioByID(questionarioID);
        questionario.setElencoDomande(domandaRepository.getDomandeByQuestionario(questionarioID));

        Compilazione c = new Compilazione(studente, questionario);
        c.setID(rs.getInt("compilazioneID"));
        c.setCompletato(rs.getBoolean("completato"));
        c.setPunteggio(rs.getInt("punteggio"));

        return c;
    }

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
                    return mapResultSetToCompilazione(rs);
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

    public ArrayList<Compilazione> getCompilazioniStatus(int studenteID, boolean status) {
        ArrayList<Compilazione> elencoCompilazioni = new ArrayList<Compilazione>();
        String query = "SELECT " +
                "compilazioneID, studenteID_FK, " +
                "questionarioID_FK, completato, punteggio, " +
                "numeroDomande FROM compilazioni WHERE studenteID_FK = ? AND completato = ?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);) {

            ps.setInt(1, studenteID);
            ps.setBoolean(2, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    elencoCompilazioni.add(mapResultSetToCompilazione(rs));
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

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Errore aggiornamento punteggio: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatusCompilazione(int compilazioneID, boolean isCompletato) {
        String query = "UPDATE compilazioni SET completato = ? WHERE compilazioneID = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            if (isCompletato) {
                ps.setInt(1, 1);
            } else {
                ps.setInt(1, 0);
            }
            ps.setInt(2, compilazioneID);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Errore aggiornamento status compilazione: " + e.getMessage());
            return false;
        }
    }

    public Compilazione getCompilazioneInSospeso(int studenteID, int questionarioID) {
        String query = "SELECT * FROM compilazioni WHERE studenteID_FK = ? AND questionarioID_FK = ? AND completato = false LIMIT 1";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, studenteID);
            ps.setInt(2, questionarioID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCompilazione(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Errore, nessuna compilazione in sospeso: " + e.getMessage());
        }
        return null;
    }

    public boolean esisteCompilazione(int studenteID, int questionarioID) {

        String query = "SELECT COUNT(compilazioneID) FROM compilazioni WHERE studenteID_FK = ? AND questionarioID_FK = ?;";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, studenteID);
            ps.setInt(2, questionarioID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (rs.getInt(1) == 0) {
                        return false;
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Errore, nessuna compilazione in sospeso: " + e.getMessage());
        }
        return false;
    }

    // Funzioni Dashboard

    // DashboardService si occuperà di recuperare le info dei questionari delle
    // compilazioni nella lista tonrata dal metodo
    public ArrayList<Compilazione> getTopCompilazioniStudente(int studenteID, int numCompilazioni) {
        ArrayList<Compilazione> elencoTopCompilazioni = new ArrayList<Compilazione>();
        String query = "SELECT * FROM compilazioni " +
                "WHERE studenteID_FK = ? AND completato = 1 " +
                "ORDER BY punteggio DESC " +
                "LIMIT ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, studenteID);
            ps.setInt(2, numCompilazioni);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    elencoTopCompilazioni.add(mapResultSetToCompilazione(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Errore nel recupero top 3 compilazioni per studente " + e.getMessage());
        }
        return elencoTopCompilazioni;
    }

    // Classifica globale: migliori compilazioni tra tutti gli studenti
    public ArrayList<Compilazione> getTopCompilazioniGlobale(int numCompilazioni) {
        ArrayList<Compilazione> classifica = new ArrayList<>();
        String query = "SELECT * FROM compilazioni " +
                "WHERE completato = 1 " +
                "ORDER BY punteggio DESC " +
                "LIMIT ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, numCompilazioni);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    classifica.add(mapResultSetToCompilazione(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Errore recupero classifica globale: " + e.getMessage());
        }
        return classifica;
    }

    // Classifica per un singolo questionario: migliori punteggi
    public ArrayList<Compilazione> getTopCompilazioniPerQuestionario(int questionarioID, int numCompilazioni) {
        ArrayList<Compilazione> classifica = new ArrayList<>();
        String query = "SELECT * FROM compilazioni " +
                "WHERE questionarioID_FK = ? AND completato = 1 " +
                "ORDER BY punteggio DESC " +
                "LIMIT ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, questionarioID);
            ps.setInt(2, numCompilazioni);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    classifica.add(mapResultSetToCompilazione(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Errore recupero classifica questionario: " + e.getMessage());
        }
        return classifica;
    }
}
