package dev.eduteam.eduquest.services.questionari;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.questionari.*;
import dev.eduteam.eduquest.models.questionari.Questionario.Difficulty;
import dev.eduteam.eduquest.repositories.accounts.DocenteRepository;
import dev.eduteam.eduquest.repositories.questionari.QuestionarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionarioService {

    @Autowired
    private CompitinoService compitinoService;

    @Autowired
    private QuestionarioRepository questionarioRepository;

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private DomandaService domandaService;

    // Restituisce una lista mista di Questionario e Compitini
    public ArrayList<Questionario> getQuestionari() {
        ArrayList<Questionario> questionari = questionarioRepository.getQuestionari();
        for (Questionario q : questionari) {
            ArrayList<Domanda> domande = domandaService.getDomandeComplete(q.getID());
            q.setElencoDomande(domande);
        }
        return questionari;
    }

    public ArrayList<Questionario> getQuestionariByDocente(int docenteID) {
        ArrayList<Questionario> questionari = questionarioRepository.getQuestionariByDocente(docenteID);
        for (Questionario q : questionari) {
            ArrayList<Domanda> domande = domandaService.getDomandeComplete(q.getID());
            q.setElencoDomande(domande);
        }
        return questionari;
    }

    public List<Questionario> getQuestionariDisponibliPerStudente(int studenteID) {
        List<Questionario> tutti = getQuestionari();

        return tutti.stream().filter(q -> {
            if (q instanceof Compitino) {
                return compitinoService.isCompilabileByStudente(studenteID, q.getID());
            }
            return true;
        }).collect(Collectors.toList());
    }

    public Questionario getQuestionarioCompleto(int ID) {
        Questionario questionario = questionarioRepository.getQuestionarioByID(ID);

        if (questionario != null) {
            ArrayList<Domanda> domande = domandaService.getDomandeComplete(ID);
            questionario.setElencoDomande(domande);
        }
        return questionario;
    }

    public Questionario creaQuestionario(int docenteID, Difficulty livelloDiff) {
        Docente d = docenteRepository.getDocenteByAccountID(docenteID);
        if (d == null)
            return null;
        Questionario nuovo = new Questionario("Nuovo Questionario", "Nuova Descrizione",
                new ArrayList<Domanda>(), d, livelloDiff);
        return questionarioRepository.insertQuestionario(nuovo);
    }

    public boolean rimuoviQuestionario(int ID) {
        return questionarioRepository.removeQuestionario(ID);
    }

    public boolean modificaInfoQuestionario(Questionario questionario, String nome, String descrizione,
            Difficulty livelloDiff) {
        if (descrizione == null) {
            throw new IllegalArgumentException("La descrizione non può essere null");
        }
        questionario.setNome(nome);
        questionario.setDescrizione(descrizione);
        questionario.setLivelloDiff(livelloDiff);
        return questionarioRepository.updateQuestionario(questionario);
    }

    public Domanda getDomandaSuccessiva(int questionarioID, int domandaID) {
        Questionario q = getQuestionarioCompleto(questionarioID);
        if (q == null || q.getElencoDomande() == null)
            return null;

        ArrayList<Domanda> elenco = q.getElencoDomande();
        for (int i = 0; i < elenco.size(); i++) {
            if (elenco.get(i).getID() == domandaID) {
                if (i + 1 < elenco.size()) {
                    return elenco.get(i + 1);
                }
                return null;
            }
        }
        return null;
    }

    public Domanda getDomandaInSospeso(int questionarioID, Risposta[] risposte) {
        int index = 0;
        Questionario q = getQuestionarioCompleto(questionarioID);
        if (q == null || q.getElencoDomande() == null)
            return null;

        // Deve trovare la prossima domanda a cui non ho ancora dato risposta e tornarla
        while (index < risposte.length && index < q.getNumeroDomande() && risposte[index] != null) {
            index++;
        }
        // Escludiamo il caso in cui non ho domanda da compilare
        if (index < q.getNumeroDomande()) {
            return q.getElencoDomande().get(index);
        }
        return null;
    }

    public List<Questionario> cercaQuestionari(String keyword) {
        return questionarioRepository.searchQuestionari(keyword);
    }

}
