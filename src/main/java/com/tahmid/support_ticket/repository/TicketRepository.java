package com.tahmid.support_ticket.repository;

import com.tahmid.support_ticket.entity.Ticket;
import com.tahmid.support_ticket.entity.TicketPriority;
import com.tahmid.support_ticket.entity.TicketStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository
        extends JpaRepository<Ticket, Long> {

    // =========================================================
    // CUSTOMER TICKETS
    // =========================================================

    List<Ticket> findByCustomer_IdOrderByCreatedAtDesc(
            Long customerId
    );


    // =========================================================
    // ADMIN SEARCH & FILTER
    // =========================================================

    @Query("""
            SELECT t
            FROM Ticket t
            WHERE
                (
                    :keyword IS NULL
                    OR :keyword = ''
                    OR LOWER(t.title) LIKE LOWER(
                        CONCAT('%', :keyword, '%'))
                    OR LOWER(t.description) LIKE LOWER(
                        CONCAT('%', :keyword, '%'))
                    OR LOWER(t.customer.email) LIKE LOWER(
                        CONCAT('%', :keyword, '%'))
                )
                AND (
                    :status IS NULL
                    OR t.status = :status
                )
                AND (
                    :priority IS NULL
                    OR t.priority = :priority
                )
            ORDER BY t.createdAt DESC
            """)
    List<Ticket> searchTickets(
            @Param("keyword") String keyword,
            @Param("status") TicketStatus status,
            @Param("priority") TicketPriority priority
    );


    // =========================================================
    // CUSTOMER DASHBOARD STATISTICS
    // =========================================================

    long countByCustomer_Id(
            Long customerId
    );

    long countByCustomer_IdAndStatus(
            Long customerId,
            TicketStatus status
    );

    long countByCustomer_IdAndPriority(
            Long customerId,
            TicketPriority priority
    );


    // =========================================================
    // ADMIN DASHBOARD STATISTICS
    // =========================================================

    long countByStatus(
            TicketStatus status
    );

    long countByPriority(
            TicketPriority priority
    );
}