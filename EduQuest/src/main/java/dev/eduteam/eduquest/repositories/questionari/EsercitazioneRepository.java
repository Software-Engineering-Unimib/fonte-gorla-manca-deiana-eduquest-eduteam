package dev.eduteam.eduquest.repositories.questionari;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.springframework.stereotype.Repository;
import dev.eduteam.eduquest.models.questionari.Esercitazione;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.repositories.ConnectionSingleton;

@Repository
public class EsercitazioneRepository extends QuestionarioRepository {

    // Metodo che aggiunge una esercitazione creata nel database
    public Esercitazione insertEsercitazione(Esercitazione es) {
        // Usiamo il metodo padre per salvare nella tabella "questionari" i dati comuni
        Questionario base = super.insertQuestionario(es);

        // Garantiamo che un esercitazione non può esistere
        // se non esiste il questionario corrispondente
        if (base != null) {
            String query = "INSERT INTO esercitazioni (questionarioID_FK, noteDidattiche) VALUES (?, ?)";

            try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                    PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, base.getID());
                ps.setString(2, es.getNoteDidattiche());

                ps.executeUpdate();
                return es;
            } catch (Exception e) {
                System.out.println("Errore inserimento dettagli esercitazione: " + e.getMessage());
            }
        }
        return null;
    }

    // Metodo che aggiorna le note di una esercitazione esistente nel database
    public boolean updateNoteEsercitazione(Esercitazione es) {
        boolean result = false;
        String query = "UPDATE esercitazioni SET noteDidattiche = ? WHERE questionarioID_FK = ?";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, es.getNoteDidattiche());
            ps.setInt(2, es.getID());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
}
