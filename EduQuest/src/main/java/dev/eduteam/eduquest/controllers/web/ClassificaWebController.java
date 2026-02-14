package dev.eduteam.eduquest.controllers.web;

import dev.eduteam.eduquest.models.accounts.Account;
import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.models.questionari.Compilazione;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.services.ClassificaService;
import dev.eduteam.eduquest.services.questionari.QuestionarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/classifiche")
public class ClassificaWebController {

    @Autowired
    private ClassificaService classificaService;

    @Autowired
    private QuestionarioService questionarioService;

    private static final int LIMITE_DEFAULT = 10;

    @GetMapping("")
    public String paginaClassifiche(HttpSession session, Model model,
                                     @RequestParam(defaultValue = "edupoints") String tipo) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        switch (tipo) {
            case "media":
                List<Studente> classificaMedia = classificaService.getClassificaMedia(LIMITE_DEFAULT);
                model.addAttribute("classifica", classificaMedia);
                model.addAttribute("tipoClassifica", "Media Punteggio");
                break;
            case "compilazioni":
                List<Compilazione> classificaComp = classificaService.getClassificaCompilazioniGlobale(LIMITE_DEFAULT);
                model.addAttribute("classificaCompilazioni", classificaComp);
                model.addAttribute("tipoClassifica", "Migliori Compilazioni");
                break;
            case "edupoints":
            default:
                List<Studente> classificaEP = classificaService.getClassificaEduPoints(LIMITE_DEFAULT);
                model.addAttribute("classifica", classificaEP);
                model.addAttribute("tipoClassifica", "EduPoints");
                break;
        }

        model.addAttribute("user", user);
        model.addAttribute("tipoSelezionato", tipo);
        return "classifica/classifiche";
    }

    @GetMapping("/questionario/{id}")
    public String classificaQuestionario(@PathVariable int id,
                                          HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Questionario questionario = questionarioService.getQuestionarioCompleto(id);
        List<Compilazione> classifica = classificaService.getClassificaPerQuestionario(id, LIMITE_DEFAULT);
        model.addAttribute("classificaCompilazioni", classifica);
        model.addAttribute("questionario", questionario);
        model.addAttribute("user", user);
        model.addAttribute("tipoClassifica", "Classifica Questionario");
        return "classifica/classifica-questionario";
    }

    @GetMapping("/questionari")
    public String ricercaQuestionari(HttpSession session, Model model,
                                     @RequestParam(required = false) String cerca) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Questionario> questionari;
        if (cerca != null && !cerca.trim().isEmpty()) {
            questionari = questionarioService.cercaQuestionari(cerca.trim());
        } else {
            questionari = questionarioService.getQuestionari();
        }

        model.addAttribute("questionari", questionari);
        model.addAttribute("cerca", cerca);
        model.addAttribute("user", user);
        return "classifica/ricerca-questionari";
    }
}
