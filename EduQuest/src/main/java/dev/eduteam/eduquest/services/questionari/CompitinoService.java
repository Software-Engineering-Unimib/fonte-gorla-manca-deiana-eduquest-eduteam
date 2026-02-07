package dev.eduteam.eduquest.services.questionari;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.questionari.Compitino;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.questionari.Questionario.Difficulty;
import dev.eduteam.eduquest.repositories.accounts.DocenteRepository;
import dev.eduteam.eduquest.repositories.questionari.CompitinoRepository;
import dev.eduteam.eduquest.repositories.questionari.QuestionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class CompitinoService {

    @Autowired
    private CompitinoRepository compitinoRepository;

    @Autowired
    private QuestionarioRepository questionarioRepository;

    @Autowired
    private DomandaService domandaService;

    @Autowired
    private DocenteRepository docenteRepository;

    // Recupera un compitino completo di domande e dettagli extra
    public Compitino getCompitinoCompleto(int id) {
        Questionario q = compitinoRepository.getQuestionarioByID(id);
        if (q instanceof Compitino) {
            Compitino c = (Compitino) q;
            c.setElencoDomande(domandaService.getDomandeComplete(id));
            return c;
        }
        return null;
    }

    public Compitino creaCompitino(int docenteID, Difficulty diff, LocalDate dataFine, int tentativi) {
        Docente docente = docenteRepository.getDocenteByAccountID(docenteID);
        if (docente == null)
            return null;
        Compitino nuovo = new Compitino("Nuovo Compitino", "Descrizione", new ArrayList<>(),
                docente, diff, dataFine, tentativi);
        // CompitinoRepository gestisce internamente la doppia insert
        return compitinoRepository.insertCompitino(nuovo);
    }

    public boolean isCompilabileByStudente(int studenteID, int compitinoID) {
        Questionario q = compitinoRepository.getQuestionarioByID(compitinoID);
        if (q == null)
            return false;
        if (!(q instanceof Compitino))
            return true; // Se non è un compitino posso sempre compilarlo
        Compitino c = (Compitino) q;

        // Controllo scadenza
        if (LocalDate.now().isAfter(c.getDataFine())) {
            return false;
        }

        // Controllo tentativi
        int tentativiFatti = compitinoRepository.countTentativi(studenteID, compitinoID);
        return tentativiFatti < c.getTentativiMax();
    }

    // Uniti i due metodi di modifica e sistemata logica in controller
    public boolean modificaInfoCompitino(Compitino questionario, String nome, String descrizione, Difficulty livelloDiff, LocalDate dataFine, int tentativiMax) {
        questionario.setNome(nome);
        questionario.setDescrizione(descrizione);
        questionario.setLivelloDiff(livelloDiff);
        questionarioRepository.updateQuestionario(questionario);
        questionario.setDataFine(dataFine);
        questionario.setTentativiMax(tentativiMax);
        return compitinoRepository.updateCompitino(questionario);
    }
}