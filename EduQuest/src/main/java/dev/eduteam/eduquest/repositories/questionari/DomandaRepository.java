package dev.eduteam.eduquest.repositories.questionari;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import dev.eduteam.eduquest.models.questionari.Risposta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.repositories.ConnectionSingleton;

@Repository
public class DomandaRepository {

    @Autowired
    private RispostaRepository rispostaRepository;

    // DA VALUTARE: Dato che l'ID delle domande è univoco si potrebbe fare un metodo
    // in overload per la ricerca con solo l'ID della domanda
    // PER ORA li ho messi entrambi

    // Aggiungi questo metodo per le operazioni in cui conosci solo l'ID della
    // domanda
    public Domanda getDomandaByID(int domandaID) {
        Domanda domanda = null;
        String query = "SELECT domandaID, tipo, testo, numeroRisposte, questionarioID_FK FROM domande WHERE domandaID = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, domandaID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int tipo = rs.getInt("tipo");
                    Domanda.Type tipoEnum = Domanda.Type.values()[tipo - 1];
                    domanda = Domanda.createDomandaOfType(tipoEnum);
                    domanda.setID(rs.getInt("domandaID"));
                    domanda.setTesto(rs.getString("testo"));
                    domanda.setNumeroRisposte(rs.getInt("numeroRisposte"));

                    ArrayList<Risposta> risposte = rispostaRepository.getRisposteByDomanda(domanda.getID());
                    domanda.setElencoRisposte(risposte);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return domanda;
    }

    public Domanda getDomandaByID(int questionarioID, int domandaID) {
        Domanda domanda = null;
        String query = "SELECT " +
                "domandaID, " +
                "tipo, " +
                "testo, " +
                "numeroRisposte, " +
                "questionarioID_FK FROM domande WHERE domandaID = ? AND questionarioID_FK = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);) {

            ps.setInt(1, domandaID);
            ps.setInt(2, questionarioID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Usiamo il valore numerico del tipo per creare la sottoclasse corretta
                    // 1=MULTIPLA, 2=MULTIPLE_RISPOSTE, 3=VERO_FALSO
                    int tipo = rs.getInt("tipo");
                    Domanda.Type tipoEnum = Domanda.Type.values()[tipo - 1];

                    domanda = Domanda.createDomandaOfType(tipoEnum);
                    domanda.setID(rs.getInt("domandaID"));
                    domanda.setTesto(rs.getString("testo"));
                    domanda.setNumeroRisposte(rs.getInt("numeroRisposte"));

                    ArrayList<Risposta> risposte = rispostaRepository.getRisposteByDomanda(domanda.getID());
                    domanda.setElencoRisposte(risposte);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return domanda;
    }

    // -------------------------

    public ArrayList<Domanda> getDomandeByQuestionario(int questionarioID) {
        ArrayList<Domanda> elecoDomande = new ArrayList<Domanda>();
        String query = "SELECT " +
                "domandaID, " +
                "tipo, " +
                "testo, " +
                "numeroRisposte, " +
                "questionarioID_FK FROM domande WHERE questionarioID_FK = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, questionarioID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int tipo = rs.getInt("tipo");
                    Domanda.Type tipoEnum = Domanda.Type.values()[tipo - 1];

                    Domanda domanda = Domanda.createDomandaOfType(tipoEnum);
                    domanda.setID(rs.getInt("domandaID"));
                    domanda.setTesto(rs.getString("testo"));
                    domanda.setNumeroRisposte(rs.getInt("numeroRisposte"));

                    ArrayList<Risposta> risposte = rispostaRepository.getRisposteByDomanda(domanda.getID());
                    domanda.setElencoRisposte(risposte);

                    elecoDomande.add(domanda);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return elecoDomande;
    }

    public Domanda insertDomanda(Domanda d, int questionarioID) {
        String query = "INSERT INTO domande (tipo, testo, numeroRisposte, questionarioID_FK) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, d.getTipoDomanda().ordinal() + 1);
            ps.setString(2, d.getTesto());
            ps.setInt(3, d.getNumeroRisposte());
            ps.setInt(4, questionarioID);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    d.setID(rs.getInt(1)); // Imposta l'ID generato alla domanda
                }
            }
            return d;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean removeDomanda(int domandaID, int questionarioID) {
        boolean result = false;
        String query = "DELETE FROM domande WHERE domandaID = ? AND questionarioID_FK = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query);) {

            ps.setInt(1, domandaID);
            ps.setInt(2, questionarioID);

            int rowsAffected = ps.executeUpdate();
            result = rowsAffected > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public boolean updateDomanda(Domanda d) {
        // Una volta creata la domanda, il tipo non può essere modificato
        boolean result = false;
        String query = "UPDATE domande SET testo = ? WHERE domandaID = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, d.getTesto());
            ps.setInt(2, d.getID());

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
