package dev.eduteam.eduquest.controllers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.eduteam.eduquest.models.accounts.Account;
import dev.eduteam.eduquest.services.accounts.AccountService;
import jakarta.servlet.http.HttpSession;

/**
 * Controller per autenticazione e pagine pubbliche.
 * Gestisce: home, login, register, logout e routing dashboard.
 */
@Controller
public class AuthWebController {

    @Autowired
    private AccountService accountService;

    // ==================== PAGINE PUBBLICHE ====================

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // Se già loggato, redirect alla dashboard
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "account/login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String userName,
                          @RequestParam String password,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        try {
            Account account = accountService.logIn(userName, password);
            session.setAttribute("user", account);
            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        // Se già loggato, redirect alla dashboard
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "account/register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String nome,
                             @RequestParam String cognome,
                             @RequestParam String userName,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String confermaPassword,
                             @RequestParam boolean isDocente,
                             RedirectAttributes redirectAttributes) {
        // Validazione conferma password
        if (!password.equals(confermaPassword)) {
            redirectAttributes.addFlashAttribute("error", "Le password non corrispondono.");
            redirectAttributes.addFlashAttribute("nome", nome);
            redirectAttributes.addFlashAttribute("cognome", cognome);
            redirectAttributes.addFlashAttribute("userName", userName);
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/register";
        }

        try {
            accountService.registraAccount(nome, cognome, userName, email, password, isDocente);
            redirectAttributes.addFlashAttribute("success", "Registrazione completata! Effettua il login.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("nome", nome);
            redirectAttributes.addFlashAttribute("cognome", cognome);
            redirectAttributes.addFlashAttribute("userName", userName);
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Logout effettuato con successo.");
        return "redirect:/login";
    }

    // ==================== DASHBOARD ROUTING ====================

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/login";
        }

        if (user.isDocente()) {
            return "redirect:/docente/dashboard";
        } else {
            return "redirect:/studente/dashboard";
        }
    }
}
