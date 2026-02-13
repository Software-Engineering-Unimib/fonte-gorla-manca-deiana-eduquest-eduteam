package dev.eduteam.eduquest.controllers.web;

import dev.eduteam.eduquest.models.accounts.Account;
import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.models.questionari.Compilazione;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.services.accounts.StudenteService;
import dev.eduteam.eduquest.services.questionari.CompilazioneService;
import dev.eduteam.eduquest.services.questionari.QuestionarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

@Controller
@RequestMapping("studente/compila/")
public class CompilazioneWebController {

    @Autowired
    private QuestionarioService questionarioService;

    @Autowired
    private CompilazioneService compilazioneService;

    @Autowired
    private StudenteService studenteService;

    @GetMapping("{ID}")
    public String getQuestionario(
            HttpSession session,
            Model model,
            @PathVariable int ID) {

        Account user = (Account) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (user.isDocente()) {
            return "redirect:/docente/dashboard/" + ID;
        }

        Studente studente = studenteService.getById(user.getAccountID());
        Questionario questionario = questionarioService.getQuestionarioCompleto(ID);
        Compilazione compilazione = compilazioneService.riprendiCompilazione(studente.getAccountID(), ID);
        Risposta[] risposteComp = compilazione.getRisposte();

        if (questionario != null) {

            model.addAttribute("compilazione", compilazione);
            model.addAttribute("user", studente);
            model.addAttribute("studenteID", studente.getAccountID());
            model.addAttribute("questionario", questionario);

            for (int i = 0; i < risposteComp.length; i++) {
                if (risposteComp[i] == null) {
                    Domanda domanda = questionario.getElencoDomande().get(i);
                    ArrayList<Risposta> risposte = domanda.getElencoRisposte();

                    model.addAttribute("domanda", domanda);
                    model.addAttribute("index", questionario.getElencoDomande().indexOf(domanda));
                    model.addAttribute("risposte", risposte);

                    return "domanda/singola-domanda-studente";
                }
            }
        }
        return "questionario/conferma-compilazione";
    }

    @PostMapping("{ID}")
    public String creaCompilazione(@PathVariable int ID,
            HttpSession session) {
        Studente studente = (Studente) session.getAttribute("user");

        if (studente == null) {
            return "redirect:/login";
        }

        compilazioneService.creaCompilazione(studente.getAccountID(), ID);

        return "redirect:/studente/compila/" + ID;
    }

    @PostMapping("rispondi/{ID}")
    public String rispondiDomanda(@PathVariable int ID,
            @RequestParam int compilazioneID,
            @RequestParam int domandaID,
            @RequestParam int rispostaID,
            HttpSession session) {

        compilazioneService.inserisciRispostaComp(compilazioneID, domandaID, rispostaID);

        return "redirect:/studente/compila/" + ID;
    }

    @PostMapping("{ID}/conferma")
    public String confermaCompilazione(@PathVariable int ID,
            @RequestParam int compilazioneID,
            @RequestParam int studenteID) {

        compilazioneService.chiudiCompilazione(studenteID, compilazioneID);

        return "redirect:/studente/compila/" + ID + "/riepilogo/" + compilazioneID;
    }

    @GetMapping("{ID}/riepilogo/{compilazioneID}")
    public String getRiepilogo(
            HttpSession session,
            Model model,
            @PathVariable int ID,
            @PathVariable int compilazioneID) {

        Account user = (Account) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (user.isDocente()) {
            return "redirect:/docente/dashboard/" + ID;
        }

        Studente studente = studenteService.getById(user.getAccountID());
        Questionario questionario = questionarioService.getQuestionarioCompleto(ID);
        Compilazione compilazione = compilazioneService.getCompilazioneByID(compilazioneID);
        Risposta[] risposteComp = compilazione.getRisposte();

        if (questionario != null) {

            int eduPointsAssegnati = compilazioneService.calcolaEduPoints(studente, compilazione.getPunteggio(), questionario.getPunteggioMax());
            ArrayList<Domanda> domande = questionario.getElencoDomande();
            model.addAttribute("compilazione", compilazione);
            model.addAttribute("questionario", questionario);
            model.addAttribute("risposte", risposteComp);
            model.addAttribute("domande", domande);
            model.addAttribute("eduPoints", eduPointsAssegnati);
        }
        return "questionario/riepilogo-questionario";
    }
}
