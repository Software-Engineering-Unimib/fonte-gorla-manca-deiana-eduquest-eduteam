package dev.eduteam.eduquest.controllers.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dev.eduteam.eduquest.models.accounts.Account;
import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.services.questionari.QuestionarioService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/docente")
public class DocenteWebController {

    @Autowired
    private QuestionarioService questionarioService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/login";
        }
        
        if (!user.isDocente()) {
            return "redirect:/studente/dashboard";
        }

        Docente docente = (Docente) user;
        model.addAttribute("user", docente);
        
        // Carica i questionari del docente
        try {
            List<Questionario> questionari = questionarioService.getQuestionariByDocente(docente.getAccountID());
            model.addAttribute("questionari", questionari);
        } catch (Exception e) {
            // Se non ci sono questionari, la lista sarà null
            model.addAttribute("questionari", null);
        }

        return "docente/dashboard";
    }
}
