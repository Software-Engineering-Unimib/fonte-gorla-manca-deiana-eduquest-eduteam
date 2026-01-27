package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Questionario;
import dev.eduteam.eduquest.repositories.QuestionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class QuestionarioService {

    @Autowired
    private QuestionarioRepository questionarioRepository;

    @Autowired
    private DomandaService domandaService;

    // INIZIO ZONA TEMPORANEA

    public ArrayList<Questionario> getQuestionari() {
        return questionarioRepository.getQuestionari();
    }

    // FINE ZONA TEMPORANEA

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

    public Questionario creaQuestionario() {
        Questionario nuovo = new Questionario("Nuovo Questionario", "Nuova Descrizione", new ArrayList<Domanda>());
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

}
