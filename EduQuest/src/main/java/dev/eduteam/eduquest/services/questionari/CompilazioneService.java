package dev.eduteam.eduquest.services.questionari;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.eduteam.eduquest.models.questionari.Compilazione;
import dev.eduteam.eduquest.repositories.accounts.StudenteRepository;
import dev.eduteam.eduquest.repositories.questionari.CompilazioneRepository;
import dev.eduteam.eduquest.repositories.questionari.QuestionarioRepository;

@Service
public class CompilazioneService {

    @Autowired
    private CompilazioneRepository compilazioneRepository;

    @Autowired
    private StudenteRepository studenteRepository;

    @Autowired
    private QuestionarioRepository questionarioRepository;

    public Compilazione creaCompilazione(int studenteID, int questionarioID) {
        Compilazione compilazione = new Compilazione(studenteRepository.getStudenteByAccountID(studenteID),
                questionarioRepository.getQuestionarioByID(questionarioID));
        return compilazioneRepository.insertCompilazione(compilazione);
    }

}
