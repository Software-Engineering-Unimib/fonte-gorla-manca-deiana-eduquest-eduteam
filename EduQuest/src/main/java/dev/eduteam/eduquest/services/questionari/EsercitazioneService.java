package dev.eduteam.eduquest.services.questionari;

import java.util.ArrayList;

import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.repositories.questionari.QuestionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Esercitazione;
import dev.eduteam.eduquest.models.questionari.Feedback;
import dev.eduteam.eduquest.models.questionari.Questionario.Difficulty;
import dev.eduteam.eduquest.repositories.accounts.DocenteRepository;
import dev.eduteam.eduquest.repositories.questionari.DomandaRepository;
import dev.eduteam.eduquest.repositories.questionari.EsercitazioneRepository;
import dev.eduteam.eduquest.repositories.questionari.FeedbackRepository;

@Service
public class EsercitazioneService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private DomandaRepository domandaRepository;

    @Autowired
    private QuestionarioRepository questionarioRepository;

    @Autowired
    private EsercitazioneRepository esercitazioneRepository;

    @Autowired
    private DocenteRepository docenteRepository;

    public Domanda aggiungiDomandaConFeedback(int questionarioID, Domanda.Type tipoDomanda, String feedbackTesto) {
        Domanda nuovaDomanda = Domanda.createDomandaOfType(tipoDomanda);
        nuovaDomanda.setTesto("Inserisci qui il testo");

        Domanda domandaSalvata = domandaRepository.insertDomanda(nuovaDomanda, questionarioID);

        if (domandaSalvata != null && feedbackTesto != null && !feedbackTesto.trim().isEmpty()) {
            Feedback f = new Feedback(domandaSalvata, feedbackTesto);
            feedbackRepository.insertFeedback(f);
        }
        return domandaSalvata;
    }

    public Esercitazione creaEsercitazione(int docenteID, Difficulty difficolta, String noteDidattiche) {
        Docente docente = docenteRepository.getDocenteByAccountID(docenteID);
        if (docente == null) {
            return null;
        }
        Esercitazione nuova = new Esercitazione("Nuova Esercitazione", "Descrizione", new ArrayList<>(), docente,
                difficolta);
        nuova.setNoteDidattiche(noteDidattiche);

        return esercitazioneRepository.insertEsercitazione(nuova);
    }

    // Uniti i due metodi di modifica e sistemata logica in controller
    public boolean modificaInfoEsercitazione(Esercitazione questionario, String nome, String descrizione, Difficulty livelloDiff, String noteDidattiche) {
        questionario.setNome(nome);
        questionario.setDescrizione(descrizione);
        questionario.setLivelloDiff(livelloDiff);
        questionarioRepository.updateQuestionario(questionario);
        questionario.setNoteDidattiche(noteDidattiche);
        return esercitazioneRepository.updateNoteEsercitazione(questionario);
    }
}
