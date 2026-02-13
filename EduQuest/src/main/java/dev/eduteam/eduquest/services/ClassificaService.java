package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.models.questionari.Compilazione;
import dev.eduteam.eduquest.repositories.accounts.StudenteRepository;
import dev.eduteam.eduquest.repositories.questionari.CompilazioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClassificaService {

    @Autowired
    private StudenteRepository studenteRepository;

    @Autowired
    private CompilazioneRepository compilazioneRepository;

    // Classifica globale per EduPoints
    public List<Studente> getClassificaEduPoints(int limite) {
        return studenteRepository.getTopStudentiPerEduPoints(limite);
    }

    // Classifica globale per Media Punteggio
    public List<Studente> getClassificaMedia(int limite) {
        return studenteRepository.getTopStudentiPerMedia(limite);
    }

    // Classifica migliori compilazioni globali (tutti gli studenti)
    public List<Compilazione> getClassificaCompilazioniGlobale(int limite) {
        return compilazioneRepository.getTopCompilazioniGlobale(limite);
    }

    // Classifica migliori compilazioni per un singolo questionario
    public List<Compilazione> getClassificaPerQuestionario(int questionarioID, int limite) {
        return compilazioneRepository.getTopCompilazioniPerQuestionario(questionarioID, limite);
    }
}
