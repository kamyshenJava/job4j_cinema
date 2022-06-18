package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.cinema.model.Session;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.SessionService;
import ru.job4j.cinema.service.TicketService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class SelectController {

    private SessionService sessionService;
    private TicketService ticketService;

    public SelectController(SessionService sessionService, TicketService ticketService) {
        this.sessionService = sessionService;
        this.ticketService = ticketService;
    }

    @GetMapping("/ticket")
    public String chooseSessionForm(Model model, HttpSession session) {
        addUserToModel(model, session);
        List<Session> sessions = sessionService.findAllSessions();
        model.addAttribute("sessions", sessions);
        return "ticket";
    }

    @PostMapping("/ticket")
    public String chooseSession(HttpServletRequest req, HttpSession session) {
        String sessIdString = req.getParameter("sess.id");
        if ("Выберите сеанс".equals(sessIdString)) {
            return "redirect:/ticket";
        }
        int sessionId = Integer.parseInt(sessIdString);
        session.setAttribute("sessionId", sessionId);
        return "redirect:/row";
    }
    @GetMapping("/row")
    public String selectForm(Model model, HttpSession session) {
        addUserToModel(model, session);
        List<Integer> rows = sessionService.getRows();
        model.addAttribute("rows", rows);
        return "row";
    }
    @PostMapping("/row")
    public String select(HttpServletRequest req, HttpSession session) {
        String rowString = req.getParameter("row");
        if ("Выберите ряд".equals(rowString)) {
            return "redirect:/row";
        }
        int row = Integer.parseInt(rowString);
        session.setAttribute("row", row);
        return "redirect:/seat";
    }

    @GetMapping("/seat")
    public String seatForm(Model model, HttpSession session) {
        addUserToModel(model, session);
        int sessionId = (int) session.getAttribute("sessionId");
        int row = (int) session.getAttribute("row");
        List<Integer> seats = sessionService.calcAvailableSeats(sessionId, row);
        model.addAttribute("seats", seats);
        return "seat";
    }
    @PostMapping("/seat")
    public String seat(HttpServletRequest req, HttpSession session) {
        String seatString = req.getParameter("seat");
        if ("Выберите место".equals(seatString)) {
            return "redirect:/seat";
        }
        int seat = Integer.parseInt(seatString);
        Ticket ticket = new Ticket(new Session((int) session.getAttribute("sessionId")),
                (int) session.getAttribute("row"), seat, (User) session.getAttribute("user"));
        Optional<Ticket> rsl = ticketService.add(ticket);
        if (rsl.isEmpty()) {
            return "redirect:/failure";
        }
        return "redirect:/index";
    }
    @GetMapping("/failure")
    public String failure(Model model, HttpSession session) {
        addUserToModel(model, session);
        return "failure";
    }

    private void addUserToModel(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setUsername("guest");
        }
        model.addAttribute("user", user);
    }
}
