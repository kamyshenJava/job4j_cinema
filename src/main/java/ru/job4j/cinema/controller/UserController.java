package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String signupForm(Model model, HttpSession session) {
        addUserToModel(model, session);
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(Model model, @ModelAttribute User user) {
        Optional<User> regUser = userService.add(user);
        String rsl = "redirect:/login";
        if (regUser.isEmpty()) {
            rsl = "redirect:/fail";
        }
        return rsl;
    }

    @GetMapping("/fail")
    public String fail(Model model, HttpSession session) {
        addUserToModel(model, session);
        return "fail";
    }

    @GetMapping("/login")
    public String loginForm(Model model, @RequestParam(name = "fail", required = false) Boolean fail,
                            HttpSession session) {
        addUserToModel(model, session);
        model.addAttribute("fail", fail != null);
        return "login";
    }

    @PostMapping("/login")
    public String login(Model model, @ModelAttribute User user, HttpServletRequest req) {
        Optional<User> userDb = userService.findUserByEmailAndPhone(user.getEmail(), user.getPhone());
        if (userDb.isEmpty()) {
            return "redirect:/login?fail=true";
        }
        HttpSession session = req.getSession();
        session.setAttribute("user", userDb.get());
        return "redirect:/index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
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
