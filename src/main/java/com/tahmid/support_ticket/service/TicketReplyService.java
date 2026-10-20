
package com.tahmid.support_ticket.service;

import com.tahmid.support_ticket.entity.AppUser;
import com.tahmid.support_ticket.entity.Role;
import com.tahmid.support_ticket.entity.Ticket;
import com.tahmid.support_ticket.entity.TicketReply;
import com.tahmid.support_ticket.entity.TicketStatus;
import com.tahmid.support_ticket.repository.AppUserRepository;
import com.tahmid.support_ticket.repository.TicketReplyRepository;
import com.tahmid.support_ticket.repository.TicketRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class TicketReplyService {

    private final TicketReplyRepository replyRepository;
    private final TicketRepository ticketRepository;
    private final AppUserRepository userRepository;

    public TicketReplyService(
            TicketReplyRepository replyRepository,
            TicketRepository ticketRepository,
            AppUserRepository userRepository) {

        this.replyRepository = replyRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    // Get replies after verifying ticket access
    @Transactional(readOnly = true)
    public List<TicketReply> getReplies(
            Long ticketId,
            String email,
            boolean isAdmin) {

        Ticket ticket = findTicket(ticketId);

        verifyAccess(ticket, email, isAdmin);

        return replyRepository
                .findByTicket_IdOrderByCreatedAtAsc(ticketId);
    }

    // Add a reply to a ticket
    @Transactional
    public TicketReply addReply(
            Long ticketId,
            String email,
            boolean isAdmin,
            String message) {

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException(
                    "Reply message cannot be empty.");
        }

        if (message.trim().length() > 5000) {
            throw new IllegalArgumentException(
                    "Reply cannot exceed 5000 characters.");
        }

        Ticket ticket = findTicket(ticketId);

        verifyAccess(ticket, email, isAdmin);

        if (ticket.getStatus() == TicketStatus.CANCELLED
                || ticket.getStatus() == TicketStatus.RESOLVED) {

            throw new IllegalArgumentException(
                    "Replies are not allowed for cancelled or resolved tickets.");
        }

        AppUser sender = userRepository
                .findByEmail(normalizeEmail(email))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Logged-in user was not found."));

        TicketReply reply = new TicketReply();
        reply.setTicket(ticket);
        reply.setSender(sender);
        reply.setMessage(message.trim());
        reply.setSenderRole(sender.getRole().name());

        return replyRepository.save(reply);
    }

    // Find ticket by ID
    private Ticket findTicket(Long ticketId) {

        return ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Ticket not found."));
    }

    // Check whether the current user can access this ticket
    private void verifyAccess(
            Ticket ticket,
            String email,
            boolean isAdmin) {

        AppUser user = userRepository
                .findByEmail(normalizeEmail(email))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found."));

        if (isAdmin) {
            if (user.getRole() != Role.ADMIN) {
                throw new IllegalArgumentException(
                        "Admin access is required.");
            }
        } else {
            String ownerEmail = normalizeEmail(
                    ticket.getCustomer().getEmail());

            if (!ownerEmail.equals(normalizeEmail(email))) {
                throw new IllegalArgumentException(
                        "You cannot access another customer's ticket.");
            }
        }
    }

    private String normalizeEmail(String email) {

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
