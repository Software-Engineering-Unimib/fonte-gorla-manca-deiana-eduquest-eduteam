package dev.eduteam.eduquest.controllers.web;

import dev.eduteam.eduquest.models.accounts.Account;
import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.services.accounts.DocenteService;
import dev.eduteam.eduquest.services.questionari.QuestionarioService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("docente/dashboard/")
public class QuestionarioControllerWeb {

    @Autowired
    private QuestionarioService questionarioService;

    @Autowired
    private DocenteService docenteService;

    @GetMapping("/questionari")
    public String getQuestionariByDocente(
            Model model,
            @PathVariable int docenteID) {

        ArrayList<Questionario> questionari = questionarioService.getQuestionariByDocente(docenteID);
        Docente docente = docenteService.getByID(docenteID);

        model.addAttribute("docente", docente);
        model.addAttribute("questionari", questionari);

        return "lista-questionari";
    }

    @GetMapping("{ID}")
    public String getQuestionario(
            HttpSession session,
            Model model,
            @PathVariable int ID) {

        Account user = (Account) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (!user.isDocente()) {
            return "redirect:/studente/dashboard"; // TEMPORANEO, DEVE RIMANDARE ALLA PAGINA DOVE LO STUDENTE PUO' COMPILARE IL QUESTIONARIO
        }

        Docente docente = (Docente) user;
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

    @PostMapping("/modifica/{questionarioID}")
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
        questionarioService.modificaInfo(questionarioService.getQuestionarioCompleto(questionarioID), nome, descrizione, diff);

        return "redirect:/docente/dashboard/" + questionarioID;
    }

    @GetMapping("/modifica/{ID}")
    public String modificaQuestionario(
            HttpSession session,
            Model model,
            @PathVariable int ID) {

        Account user = (Account) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (!user.isDocente()) {
            return "redirect:/studente/dashboard"; // TEMPORANEO, DEVE RIMANDARE ALLA PAGINA DOVE LO STUDENTE PUO' COMPILARE IL QUESTIONARIO
        }

        Docente docente = (Docente) user;
        Questionario questionario = questionarioService.getQuestionarioCompleto(ID);
        if (questionario != null) {

            if (!docenteService.proprietarioQuestionario(docente, questionario)) {
                return "redirect:/docente/redirect";
            }

            model.addAttribute("questionario", questionario);
        }
        return "questionario/modifica-questionario";
    }

    @PostMapping("crea")
    public String creaQuestionario(HttpSession session) {

        System.out.println("A");
        Docente docente = (Docente) session.getAttribute("user");

        if (docente == null) {
            return "redirect:/login";
        }

        questionarioService.creaQuestionario(docente.getAccountID(), Questionario.Difficulty.Facile);

        return "redirect:/docente/dashboard";
    }

    // Post -> Delete per standard REST
    @DeleteMapping("rimuovi/{ID}")
    public ResponseEntity<Questionario> rimuoviQuestionario(@PathVariable int ID) {
        boolean result = questionarioService.rimuoviQuestionario(ID);
        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}