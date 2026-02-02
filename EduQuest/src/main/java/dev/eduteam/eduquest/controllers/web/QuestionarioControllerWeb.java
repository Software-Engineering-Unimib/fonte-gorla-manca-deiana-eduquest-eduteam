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

import java.util.ArrayList;

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
            ArrayList<Domanda> domande = questionario.getElencoDomande();

            model.addAttribute("user", docente);
            model.addAttribute("questionario", questionario);
            model.addAttribute("domande", domande);
            System.out.println(domande.isEmpty());
        }
        return "singolo-questionario";
    }

    @PostMapping("crea")
    public ResponseEntity<Questionario> creaQuestionario(@RequestParam int docenteID) {

        Questionario questionarioCreato = questionarioService.creaQuestionario(docenteID);

        if (questionarioCreato != null) {
            return ResponseEntity.status(201).body(questionarioCreato);
        } else {
            return ResponseEntity.internalServerError().build();
        }
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

    // Post -> Put per standard REST
    @PutMapping("modifica/{ID}/rinomina")
    public ResponseEntity<Questionario> rinominaQuestionario(
            @PathVariable int docenteID,
            @PathVariable int ID,
            @RequestParam(name = "nome") String nome) {

        // Validazione del nome - non nullo o vuoto
        if (nome == null || nome.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Questionario q = questionarioService.getQuestionarioCompleto(ID);
        if (q == null) {
            return ResponseEntity.notFound().build();
        }
        boolean result = questionarioService.modificaInfo(q, nome, q.getDescrizione());
        if (result) {
            return ResponseEntity.ok(q);
        } else {
            return ResponseEntity.internalServerError().build();
        }

    }

    @PostMapping("modifica/{ID}/descrizione")
    public ResponseEntity<Questionario> setDescrizoneQuestionario(
            @PathVariable int docenteID,
            @PathVariable int ID,
            @RequestParam(name = "descrizione") String descrizione) {

        // Validazione della descrizione - non nulla o vuota
        if (descrizione == null || descrizione.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Questionario q = questionarioService.getQuestionarioCompleto(ID);
        if (q == null) {
            return ResponseEntity.notFound().build();
        }
        boolean result = questionarioService.modificaInfo(q, q.getNome(), descrizione);
        if (result) {
            return ResponseEntity.ok(q);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

}