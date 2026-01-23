package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.Domanda;
import dev.eduteam.eduquest.models.Risposta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomandaService {

    @Autowired
    private RispostaService rispostaService;

    public Domanda creaDomanda() {

        int ID = (int) (Math.random() * 100000); // TEMPORARIO

        Domanda domanda = new Domanda("");

        domanda.setID(ID); // TEMPORARIO

        return domanda;
    }

    public Risposta getRisposta(Domanda domanda, int ID) {
        return domanda.getElencoRisposte().stream().filter(r -> r.getID() == ID).findFirst().orElse(null);
    }

    public void modificaTesto(Domanda domanda, String testo) {
        domanda.setTesto(testo);
    }

    public void aggiungiRisposta(Domanda domanda) {

        Risposta risposta = rispostaService.creaRisposta();
        domanda.setNumeroRisposte(domanda.getNumeroRisposte() + 1);
        domanda.getElencoRisposte().add(risposta);
    }

    public void rimuoviRisposta(Domanda domanda, Risposta risposta) {

        domanda.setNumeroRisposte(domanda.getNumeroRisposte() - 1);
        domanda.getElencoRisposte().remove(risposta);

    }

    public void setRispostaCorretta(Domanda domanda, Risposta risposta) {
        if (domanda.getElencoRisposte().contains(risposta)) {
            domanda.setRispostaCorretta(risposta);
        }
    }
}
