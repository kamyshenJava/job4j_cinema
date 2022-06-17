package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.Session;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Repository
public class TicketStore {
    private BasicDataSource pool;
    private static final Logger LOG = LoggerFactory.getLogger(TicketStore.class.getName());

    public TicketStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Ticket add(Ticket ticket) {
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO ticket(session_id, pos_row, cell, user_id) VALUES (?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ticket.getSession().getId());
            ps.setInt(2, ticket.getRow());
            ps.setInt(3, ticket.getCell());
            ps.setInt(4, ticket.getUser().getId());
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    ticket.setId(rs.getInt("id"));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in TicketStore", e);
        }
        return ticket;
    }

    public Ticket findById(int id) {
        Ticket rsl = null;
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT t.id, session_id, name, pos_row, cell, user_id, username, email, phone"
                             + "  FROM ticket t JOIN sessions s ON s.id = t.session_id"
                             + "  JOIN users u ON user_id = u.id WHERE t.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    rsl = new Ticket(rs.getInt("id"),
                            new Session(rs.getInt("session_id"), rs.getString("name")),
                            rs.getInt("pos_row"), rs.getInt("cell"),
                            new User(rs.getInt("user_id"), rs.getString("username"),
                                    rs.getString("email"), rs.getString("phone")));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in TicketStore", e);
        }
        return rsl;
    }
}
