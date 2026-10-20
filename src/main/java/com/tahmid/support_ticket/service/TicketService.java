package com.tahmid.support_ticket.service;

import com.tahmid.support_ticket.entity.AppUser;
import com.tahmid.support_ticket.entity.Ticket;
import com.tahmid.support_ticket.entity.TicketPriority;
import com.tahmid.support_ticket.entity.TicketStatus;
import com.tahmid.support_ticket.repository.AppUserRepository;
import com.tahmid.support_ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final AppUserRepository userRepository;

    public TicketService(
            TicketRepository ticketRepository,
            AppUserRepository userRepository) {

        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Ticket createTicket(
            String title,
            String description,
            TicketPriority priority,
            String email) {

        String normalizedEmail =
                email.trim().toLowerCase(Locale.ROOT);

        AppUser customer = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer not found."
                        )
                );

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Ticket title is required."
            );
        }

        if (title.trim().length() > 150) {
            throw new IllegalArgumentException(
                    "Ticket title cannot exceed 150 characters."
            );
        }

        if (description == null
                || description.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ticket description is required."
            );
        }

        if (description.trim().length() > 5000) {
            throw new IllegalArgumentException(
                    "Ticket description cannot exceed 5000 characters."
            );
        }

        Ticket ticket = new Ticket();

        ticket.setTitle(title.trim());
        ticket.setDescription(description.trim());

        ticket.setPriority(
                priority != null
                        ? priority
                        : TicketPriority.MEDIUM
        );

        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCustomer(customer);

        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public List<Ticket> getMyTickets(String email) {

        String normalizedEmail =
                email.trim().toLowerCase(Locale.ROOT);

        AppUser customer = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer not found."
                        )
                );

        return ticketRepository
                .findByCustomer_IdOrderByCreatedAtDesc(
                        customer.getId()
                );
    }

    @Transactional(readOnly = true)
    public Ticket getTicketById(Long ticketId) {

        return ticketRepository
                .findById(ticketId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Ticket not found: " + ticketId
                        )
                );
    }

    @Transactional
    public Ticket cancelTicket(
            Long ticketId,
            String email) {

        String normalizedEmail =
                email.trim().toLowerCase(Locale.ROOT);

        Ticket ticket = getTicketById(ticketId);

        String ticketOwnerEmail =
                ticket.getCustomer()
                        .getEmail()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        if (!ticketOwnerEmail.equals(normalizedEmail)) {

            throw new IllegalArgumentException(
                    "You are not allowed to cancel this ticket."
            );
        }

        if (ticket.getStatus() != TicketStatus.OPEN) {

            throw new IllegalArgumentException(
                    "Only OPEN tickets can be cancelled."
            );
        }

        ticket.setStatus(TicketStatus.CANCELLED);

        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public List<Ticket> searchTickets(
            String keyword,
            TicketStatus status,
            TicketPriority priority) {

        String normalizedKeyword =
                keyword == null
                        ? ""
                        : keyword.trim();

        return ticketRepository.searchTickets(
                normalizedKeyword,
                status,
                priority
        );
    }

    @Transactional
    public Ticket updateTicketStatus(
            Long ticketId,
            TicketStatus newStatus) {

        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "Ticket status is required."
            );
        }

        Ticket ticket = getTicketById(ticketId);

        TicketStatus currentStatus =
                ticket.getStatus();

        /*
         * No change requested.
         */
        if (currentStatus == newStatus) {
            return ticket;
        }

        /*
         * CANCELLED tickets are final.
         */
        if (currentStatus == TicketStatus.CANCELLED) {

            throw new IllegalArgumentException(
                    "A cancelled ticket cannot be reopened or changed."
            );
        }

        /*
         * RESOLVED tickets are final.
         */
        if (currentStatus == TicketStatus.RESOLVED) {

            throw new IllegalArgumentException(
                    "A resolved ticket cannot be reopened or changed."
            );
        }

        /*
         * Valid transition:
         *
         * OPEN -> IN_PROGRESS
         */
        if (currentStatus == TicketStatus.OPEN
                && newStatus == TicketStatus.IN_PROGRESS) {

            ticket.setStatus(TicketStatus.IN_PROGRESS);

            return ticketRepository.save(ticket);
        }

        /*
         * Valid transition:
         *
         * IN_PROGRESS -> RESOLVED
         */
        if (currentStatus == TicketStatus.IN_PROGRESS
                && newStatus == TicketStatus.RESOLVED) {

            ticket.setStatus(TicketStatus.RESOLVED);

            return ticketRepository.save(ticket);
        }

        /*
         * Admin cannot cancel tickets.
         *
         * Cancellation belongs to the customer
         * through the customer cancel endpoint.
         */
        if (newStatus == TicketStatus.CANCELLED) {

            throw new IllegalArgumentException(
                    "Tickets can only be cancelled by the customer."
            );
        }

        throw new IllegalArgumentException(
                "Invalid status transition: "
                        + currentStatus
                        + " -> "
                        + newStatus
        );
    }
}