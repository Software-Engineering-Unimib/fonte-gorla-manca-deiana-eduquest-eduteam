package dev.eduteam.eduquest.services.accounts;

import java.util.ArrayList;
import java.util.List;

import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.repositories.questionari.QuestionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.repositories.accounts.DocenteRepository;

@Service
public class DocenteService {

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private QuestionarioRepository questionarioRepository;

    public Docente getByID(int id) {
        return docenteRepository.getDocenteByAccountID(id);
    }

    public Docente registraDocente(Docente docente) {
        return docenteRepository.insertDocente(docente);
    }

    public List<Docente> getAll() {
        return docenteRepository.getAllDocenti();
    }

    public boolean aggiornaDocente(Docente docente) {
        return docenteRepository.updateDocente(docente);
    }

    public boolean aggiornaInsegnamento(int id, String nuovaMateria) {
        Docente d = docenteRepository.getDocenteByAccountID(id);
        if (d != null) {
            d.setInsegnamento(nuovaMateria);
            return docenteRepository.updateDocente(d);
        }
        return false;
    }

    public boolean proprietarioQuestionario(Docente docente, Questionario questionario) {
        ArrayList<Questionario> questionari = questionarioRepository.getQuestionariByDocente(docente.getAccountID());

        for (Questionario q : questionari) {
            if (q.getID() == questionario.getID()) {
                return true;
            }
        }
        return false;
    }

}
