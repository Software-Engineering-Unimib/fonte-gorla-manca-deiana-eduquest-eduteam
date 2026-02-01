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
    private DomandaService domandaService;

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private QuestionarioRepository questionarioRepository;

    // Recupera un compitino completo di domande e dettagli extra
    public Compitino getCompitinoCompleto(int id) {
        Compitino c = compitinoRepository.getCompitinoByID(id);
        if (c != null) {
            c.setElencoDomande(domandaService.getDomandeComplete(id));
        }
        return c;
    }

    public Compitino creaCompitino(int docenteID, Difficulty diff, LocalDate dataFine, int tentativi) {
        Docente docente = docenteRepository.getDocenteByAccountID(docenteID);
        if (docente == null)
            return null;
        Compitino nuovo = new Compitino("Nuovo Compitino", "Descrizione", new ArrayList<>(),
                docente, diff, dataFine, tentativi);

        Questionario qBase = questionarioRepository.insertQuestionario(nuovo);

        if (qBase != null && qBase.getID() > 0) {

            boolean successo = compitinoRepository.insertDettagliCompitino(nuovo, qBase.getID());
            if (successo) {
                nuovo.setID(qBase.getID());
                return nuovo;
            }
        }
        return null;
    }

    public boolean isCompilabileByStudente(int studenteID, int compitinoID) {
        Compitino c = compitinoRepository.getCompitinoByID(compitinoID);
        if (c == null)
            return false;

        if (LocalDate.now().isAfter(c.getDataFine())) {
            return false;
        }

        int tentativiFatti = compitinoRepository.countTentativi(studenteID, compitinoID);
        if (tentativiFatti >= c.getTentativiMax()) {
            return false;
        }

        return true;

    }
}