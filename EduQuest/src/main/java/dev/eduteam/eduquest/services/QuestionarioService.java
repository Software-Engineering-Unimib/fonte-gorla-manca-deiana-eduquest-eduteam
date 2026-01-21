package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Questionario;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class QuestionarioService {

    private ArrayList<Questionario> questionari = new ArrayList<Questionario>() {
        {
            add(creaQuestionario("Questionario Dinosauri", 5));
            add(creaQuestionario("Questionario Scienze", 2));
            add(creaQuestionario("Questionario Matematica", 3));
        }
    };

    public ArrayList<Questionario> getQuestionari() {
        return questionari;
    }

    public Questionario creaQuestionario(String nome, int numeroDomande) {

        Questionario questionario = new Questionario(nome, creaDomande(numeroDomande));
        return questionario;
    }

    private ArrayList<Domanda> creaDomande(int numeroDomande) {

        ArrayList<Domanda> tempDomande = new ArrayList<Domanda>();

        for (int i = 0; i < numeroDomande; i++) {

            Domanda domanda = new Domanda(0);
            tempDomande.add(domanda);
        }

        return tempDomande;
    }

    public void modificaDescrizione(Questionario questionario, String descrizione) {
        questionario.setDescrizione(descrizione);
    }

    public void aggiungiDomanda(Questionario questionario, int numeroRisposte) {

        Domanda domanda = new Domanda(numeroRisposte);
        questionario.setNumeroDomande(questionario.getNumeroDomande() + 1);
        questionario.getDomande().add(domanda);

    }

    public void rimuoviDomanda(Questionario questionario,  Domanda domanda) {

        questionario.setNumeroDomande(questionario.getNumeroDomande() - 1);
        questionario.getDomande().remove(domanda);
    }
}
