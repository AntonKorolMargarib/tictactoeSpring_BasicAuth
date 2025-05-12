package com.margarib.tictactoe_spring.web.controller;

import com.margarib.tictactoe_spring.domain.service.AuthService;
import com.margarib.tictactoe_spring.web.model.SignUpRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/")
    public String showLoginForm(@ModelAttribute SignUpRequest signUpRequest, Model model,
                                @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username and/or password");
        }

        if (logout != null) {
            model.addAttribute("message", "You have been successfully logged out.");
        }
        return "startPage";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("signUpRequest", new SignUpRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("signUpRequest") SignUpRequest signUpRequest, RedirectAttributes redirectAttributes) {
        boolean isRegistered = authService.register(signUpRequest);
        if (isRegistered) {
            redirectAttributes.addFlashAttribute("message", "Registration successful");
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed: Login already exists.");
            return "redirect:/register";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/?logout=true";
    }

}
