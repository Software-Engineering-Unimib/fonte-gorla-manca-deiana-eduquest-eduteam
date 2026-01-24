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

        return getQuestionari().stream().filter(q -> q.getID() == ID).findFirst().orElse(null);
    }

    public Domanda getDomanda(Questionario questionario, int ID) {

        return questionario.getElencoDomande().stream().filter(d -> d.getID() == ID).findFirst().orElse(null);
    }

    public Questionario creaQuestionario() {

        Questionario questionario = new Questionario("", "", new ArrayList<Domanda>());
        return questionario;
    }

    public void modificaNome(Questionario questionario, String nome) {
        questionario.setNome(nome);
    }

    public void modificaDescrizione(Questionario questionario, String descrizione) {
        questionario.setDescrizione(descrizione);
    }

    public void aggiungiDomanda(Questionario questionario) {

        Domanda domanda = domandaService.creaDomanda();
        questionario.setNumeroDomande(questionario.getNumeroDomande() + 1);
        questionario.getElencoDomande().add(domanda);

    }

    public void rimuoviDomanda(Questionario questionario, Domanda domanda) {

        questionario.setNumeroDomande(questionario.getNumeroDomande() - 1);
        questionario.getElencoDomande().remove(domanda);
    }
}
