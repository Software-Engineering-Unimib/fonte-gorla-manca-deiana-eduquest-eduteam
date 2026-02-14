package dev.eduteam.eduquest.repositories.accounts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.repositories.ConnectionSingleton;

@Repository
public class StudenteRepository {

    @Autowired
    private AccountRepository accountRepository;

    // Prende un result set ottenuto da una query per recuperare uno studente, mappa tutti gli attributi del result set
    // agli attributi di studente e ritorna lo studente mappato
    private Studente mapResultSetToStudente(ResultSet rs) throws Exception {
        Studente s = new Studente(rs.getString("nome"), rs.getString("cognome"),
                rs.getString("userName"), rs.getString("email"), rs.getString("password"));
        s.setAccountID(rs.getInt("accountID"));
        s.setMediaPunteggio(rs.getDouble("mediaPunteggio"));
        s.setEduPoints(rs.getInt("eduPoints"));

        return s;
    }

    // Metodo che recupera uno studente tramite il suo id dal database
    public Studente getStudenteByAccountID(int accountID) {
        String query = "SELECT a.*, s.mediaPunteggio, s.eduPoints FROM accounts a " +
                "INNER JOIN studenti s ON a.accountID = s.accountID_FK WHERE a.accountID = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, accountID);
            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapResultSetToStudente(rs);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    // Metodo che aggiunge uno studente creato al database
    public Studente insertStudente(Studente studente) {
        try {
            // Inserimento nella tabella 'account' tramite la repository comune
            int generatedID = accountRepository.insertAccount(studente, "Studente");
            studente.setAccountID(generatedID);
            String query = "INSERT INTO studenti (accountID_FK, mediaPunteggio, eduPoints) VALUES (?, ?, ?)";
            // Inserimento nella tabella 'studenti'
            try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                    PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, studente.getAccountID());
                ps.setDouble(2, studente.getMediaPunteggio());
                ps.setInt(3, studente.getEduPoints());
                ps.executeUpdate();
            }
            return studente;
        } catch (Exception e) {
            System.err.println("Errore insertStudente: " + e.getMessage());
            return null;
        }
    }

    // Metodo che recupera tutti gli studenti nel database
    public List<Studente> getAllStudenti() {
        List<Studente> studenti = new ArrayList<>();
        String query = "SELECT a.*, s.mediaPunteggio, s.eduPoints FROM accounts a " +
                "INNER JOIN studenti s ON a.accountID = s.accountID_FK";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                studenti.add(mapResultSetToStudente(rs));
            }
        } catch (Exception e) {
            System.err.println("Errore getAllStudenti: " + e.getMessage());
        }
        return studenti;
    }

    // Metodo che aggiorna uno studente già esistente nel database
    public boolean updateStudente(Studente studente) {
        try {
            // Aggiorna i dati comuni nella tabella account
            accountRepository.updateAccount(studente);

            // Aggiorna i dati specifici nella tabella studenti
            String query = "UPDATE studenti SET mediaPunteggio = ?, eduPoints = ? WHERE accountID_FK = ?";
            try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                    PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setDouble(1, studente.getMediaPunteggio());
                ps.setInt(2, studente.getEduPoints());
                ps.setInt(3, studente.getAccountID());
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("Errore updateStudente: " + e.getMessage());
            return false;
        }
    }

    // -= Funzioni Dashboard =-

    // Metodo che recupera una lista dei primi "numStudenti" studenti per media dal database
    public ArrayList<Studente> getTopStudentiPerMedia(int numStudenti) {
        ArrayList<Studente> elencoTopStudenti = new ArrayList<Studente>();
        String query = "SELECT a.*, s.mediaPunteggio, s.eduPoints " +
                "FROM studenti s " +
                "JOIN accounts a ON s.accountID_FK = a.accountID " +
                "ORDER BY s.mediaPunteggio DESC " +
                "LIMIT ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, numStudenti);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    elencoTopStudenti.add(mapResultSetToStudente(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Errore nel recupero top 3 studenti per media " + e.getMessage());
        }
        return elencoTopStudenti;
    }

    // Metodo che recupera una lista dei primi "numStudenti" studenti per eduPoints dal database
    public ArrayList<Studente> getTopStudentiPerEduPoints(int numStudenti) {
        ArrayList<Studente> elencoTopStudenti = new ArrayList<Studente>();
        String query = "SELECT a.*, s.mediaPunteggio, s.eduPoints " +
                "FROM studenti s " +
                "JOIN accounts a ON s.accountID_FK = a.accountID " +
                "ORDER BY s.eduPoints DESC " +
                "LIMIT ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, numStudenti);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    elencoTopStudenti.add(mapResultSetToStudente(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Errore nel recupero top 3 studenti per eduPoints " + e.getMessage());
        }
        return elencoTopStudenti;
    }

    // Classe di supporto per tornare i risultati del metodo
    // getStatisticheBaseStudente
    public class RiepilogoStudente {
        public double media;
        public int eduPoints;
        public int numeroCompilazioni;
    }

    // Ritona un DTO che rappresenta le statistiche di uno specifico studente
    public RiepilogoStudente getStatisticheBaseStudente(int studenteID) {
        // Vengono contate solo le compilazioni completate
        String query = "SELECT s.mediaPunteggio, s.eduPoints, COUNT(CASE WHEN c.completato = 1 THEN 1 END) as totale " +
                "FROM studenti s " +
                "LEFT JOIN compilazioni c ON s.accountID_FK = c.studenteID_FK " +
                "WHERE s.accountID_FK = ? " +
                "GROUP BY s.accountID_FK, s.mediaPunteggio, s.eduPoints";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, studenteID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RiepilogoStudente r = new RiepilogoStudente();
                    r.media = rs.getDouble("mediaPunteggio");
                    r.eduPoints = rs.getInt("eduPoints");
                    r.numeroCompilazioni = rs.getInt("totale");
                    return r;
                }
            }
        } catch (Exception e) {
            System.err.println("Errore recupero statistiche studente: " + e.getMessage());
        }
        return null;
    }

    // Metodo che recupera una lista dei primi "limit" studenti che hanno vinto punti bonus di un compitino
    public List<Studente> getVincitoriBonusCompitino(int questionarioID, int limit) {
        List<Studente> vincitori = new ArrayList<>();
        String query = "SELECT a.*, MAX(c.punteggio) as migliorVoto, s.puntiBonus " +
                "FROM compilazioni c " +
                "JOIN studenti s ON c.studenteID_FK = s.accountID_FK " +
                "JOIN accounts a ON s.accountID_FK = a.accountID " +
                "WHERE c.questionarioID_FK = ? AND c.completato = TRUE " +
                "GROUP BY c.studenteID_FK " +
                "ORDER BY migliorVoto DESC " +
                "LIMIT ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, questionarioID);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vincitori.add(mapResultSetToStudente(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Errore calcolo vincitori bonus: " + e.getMessage());
        }
        return vincitori;
    }
}
