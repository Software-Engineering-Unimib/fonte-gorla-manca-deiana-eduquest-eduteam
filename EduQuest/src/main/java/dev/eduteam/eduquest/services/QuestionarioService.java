package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Questionario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class QuestionarioService {

    @Autowired
    private DomandaService domandaService;

    private ArrayList<Questionario> questionari = new ArrayList<Questionario>() {
        {
            add(creaQuestionario("Questionario Dinosauri", "Domande su dinosauri", 5));
            add(creaQuestionario("Questionario Scienze", "Domande su scienze", 2));
            add(creaQuestionario("Questionario Matematica", "Domande su matematica", 3));
        }
    };

    public ArrayList<Questionario> getQuestionari() {
        return questionari;
    }

    public Questionario creaQuestionario(String nome, String descrizione, int numeroDomande) {

        int ID = (int) (Math.random() * 100000); // TEMPORARIO

        Questionario questionario = new Questionario(nome, descrizione, creaDomande(numeroDomande));
        questionario.setID(ID); // TEMPORARIO

        return questionario;
    }

    private ArrayList<Domanda> creaDomande(int numeroDomande) {

        ArrayList<Domanda> tempDomande = new ArrayList<Domanda>();

        for (int i = 0; i < numeroDomande; i++) {

            Domanda domanda = new Domanda(""); // Testo vuoto temporaneo sostitutivo allo 0
            tempDomande.add(domanda);
        }

        return tempDomande;
    }

    public void modificaDescrizione(Questionario questionario, String descrizione) {
        questionario.setDescrizione(descrizione);
    }

    public void aggiungiDomanda(Questionario questionario, String testo, int numeroRisposte) {

        Domanda domanda = domandaService.creaDomanda(testo, numeroRisposte);
        questionario.setNumeroDomande(questionario.getNumeroDomande() + 1);
        questionario.getElencoDomande().add(domanda);

    }

    public void rimuoviDomanda(Questionario questionario, Domanda domanda) {

        questionario.setNumeroDomande(questionario.getNumeroDomande() - 1);
        questionario.getElencoDomande().remove(domanda);
    }
}
