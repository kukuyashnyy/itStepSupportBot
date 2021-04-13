package org.itstep.domain.dao;

import org.itstep.domain.entity.Ticket;

public interface TicketDao extends Dao<Ticket, Integer> {
    Ticket findByMessageToId(Integer id);
    Ticket findByUserId(Integer id);
    Ticket findByDate(Integer date);
    Ticket findByDateAndMessageToId(Integer date, Integer messageToId);
    Ticket findByUserIdAndOpenedAndNotClosed(Integer userId);
}
