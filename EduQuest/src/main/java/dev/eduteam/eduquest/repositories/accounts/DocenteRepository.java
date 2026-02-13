package dev.eduteam.eduquest.repositories.accounts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.repositories.ConnectionSingleton;

@Repository
public class DocenteRepository {

    @Autowired
    private AccountRepository accountRepository;

    public Docente getDocenteByAccountID(int accountID) {
        String query = "SELECT a.*, d.insegnamento FROM accounts a " +
                "INNER JOIN docenti d ON a.accountID = d.accountID_FK WHERE a.accountID = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, accountID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Docente d = new Docente(rs.getString("nome"), rs.getString("cognome"),
                        rs.getString("userName"), rs.getString("email"), rs.getString("password"));
                d.setAccountID(rs.getInt("accountID"));
                d.setInsegnamento(rs.getString("insegnamento"));
                return d;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public Docente insertDocente(Docente docente) {
        try {
            // Inserimento nella tabella 'account' tramite la repository comune
            int generatedID = accountRepository.insertAccount(docente, "Docente");
            docente.setAccountID(generatedID);
            // Inserimento nella tabella 'docenti'
            String query = "INSERT INTO docenti (accountID_FK, insegnamento) VALUES (?, ?)";
            try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                    PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setInt(1, docente.getAccountID());
                ps.setString(2, docente.getInsegnamento());
                ps.executeUpdate();
            }
            return docente;
        } catch (Exception e) {
            System.err.println("Errore insertDocente: " + e.getMessage());
            return null;
        }
    }

    public List<Docente> getAllDocenti() {
        List<Docente> docenti = new ArrayList<>();
        String query = "SELECT a.*, d.insegnamento FROM accounts a " +
                "INNER JOIN docenti d ON a.accountID = d.accountID_FK";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Docente d = new Docente(rs.getString("nome"), rs.getString("cognome"),
                        rs.getString("userName"), rs.getString("email"), rs.getString("password"));
                d.setAccountID(rs.getInt("accountID"));
                d.setInsegnamento(rs.getString("insegnamento"));
                docenti.add(d);
            }
        } catch (Exception e) {
            System.err.println("Errore getAllDocenti: " + e.getMessage());
        }
        return docenti;
    }

    public boolean updateDocente(Docente docente) {
        try {
            // Aggiorna i dati comuni
            accountRepository.updateAccount(docente);

            // Aggiorna i dati specifici
            String query = "UPDATE docenti SET insegnamento = ? WHERE accountID_FK = ?";
            try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                    PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, docente.getInsegnamento());
                ps.setInt(2, docente.getAccountID());
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("Errore updateDocente: " + e.getMessage());
            return false;
        }
    }

    public Questionario getQuestionarioPiuComp(int docenteID) {
        Docente docente = getDocenteByAccountID(docenteID);
        if (docente == null)
            return null;

        String query = "SELECT q.*, COUNT(c.compilazioneID) AS num_compilazioni " +
                "FROM questionari q " +
                "LEFT JOIN compilazioni c ON q.questionarioID = c.questionarioID_FK " +
                "WHERE q.docenteID_FK = ? " +
                "GROUP BY q.questionarioID " +
                "ORDER BY num_compilazioni DESC " +
                "LIMIT 1";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, docenteID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Questionario q = new Questionario(rs.getString("nome"),
                        rs.getString("descrizione"),
                        new ArrayList<>(), docente,
                        Questionario.Difficulty.valueOf(rs.getString("livelloDiff")));

                q.setID(rs.getInt("questionarioID"));
                q.setDataCreazione(rs.getDate("dataCreazione").toLocalDate());

                return q;
            }

        } catch (Exception e) {
            System.err.println("Errore query questionario più compilato: " + e.getMessage());
        }
        return null;
    }

    public Questionario getQuestionarioMigliorMediaPunt(int docenteID) {
        Docente docente = getDocenteByAccountID(docenteID);
        if (docente == null)
            return null;

        String query = "SELECT q.*, AVG(c.punteggio) AS media " +
                "FROM questionari q " +
                "JOIN compilazioni c ON q.questionarioID = c.questionarioID_FK " +
                "WHERE q.docenteID_FK = ? " +
                "GROUP BY q.questionarioID " +
                "ORDER BY media DESC " +
                "LIMIT 1";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, docenteID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Questionario q = new Questionario(
                        rs.getString("nome"),
                        rs.getString("descrizione"),
                        new ArrayList<>(),
                        docente,
                        Questionario.Difficulty.valueOf(rs.getString("livelloDiff")));
                q.setID(rs.getInt("questionarioID"));
                q.setDataCreazione(rs.getDate("dataCreazione").toLocalDate());

                return q;
            }

        } catch (Exception e) {
            System.err.println("Errore query qusetionario miglior media: " + e.getMessage());
        }
        return null;
    }

    public int getNumeroTotQuestCreati(int docenteID) {
        String query = "SELECT COUNT(*) AS totQuestionariCreati " +
                "FROM questionari q " +
                "WHERE docenteID_FK = ? ";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, docenteID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            System.err.println("Errore nel conteggio questionari docente: " + e.getMessage());
        }
        return 0;
    }

    public List<String> getCompitiniValidiConMedia(int docenteID) {
        List<String> validi = new ArrayList<>();

        String query = "SELECT q.nome, c.dataFine, IFNULL(AVG(comp.punteggio), 0) AS media_punti " +
                "FROM questionari q " +
                "INNER JOIN compitini c ON q.questionarioID = c.questionarioID_FK " +
                "LEFT JOIN compilazioni comp ON q.questionarioID = comp.questionarioID_FK " +
                "WHERE q.docenteID_FK = ? AND c.dataFine >= CURDATE() " +
                "GROUP BY q.questionarioID, q.nome, c.dataFine";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, docenteID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String info = String.format("Compitino: %s | Scadenza: %s | Media Punti: %.2f",
                        rs.getString("nome"),
                        rs.getDate("dataFine").toString(),
                        rs.getDouble("media_punti"));
                validi.add(info);
            }

        } catch (Exception e) {
            System.err.println("Errore ritorno questionari validi: " + e.getMessage());
        }
        return validi;
    }
}
