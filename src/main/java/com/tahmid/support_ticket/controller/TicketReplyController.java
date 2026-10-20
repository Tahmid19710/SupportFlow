
package com.tahmid.support_ticket.controller;

import com.tahmid.support_ticket.entity.Role;
import com.tahmid.support_ticket.entity.Ticket;
import com.tahmid.support_ticket.service.TicketReplyService;
import com.tahmid.support_ticket.service.TicketService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TicketReplyController {

    private final TicketReplyService replyService;
    private final TicketService ticketService;

    public TicketReplyController(
            TicketReplyService replyService,
            TicketService ticketService) {

        this.replyService = replyService;
        this.ticketService = ticketService;
    }

    // Customer views their own ticket conversation
    @GetMapping("/tickets/{id}/replies")
    public String customerReplies(
            @PathVariable Long id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Ticket ticket = ticketService.getTicketById(id);

            if (!ticket.getCustomer().getEmail()
                    .equalsIgnoreCase(authentication.getName())) {

                redirectAttributes.addFlashAttribute(
                        "error",
                        "You are not allowed to access this ticket."
                );

                return "redirect:/tickets";
            }

            model.addAttribute("ticket", ticket);
            model.addAttribute(
                    "replies",
                    replyService.getReplies(
                            id,
                            authentication.getName(),
                            false
                    )
            );

            model.addAttribute("isAdmin", false);

            return "ticket-replies";

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );

            return "redirect:/tickets";
        }
    }

    // Admin views a ticket conversation
    @GetMapping("/admin/tickets/{id}/replies")
    public String adminReplies(
            @PathVariable Long id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Ticket ticket = ticketService.getTicketById(id);

            model.addAttribute("ticket", ticket);
            model.addAttribute(
                    "replies",
                    replyService.getReplies(
                            id,
                            authentication.getName(),
                            true
                    )
            );

            model.addAttribute("isAdmin", true);

            return "ticket-replies";

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );

            return "redirect:/admin/tickets";
        }
    }

    // Customer submits a reply
    @PostMapping("/tickets/{id}/replies")
    public String customerReply(
            @PathVariable Long id,
            @RequestParam String message,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            replyService.addReply(
                    id,
                    authentication.getName(),
                    false,
                    message
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Your reply was sent successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/tickets/" + id + "/replies";
    }

    // Admin submits a reply
    @PostMapping("/admin/tickets/{id}/replies")
    public String adminReply(
            @PathVariable Long id,
            @RequestParam String message,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            replyService.addReply(
                    id,
                    authentication.getName(),
                    true,
                    message
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Admin reply was sent successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/admin/tickets/" + id + "/replies";
    }
}
