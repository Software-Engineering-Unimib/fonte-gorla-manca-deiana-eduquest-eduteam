package dev.eduteam.eduquest.controllers.web;

import dev.eduteam.eduquest.models.accounts.Account;
import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.models.questionari.*;
import dev.eduteam.eduquest.services.accounts.DocenteService;
import dev.eduteam.eduquest.services.accounts.StudenteService;
import dev.eduteam.eduquest.services.questionari.CompilazioneService;
import dev.eduteam.eduquest.services.questionari.CompitinoService;
import dev.eduteam.eduquest.services.questionari.EsercitazioneService;
import dev.eduteam.eduquest.services.questionari.QuestionarioService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("")
public class QuestionarioControllerWeb {

    @Autowired
    private QuestionarioService questionarioService;

    @Autowired
    private CompitinoService compitinoService;

    @Autowired
    private EsercitazioneService esercitazioneService;

    @Autowired
    private DocenteService docenteService;

    @Autowired
    private StudenteService studenteService;

    @Autowired
    private CompilazioneService compilazioneService;

    @GetMapping("docente/dashboard/{ID}")
    public String getQuestionario(
            HttpSession session,
            Model model,
            @PathVariable int ID) {

        Account user = (Account) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (!user.isDocente()) {
            return "redirect:/studente/dashboard/" + ID;
        }

        Docente docente = docenteService.getByID(user.getAccountID());

        Questionario questionario = questionarioService.getQuestionarioCompleto(ID);
        if (questionario != null) {

            if (!docenteService.proprietarioQuestionario(docente, questionario)) {
                return "redirect:/docente/redirect";
            }

            ArrayList<Domanda> domande = questionario.getElencoDomande();

            model.addAttribute("user", docente);
            model.addAttribute("questionario", questionario);
            model.addAttribute("domande", domande);
        }
        return "questionario/singolo-questionario";
    }

    @PostMapping("docente/dashboard/modifica/questionario/{questionarioID}")
    public String modificaQuestionario(@PathVariable int questionarioID,
            @RequestParam String nome,
            @RequestParam String descrizione,
            @RequestParam String difficolta,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Docente docente = (Docente) session.getAttribute("user");

        if (docente == null) {
            return "redirect:/login";
        }

        Questionario.Difficulty diff = Questionario.Difficulty.valueOf(difficolta);

        // Validazione: campi obbligatori non vuoti
        if (questionarioID == 0 || nome.isBlank() || descrizione.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Nome e descrizione sono obbligatori.");
            redirectAttributes.addFlashAttribute("nome", nome);
            redirectAttributes.addFlashAttribute("cognome", descrizione);
            return "redirect:/docente/profilo";
        }
        questionarioService.modificaInfoQuestionario(questionarioService.getQuestionarioCompleto(questionarioID), nome,
                descrizione, diff);

        return "redirect:/docente/dashboard/" + questionarioID;
    }

    @PostMapping("docente/dashboard/modifica/esercitazione/{questionarioID}")
    public String modificaEsercitazione(@PathVariable int questionarioID,
            @RequestParam String nome,
            @RequestParam String descrizione,
            @RequestParam String difficolta,
            @RequestParam String noteDidattiche,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Docente docente = (Docente) session.getAttribute("user");

        if (docente == null) {
            return "redirect:/login";
        }

        Questionario.Difficulty diff = Questionario.Difficulty.valueOf(difficolta);

        // Validazione: campi obbligatori non vuoti
        if (questionarioID == 0 || nome.isBlank() || descrizione.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Nome e descrizione sono obbligatori.");
            redirectAttributes.addFlashAttribute("nome", nome);
            redirectAttributes.addFlashAttribute("cognome", descrizione);
            return "redirect:/docente/profilo";
        }
        esercitazioneService.modificaInfoEsercitazione(
                (Esercitazione) questionarioService.getQuestionarioCompleto(questionarioID), nome, descrizione, diff,
                noteDidattiche);
        return "redirect:/docente/dashboard/" + questionarioID;
    }

    @PostMapping("docente/dashboard/modifica/compitino/{questionarioID}")
    public String modificaCompitino(@PathVariable int questionarioID,
            @RequestParam String nome,
            @RequestParam String descrizione,
            @RequestParam String difficolta,
            @RequestParam LocalDate dataFine,
            @RequestParam int tentativiMax,
            @RequestParam int puntiBonus,
            @RequestParam boolean assegnatiPtBonus,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Docente docente = (Docente) session.getAttribute("user");

        if (docente == null) {
            return "redirect:/login";
        }

        Questionario.Difficulty diff = Questionario.Difficulty.valueOf(difficolta);

        // Validazione: campi obbligatori non vuoti
        if (questionarioID == 0 || nome.isBlank() || descrizione.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Nome e descrizione sono obbligatori.");
            redirectAttributes.addFlashAttribute("nome", nome);
            redirectAttributes.addFlashAttribute("cognome", descrizione);
            return "redirect:/docente/profilo";
        }
        compitinoService.modificaInfoCompitino((Compitino) questionarioService.getQuestionarioCompleto(questionarioID),
                nome, descrizione, diff, dataFine, tentativiMax, puntiBonus, assegnatiPtBonus);
        return "redirect:/docente/dashboard/" + questionarioID;
    }

    @GetMapping("docente/dashboard/modifica/{ID}")
    public String modificaQuestionario(
            HttpSession session,
            Model model,
            @PathVariable int ID) {

        Account user = (Account) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (!user.isDocente()) {
            return "redirect:/studente/dashboard/" + ID;
        }

        Docente docente = docenteService.getByID(user.getAccountID());

        Questionario questionario = questionarioService.getQuestionarioCompleto(ID);
        if (questionario != null) {

            if (!docenteService.proprietarioQuestionario(docente, questionario)) {
                return "redirect:/docente/redirect";
            }

            model.addAttribute("questionario", questionario);
        }

        if (questionario instanceof Esercitazione) {

            return "questionario/modifica-esercitazione";
        } else if (questionario instanceof Compitino) {

            return "questionario/modifica-compitino";
        }
        return "questionario/modifica-questionario";
    }

    @PostMapping("docente/dashboard/crea")
    public String creaQuestionario(HttpSession session) {

        Docente docente = (Docente) session.getAttribute("user");

        if (docente == null) {
            return "redirect:/login";
        }
        questionarioService.creaQuestionario(docente.getAccountID(), Questionario.Difficulty.Facile);
        return "redirect:/docente/dashboard";
    }

    @PostMapping("docente/dashboard/creaEsercitazione")
    public String creaEsercitazione(HttpSession session) {

        Docente docente = (Docente) session.getAttribute("user");

        if (docente == null) {
            return "redirect:/login";
        }
        esercitazioneService.creaEsercitazione(docente.getAccountID(), Questionario.Difficulty.Facile, "");
        return "redirect:/docente/dashboard";
    }

    @PostMapping("docente/dashboard/creaCompitino")
    public String creaCompitino(HttpSession session) {

        Docente docente = (Docente) session.getAttribute("user");

        if (docente == null) {
            return "redirect:/login";
        }
        compitinoService.creaCompitino(docente.getAccountID(), Questionario.Difficulty.Facile, LocalDate.now(), 1);
        return "redirect:/docente/dashboard";
    }

    // Post -> Delete per standard REST
    @PostMapping("docente/dashboard/rimuovi/{ID}")
    public String rimuoviQuestionario(HttpSession session, @PathVariable int ID) {
        Docente docente = (Docente) session.getAttribute("user");

        if (docente == null) {
            return "redirect:/login";
        }
        questionarioService.rimuoviQuestionario(ID);
        return "redirect:/docente/dashboard";
    }

    // -- METODI STUDENTE --

    @GetMapping("studente/dashboard/{ID}")
    public String getQuestionarioStudente(
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
        if (questionario != null) {

            boolean rimangonoTentativi = compitinoService.isCompilabileByStudente(user.getAccountID(), questionario.getID());
            boolean esisteCompilazione = compilazioneService.esisteCompilazione(studente.getAccountID(), questionario.getID());

            model.addAttribute("user", studente);
            model.addAttribute("questionario", questionario);
            model.addAttribute("rimangonoTentativi", rimangonoTentativi);
            model.addAttribute("esisteCompilazione", esisteCompilazione);

        }
        return "questionario/singolo-questionario-studente";
    }

    @GetMapping("studente/dashboard/compilazioni")
    public String getCompilazioniInSospeso(
            HttpSession session,
            Model model) {

        Account user = (Account) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (user.isDocente()) {
            return "redirect:/docente/dashboard";
        }

        Studente studente = studenteService.getById(user.getAccountID());

        ArrayList<Compilazione> compilazioni = compilazioneService.getCompilazioniInSospeso(user.getAccountID());
        ArrayList<Questionario> questionari = new ArrayList<Questionario>();
        for (Compilazione c : compilazioni) {
            System.out.println("AAA");
            questionari.add(c.getQuestionario());
        }

        model.addAttribute("user", studente);
        model.addAttribute("compilazioni", compilazioni);
        model.addAttribute("questionari", questionari);

        return "questionario/compilazioni-sospese";
    }

}