package dev.eduteam.eduquest.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.eduteam.eduquest.models.Docente;
import dev.eduteam.eduquest.repositories.DocenteRepository;

@Service
public class DocenteService {

    @Autowired
    private DocenteRepository docenteRepository;

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

}
