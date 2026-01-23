package dev.eduteam.eduquest.services;

import dev.eduteam.eduquest.models.Risposta;
import org.springframework.stereotype.Service;

@Service
public class RispostaService {

    public Risposta creaRisposta() {

        int ID = (int) (Math.random() * 100000); // TEMPORARIO

        Risposta risposta = new Risposta("");

        risposta.setID(ID); // TEMPORARIO

        return risposta;
    }

    public void modificaTesto(Risposta risposta, String testo) { risposta.setTesto(testo); }
}
