package com.tahmid.support_ticket.controller;

import com.tahmid.support_ticket.entity.Ticket;
import com.tahmid.support_ticket.entity.TicketPriority;
import com.tahmid.support_ticket.entity.TicketStatus;
import com.tahmid.support_ticket.service.TicketService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminController {

    private final TicketService ticketService;

    public AdminController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /*
     * ---------------------------------------------------------
     * ADMIN TICKET LIST
     * ---------------------------------------------------------
     */
    @GetMapping("/admin/tickets")
    public String adminTickets(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            Model model) {

        List<Ticket> tickets =
                ticketService.searchTickets(
                        keyword,
                        status,
                        priority
                );

        model.addAttribute("tickets", tickets);

        model.addAttribute(
                "statuses",
                TicketStatus.values()
        );

        model.addAttribute(
                "priorities",
                TicketPriority.values()
        );

        model.addAttribute(
                "keyword",
                keyword == null ? "" : keyword
        );

        model.addAttribute(
                "selectedStatus",
                status
        );

        model.addAttribute(
                "selectedPriority",
                priority
        );

        return "admin-tickets";
    }

    /*
     * ---------------------------------------------------------
     * ADMIN TICKET DETAILS
     * ---------------------------------------------------------
     */
    @GetMapping("/admin/tickets/{id}")
    public String adminTicketDetails(
            @PathVariable("id") Long ticketId,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            Ticket ticket =
                    ticketService.getTicketById(ticketId);

            model.addAttribute(
                    "ticket",
                    ticket
            );

            model.addAttribute(
                    "statuses",
                    TicketStatus.values()
            );

            return "admin-ticket-details";

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );

            return "redirect:/admin/tickets";
        }
    }

    /*
     * ---------------------------------------------------------
     * UPDATE TICKET STATUS
     * ---------------------------------------------------------
     */
    @PostMapping("/admin/tickets/{id}/status")
    public String updateTicketStatus(
            @PathVariable("id") Long ticketId,
            @RequestParam("status") TicketStatus status,
            RedirectAttributes redirectAttributes) {

        try {

            Ticket updatedTicket =
                    ticketService.updateTicketStatus(
                            ticketId,
                            status
                    );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Ticket #"
                            + updatedTicket.getId()
                            + " status updated to "
                            + updatedTicket.getStatus()
                            + "."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Unable to update ticket status. Please try again."
            );
        }

        return "redirect:/admin/tickets/" + ticketId;
    }
}