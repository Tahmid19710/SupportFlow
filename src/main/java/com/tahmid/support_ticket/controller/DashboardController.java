package com.tahmid.support_ticket.controller;

import com.tahmid.support_ticket.entity.AppUser;
import com.tahmid.support_ticket.entity.Role;
import com.tahmid.support_ticket.entity.TicketPriority;
import com.tahmid.support_ticket.entity.TicketStatus;
import com.tahmid.support_ticket.repository.AppUserRepository;
import com.tahmid.support_ticket.repository.TicketRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;

@Controller
public class DashboardController {

    private final AppUserRepository userRepository;
    private final TicketRepository ticketRepository;

    public DashboardController(
            AppUserRepository userRepository,
            TicketRepository ticketRepository) {

        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Authentication authentication,
            Model model) {

        String email = authentication.getName()
                .trim()
                .toLowerCase(Locale.ROOT);

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Logged-in user not found."
                        ));

        // Basic user information
        model.addAttribute("userName", user.getName());
        model.addAttribute("userRole", user.getRole().name());

        // =====================================================
        // CUSTOMER DASHBOARD STATISTICS
        // =====================================================

        if (user.getRole() == Role.CUSTOMER) {

            Long customerId = user.getId();

            long totalTickets =
                    ticketRepository.countByCustomer_Id(customerId);

            long openTickets =
                    ticketRepository.countByCustomer_IdAndStatus(
                            customerId,
                            TicketStatus.OPEN
                    );

            long inProgressTickets =
                    ticketRepository.countByCustomer_IdAndStatus(
                            customerId,
                            TicketStatus.IN_PROGRESS
                    );

            long resolvedTickets =
                    ticketRepository.countByCustomer_IdAndStatus(
                            customerId,
                            TicketStatus.RESOLVED
                    );

            long cancelledTickets =
                    ticketRepository.countByCustomer_IdAndStatus(
                            customerId,
                            TicketStatus.CANCELLED
                    );

            model.addAttribute(
                    "totalTickets",
                    totalTickets
            );

            model.addAttribute(
                    "openTickets",
                    openTickets
            );

            model.addAttribute(
                    "inProgressTickets",
                    inProgressTickets
            );

            model.addAttribute(
                    "resolvedTickets",
                    resolvedTickets
            );

            model.addAttribute(
                    "cancelledTickets",
                    cancelledTickets
            );
        }

        // =====================================================
        // ADMIN DASHBOARD STATISTICS
        // =====================================================

        if (user.getRole() == Role.ADMIN) {

            long totalTickets =
                    ticketRepository.count();

            long openTickets =
                    ticketRepository.countByStatus(
                            TicketStatus.OPEN
                    );

            long inProgressTickets =
                    ticketRepository.countByStatus(
                            TicketStatus.IN_PROGRESS
                    );

            long resolvedTickets =
                    ticketRepository.countByStatus(
                            TicketStatus.RESOLVED
                    );

            long cancelledTickets =
                    ticketRepository.countByStatus(
                            TicketStatus.CANCELLED
                    );

            long urgentTickets =
                    ticketRepository.countByPriority(
                            TicketPriority.URGENT
                    );

            model.addAttribute(
                    "totalTickets",
                    totalTickets
            );

            model.addAttribute(
                    "openTickets",
                    openTickets
            );

            model.addAttribute(
                    "inProgressTickets",
                    inProgressTickets
            );

            model.addAttribute(
                    "resolvedTickets",
                    resolvedTickets
            );

            model.addAttribute(
                    "cancelledTickets",
                    cancelledTickets
            );

            model.addAttribute(
                    "urgentTickets",
                    urgentTickets
            );
        }

        return "dashboard";
    }
}