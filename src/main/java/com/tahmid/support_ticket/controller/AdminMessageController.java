package com.tahmid.support_ticket.controller;

import com.tahmid.support_ticket.entity.Ticket;
import com.tahmid.support_ticket.service.TicketService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminMessageController {

    private final TicketService ticketService;

    public AdminMessageController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/admin/messages")
    public String adminMessages(Model model) {

        // Get all tickets
        List<Ticket> tickets = ticketService.searchTickets(
                "",
                null,
                null
        );

        model.addAttribute("tickets", tickets);

        return "admin-messages";
    }
}