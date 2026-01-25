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

    // INIZIO ZONA TEMPORANEA

    public ArrayList<Questionario> getQuestionari() {
        return questionarioRepository.getQuestionari();
    }

    // FINE ZONA TEMPORANEA

    public Questionario getQuestionario(int ID) {
        return questionarioRepository.getQuestionarioByID(ID);
    }

    public Questionario creaQuestionario() {
        Questionario nuovo = new Questionario("", "", new ArrayList<Domanda>());
        return questionarioRepository.insertQuestionario(nuovo);
    }

    public boolean rimuoviQuestionario(int ID) {
        return questionarioRepository.removeQuestionario(ID);
    }

    public boolean modificaNome(Questionario questionario, String nome) {
        questionario.setNome(nome);
        return questionarioRepository.updateQuestionario(questionario);
    }

    public boolean modificaDescrizione(Questionario questionario, String descrizione) {
        questionario.setDescrizione(descrizione);
        return questionarioRepository.updateQuestionario(questionario);
    }

}
