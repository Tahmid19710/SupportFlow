package com.tahmid.support_ticket.repository;

import com.tahmid.support_ticket.entity.TicketReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketReplyRepository
        extends JpaRepository<TicketReply, Long> {

    List<TicketReply> findByTicket_IdOrderByCreatedAtAsc(Long ticketId);

    void deleteByTicket_Id(Long ticketId);
}