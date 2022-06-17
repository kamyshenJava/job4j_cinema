package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

@Repository
public class UserStore {

    private BasicDataSource pool;
    private static final Logger LOG = LoggerFactory.getLogger(UserStore.class.getName());

    public UserStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Optional<User> add(User user) {
        User rsl = null;
        try (Connection con = pool.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO users(username, email, phone) VALUES (?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt("id"));
                    rsl = user;
                }
            }
        } catch (Exception e) {
            LOG.error("Exception is UserStore", e);
        }
        return Optional.ofNullable(rsl);
    }

    public Optional<User> findUserByEmailAndPhone(String email, String phone) {
        User rsl = null;
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE email = ? AND phone = ?")) {
            ps.setString(1, email);
            ps.setString(2, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    rsl = new User(rs.getInt("id"), rs.getString("username"),
                            rs.getString("email"), rs.getString("phone"));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception is UserStore", e);
        }
        return Optional.ofNullable(rsl);
    }
}
