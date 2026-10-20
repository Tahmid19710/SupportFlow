package com.tahmid.support_ticket.controller;

import com.tahmid.support_ticket.dto.RegistrationForm;
import com.tahmid.support_ticket.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registrationForm") RegistrationForm form,
            BindingResult result) {

        if (result.hasErrors()) {
            return "register";
        }

        if (form.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            result.rejectValue(
                    "password",
                    "password.tooLong",
                    "Password is too long. Please use a shorter password."
            );
            return "register";
        }

        try {
            userService.registerCustomer(
                    form.getName(),
                    form.getEmail(),
                    form.getPassword()
            );
        } catch (IllegalArgumentException ex) {
            result.rejectValue("email", "email.duplicate", ex.getMessage());
            return "register";
        }

        return "redirect:/login";
    }
}