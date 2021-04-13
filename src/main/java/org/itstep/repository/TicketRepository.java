package org.itstep.repository;

import org.itstep.domain.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    Ticket findTicketByMessageToId(Integer id);
    Ticket findTicketByUserId(Integer userId);
    Ticket findTicketById(Integer id);
    Ticket findTicketByMessageDate(Integer date);
    Ticket findTicketByMessageDateAndMessageFromId(Integer date, Integer messageFromId);
    Ticket findTicketByUserIdAndOpenedIsTrueAndClosedIsFalse(Integer userId);

}
