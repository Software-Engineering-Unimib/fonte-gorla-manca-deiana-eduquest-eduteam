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
    private DomandaService domandaService;

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

    public Domanda getDomanda(Questionario questionario, int ID) {

        return questionario.getElencoDomande().stream().filter(d -> d.getID() == ID).findFirst().orElse(null);
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

    // Forse da spostare in DomandaService
    // Non rivisti per bene
    public boolean aggiungiDomanda(Questionario questionario) {

        Domanda domanda = domandaService.creaDomanda();
        questionario.setNumeroDomande(questionario.getNumeroDomande() + 1);
        questionario.getElencoDomande().add(domanda);
        return questionarioRepository.updateQuestionario(questionario);
    }

    public boolean rimuoviDomanda(int ID, int domandaID) {

        Questionario questionario = getQuestionario(ID);
        Domanda domanda = getDomanda(questionario, domandaID);
        if (domanda == null) {
            return false;
        }
        questionario.setNumeroDomande(questionario.getNumeroDomande() - 1);
        questionario.getElencoDomande().remove(domanda);
        return questionarioRepository.updateQuestionario(questionario);
    }
}
