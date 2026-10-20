package com.tahmid.support_ticket.controller;

import com.tahmid.support_ticket.entity.Ticket;
import com.tahmid.support_ticket.entity.TicketPriority;
import com.tahmid.support_ticket.service.TicketService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

@Controller
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /*
     * ---------------------------------------------------------
     * VIEW CUSTOMER TICKETS
     * ---------------------------------------------------------
     */
    @GetMapping("/tickets")
    public String myTickets(
            Authentication authentication,
            Model model) {

        String email = normalizeEmail(
                authentication.getName()
        );

        List<Ticket> tickets =
                ticketService.getMyTickets(email);

        model.addAttribute(
                "tickets",
                tickets
        );

        return "tickets";
    }

    /*
     * ---------------------------------------------------------
     * CREATE TICKET PAGE
     * ---------------------------------------------------------
     */
    @GetMapping("/tickets/new")
    public String createTicketPage() {
        return "create-ticket";
    }

    /*
     * ---------------------------------------------------------
     * CREATE NEW TICKET
     * ---------------------------------------------------------
     */
    @PostMapping("/tickets")
    public String createTicket(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(
                    value = "priority",
                    required = false
            ) TicketPriority priority,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {

            String email = normalizeEmail(
                    authentication.getName()
            );

            Ticket ticket =
                    ticketService.createTicket(
                            title,
                            description,
                            priority,
                            email
                    );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Ticket #"
                            + ticket.getId()
                            + " created successfully."
            );

            return "redirect:/tickets";

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );

            return "redirect:/tickets/new";

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Unable to create ticket. Please try again."
            );

            return "redirect:/tickets/new";
        }
    }

    /*
     * ---------------------------------------------------------
     * VIEW SINGLE TICKET
     * ---------------------------------------------------------
     */
    @GetMapping("/tickets/{id}")
    public String ticketDetails(
            @PathVariable("id") Long ticketId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            String loggedInEmail =
                    normalizeEmail(
                            authentication.getName()
                    );

            Ticket ticket =
                    ticketService.getTicketById(
                            ticketId
                    );

            String ownerEmail =
                    normalizeEmail(
                            ticket.getCustomer()
                                    .getEmail()
                    );

            /*
             * Customer can only view their own ticket.
             */
            if (!loggedInEmail.equals(ownerEmail)) {

                redirectAttributes.addFlashAttribute(
                        "error",
                        "You are not allowed to view this ticket."
                );

                return "redirect:/tickets";
            }

            model.addAttribute(
                    "ticket",
                    ticket
            );

            return "ticket-details";

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );

            return "redirect:/tickets";

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Unable to load ticket details."
            );

            return "redirect:/tickets";
        }
    }

    /*
     * ---------------------------------------------------------
     * CANCEL CUSTOMER TICKET
     * ---------------------------------------------------------
     */
    @PostMapping("/tickets/{id}/cancel")
    public String cancelTicket(
            @PathVariable("id") Long ticketId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {

            String email =
                    normalizeEmail(
                            authentication.getName()
                    );

            Ticket ticket =
                    ticketService.cancelTicket(
                            ticketId,
                            email
                    );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Ticket #"
                            + ticket.getId()
                            + " cancelled successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Unable to cancel ticket. Please try again."
            );
        }

        return "redirect:/tickets";
    }

    /*
     * ---------------------------------------------------------
     * EMAIL NORMALIZATION
     * ---------------------------------------------------------
     */
    private String normalizeEmail(String email) {

        if (email == null) {
            return "";
        }

        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}