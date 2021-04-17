package org.itstep.domain.dao.Impl;

import org.itstep.domain.dao.TicketDao;
import org.itstep.domain.entity.Ticket;
import org.itstep.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketDaoImpl implements TicketDao {

    private final TicketRepository ticketRepository;

    public TicketDaoImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }


    @Override
    public void save(Ticket entity) {
        ticketRepository.save(entity);
    }

    @Override
    public Ticket findById(Integer integer) {
        return ticketRepository.findTicketById(integer);
    }

    @Override
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket update(Ticket entity) {
        return ticketRepository.saveAndFlush(entity);
    }

    @Override
    public void delete(Ticket entity) {
        ticketRepository.delete(findById(entity.getId()));
    }

    @Override
    public Ticket findByMessageToId(Integer id) {
        return ticketRepository.findTicketByMessageToId(id);
    }

    @Override
    public Ticket findByUserId(Integer id) {
        return ticketRepository.findTicketByUserId(id);
    }

    @Override
    public Ticket findByDate(Integer date) {
        return ticketRepository.findTicketByMessageDate(date);
    }

    @Override
    public Ticket findByDateAndMessageToId(Integer date, Integer messageToId) {
        return ticketRepository.findTicketByMessageDateAndMessageFromId(date, messageToId);
    }

    @Override
    public Ticket findByUserIdAndOpenedAndNotClosed(Integer userId) {
        return ticketRepository.findTicketByUserIdAndOpenedIsTrueAndClosedIsFalse(userId);
    }
}
