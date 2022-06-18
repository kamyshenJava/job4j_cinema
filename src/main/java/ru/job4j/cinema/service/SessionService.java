package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Session;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.store.SessionStore;
import ru.job4j.cinema.store.TicketStore;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class SessionService {

    private SessionStore sessionStore;
    private TicketStore ticketStore;
    private int rowsTotal;
    private int cellsTotal;

    public SessionService(SessionStore sessionStore, TicketStore ticketStore) {
        this.sessionStore = sessionStore;
        this.ticketStore = ticketStore;
        this.rowsTotal = 35;
        this.cellsTotal = 35;
    }

    public List<Session> findAllSessions() {
        return sessionStore.findAll();
    }

    public List<Integer> calcAvailableSeats(int sessionId, int row) {
        List<Ticket> tickets = findTicketsForSessionAndRow(sessionId, row);
        List<Integer> seatsTaken = tickets.stream()
                .filter(ticket -> ticket.getRow() == row)
                .map(Ticket::getCell)
                .toList();
        return IntStream.rangeClosed(1, cellsTotal).
                filter(i -> !seatsTaken.contains(i)).
                boxed().
                toList();
    }

    public List<Integer> getRows() {
        return IntStream.rangeClosed(1, rowsTotal).boxed().toList();
    }

    private List<Ticket> findTicketsForSessionAndRow(int sessionId, int row) {
        return ticketStore.findTicketsForSessionAndRow(sessionId, row);
    }
}
