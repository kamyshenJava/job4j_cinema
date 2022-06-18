package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.store.TicketStore;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {
    private TicketStore ticketStore;

    public TicketService(TicketStore ticketStore) {
        this.ticketStore = ticketStore;
    }

    public Optional<Ticket> add(Ticket ticket) {
        return ticketStore.add(ticket);
    }

    public List<Ticket> findByUserId(int userId) {
        return ticketStore.findByUserId(userId);
    }
}
