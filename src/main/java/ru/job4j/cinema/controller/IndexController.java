package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.TicketService;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class IndexController {

    private TicketService ticketService;

    public IndexController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/index")
    public String index(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setUsername("guest");
            return "index";
        }
        model.addAttribute("user", user);
        List<Ticket> tickets = ticketService.findByUserId(user.getId());
        model.addAttribute("tickets", tickets);
        return "index";
    }
}
