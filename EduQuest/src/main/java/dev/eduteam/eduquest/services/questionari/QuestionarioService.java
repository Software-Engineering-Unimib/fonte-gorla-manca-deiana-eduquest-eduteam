package dev.eduteam.eduquest.services.questionari;

import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.repositories.accounts.DocenteRepository;
import dev.eduteam.eduquest.repositories.questionari.QuestionarioRepository;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class QuestionarioService {

    @Autowired
    private QuestionarioRepository questionarioRepository;
    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private DomandaService domandaService;

    // Aggiunto per permettere allo studente di controllare i questionari senza l'ID
    // del docente
    public ArrayList<Questionario> getQuestionari() {
        return questionarioRepository.getQuestionari();
    }

    public ArrayList<Questionario> getQuestionariByDocente(int docenteID) {
        return questionarioRepository.getQuestionariByDocente(docenteID);
    }

    public Questionario getQuestionarioCompleto(int ID) {
        Questionario questionario = questionarioRepository.getQuestionarioByID(ID);

        // Controllo che questionario non sia null prima di usarlo
        if (questionario != null) {
            // Recupero le domande (che a loro volta hanno già le risposte caricate)
            ArrayList<Domanda> domande = domandaService.getDomandeComplete(ID);
            // "Allego" le domande al questionario prima di restituirlo
            questionario.setElencoDomande(domande);
        }
        return questionario;
    }

    public Questionario creaQuestionario(int docenteID) {
        Questionario nuovo = new Questionario("Nuovo Questionario", "Nuova Descrizione", new ArrayList<Domanda>(),
                docenteRepository.getDocenteByAccountID(docenteID));
        return questionarioRepository.insertQuestionario(nuovo);
    }

    public boolean rimuoviQuestionario(int ID) {
        return questionarioRepository.removeQuestionario(ID);
    }

    // Uniti i due metodi di modifica e sistemata logica in controller
    public boolean modificaInfo(Questionario questionario, String nome, String descrizione) {
        questionario.setNome(nome);
        questionario.setDescrizione(descrizione);
        return questionarioRepository.updateQuestionario(questionario);
    }

    public Domanda getDomandaSuccessiva(int questionarioID, int domandaID) {
        Questionario q = questionarioRepository.getQuestionarioByID(questionarioID);
        ArrayList<Domanda> elenco = q.getElencoDomande();
        for (int i = 0; i < elenco.size(); i++) {
            if (elenco.get(i).getID() == domandaID) {
                if (i + 1 < elenco.size()) {
                    return elenco.get(i + 1);
                }
                // Trovata, ma era l'ultima: non c'è una successiva
                return null;
            }
        }
        // Si potrebbe definire un'eccezione specifica per distinguere
        // tra non c'è e ID non valido
        return null;
    }

}
