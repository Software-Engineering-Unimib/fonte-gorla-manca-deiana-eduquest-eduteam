package dev.eduteam.eduquest.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dev.eduteam.eduquest.models.accounts.Account;
import dev.eduteam.eduquest.models.accounts.Studente;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/studente")
public class StudenteWebController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/login";
        }
        
        if (user.isDocente()) {
            return "redirect:/docente/dashboard";
        }

        Studente studente = (Studente) user;
        model.addAttribute("user", studente);
        
        // Placeholder per statistiche e questionari disponibili
        model.addAttribute("questionariCompletati", 0);
        model.addAttribute("questionariDisponibili", null);

        return "studente/dashboard";
    }
}
