package com.tahmid.support_ticket.controller;

import com.tahmid.support_ticket.entity.Ticket;
import com.tahmid.support_ticket.repository.TicketReplyRepository;
import com.tahmid.support_ticket.repository.TicketRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminTicketDeleteController {

    private final TicketRepository ticketRepository;
    private final TicketReplyRepository ticketReplyRepository;

    public AdminTicketDeleteController(
            TicketRepository ticketRepository,
            TicketReplyRepository ticketReplyRepository) {

        this.ticketRepository = ticketRepository;
        this.ticketReplyRepository = ticketReplyRepository;
    }

    @Transactional
    @PostMapping("/admin/tickets/{id}/delete")
    public String deleteTicket(
            @PathVariable("id") Long ticketId,
            RedirectAttributes redirectAttributes) {

        try {

            Ticket ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Ticket not found: " + ticketId
                            )
                    );

            /*
             * Important:
             * Replies reference the ticket through a foreign key.
             * So replies must be deleted first.
             */
            ticketReplyRepository.deleteByTicket_Id(ticketId);

            /*
             * Flush reply deletion before deleting the parent ticket.
             */
            ticketReplyRepository.flush();

            ticketRepository.delete(ticket);

            ticketRepository.flush();

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Ticket #" + ticketId + " deleted successfully."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Unable to delete ticket #" + ticketId
                            + ". Please try again."
            );
        }

        return "redirect:/admin/tickets";
    }
}