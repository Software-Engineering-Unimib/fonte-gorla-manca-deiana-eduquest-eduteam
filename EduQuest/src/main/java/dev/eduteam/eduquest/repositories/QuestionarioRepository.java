package dev.eduteam.eduquest.repositories;

import com.mysql.cj.jdbc.MysqlDataSource;
import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Questionario;
import dev.eduteam.eduquest.models.Risposta;
import dev.eduteam.eduquest.services.DomandaService;
import dev.eduteam.eduquest.services.QuestionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;

@Repository
public class QuestionarioRepository {

    private final String serverName = "Localhost";
    private final int portNumber = 3306;
    private final String userName = "root";
    private final String password = "nicksql";
    private final String databaseName = "Test_Questionario";
    private final boolean useSSL = false;
    private final boolean allowPublicKeyRetrieval = true;

    private Connection connect() {

        try {
            MysqlDataSource ds = new MysqlDataSource();
            ds.setServerName(serverName);
            ds.setPortNumber(portNumber);
            ds.setUser(userName);
            ds.setPassword(password);
            ds.setDatabaseName(databaseName);
            ds.setUseSSL(useSSL);
            ds.setAllowPublicKeyRetrieval(allowPublicKeyRetrieval);

            return ds.getConnection();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // - METODI DI GET / READ -
    public Questionario getQuestionarioByID(int id) {

        return null;
    }

    // MOLTO PROBABILMENTE QUESTO METODO DIVENTERA' "getQuestionariByDocente" DOPO IL MERGE
    // QUINDI CAMBIERA'
    public ArrayList<Questionario> getQuestionari() {

        ArrayList<Questionario> questionari = new ArrayList<Questionario>();

        Connection conn = connect();

        if (conn != null) {
            try {
                String query = "SELECT " +
                        "questionarioID, " +
                        "nome, " +
                        "descrizione, " +
                        "numeroDomande, " +
                        "dataCreazione FROM questionari";

                PreparedStatement ps = conn.prepareStatement(query);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Questionario questionario = new Questionario("", "", new ArrayList<Domanda>());

                    questionario.setID(rs.getInt("questionarioID"));
                    questionario.setNome(rs.getString("nome"));
                    questionario.setDescrizione(rs.getString("descrizione"));
                    questionario.setNumeroDomande(rs.getInt("numeroDomande"));
                    questionario.setDataCreazione(rs.getDate("dataCreazione").toLocalDate());

                    questionari.add(questionario);
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return questionari;
    }

    public ArrayList<Domanda> getDomandeByQuestionario(Questionario questionario) {

        return null;
    }

    public ArrayList<Risposta> getRispostaByDomanda(Domanda domanda) {

        return null;
    }
    // - FINE METODI DI GET / READ -

    // - METODI DI ADD / INSERT -
    public boolean addQuestionario(Questionario questionario) {

        return false;
    }

    public boolean addDomanda(Questionario questionario, Domanda domanda) {

        return false;
    }

    public boolean addRisposta(Domanda domanda, Risposta risposta) {

        return false;
    }
    // - FINE METODI DI ADD / INSERT -

    // - METODI DI REMOVE / DELETE -
    public boolean removeQuestionario(Questionario questionario) {

        return false;
    }

    public boolean removeDomanda(Questionario questionario, Domanda domanda) {

        return false;
    }

    public boolean removeRisposta(Domanda domanda, Risposta risposta) {

        return false;
    }
    // - FINE METODI DI REMOVE / DELETE -

    // - METODI DI SET / UPDATE -
    public boolean setQuestionario(Questionario questionario) {

        return false;
    }

    public boolean setDomanda(Questionario questionario, Domanda domanda) {

        return false;
    }

    public boolean setRisposta(Domanda domanda, Risposta risposta) {

        return false;
    }
    // - FINE DEI METODI DI SET / UPDATE -

}
