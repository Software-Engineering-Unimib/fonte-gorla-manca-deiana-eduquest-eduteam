package dev.eduteam.eduquest.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.eduteam.eduquest.models.Studente;
import dev.eduteam.eduquest.repositories.StudenteRepository;

@Service
public class StudenteService {

    @Autowired
    private StudenteRepository studenteRepository;

    public Studente getById(int id) {
        return studenteRepository.getStudenteByAccountID(id);
    }

    public Studente registraStudente(Studente studente) {
        if (studente.getMediaPunteggio() == 0) {
            studente.setMediaPunteggio(0.0);
        }
        return studenteRepository.insertStudente(studente);
    }

    public List<Studente> getAll() {
        return studenteRepository.getAllStudenti();
    }

    public boolean aggiornaStudente(Studente studente) {
        return studenteRepository.updateStudente(studente);
    }
    /*
     * L'aggiornaMedia verrà implementata quando avremo la compilazione
     * public boolean aggiornaMedia(int id, double nuovoVoto) {
     * Studente s = studenteRepository.getStudenteByAccountID(id);
     * if (s != null) {
     * double vecchiaMedia = s.getMediaPunteggio();
     * s.setMediaPunteggio(vecchiaMedia);
     * }
     * }
     */

}
