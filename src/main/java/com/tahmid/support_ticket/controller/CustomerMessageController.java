package com.tahmid.support_ticket.controller;

import com.tahmid.support_ticket.entity.Ticket;
import com.tahmid.support_ticket.service.TicketService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CustomerMessageController {

    private final TicketService ticketService;

    public CustomerMessageController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/messages")
    public String customerMessages(
            Authentication authentication,
            Model model) {

        List<Ticket> tickets =
                ticketService.getMyTickets(
                        authentication.getName()
                );

        model.addAttribute("tickets", tickets);

        return "customer-messages";
    }
}