package sk.tuke.gamestudio.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "home_page";
    }

    @GetMapping("/signup")
    public String signUp() {
        return "sign_up";
    }

    @GetMapping("/signin")
    public String signIn() {
        return "sign_in";
    }

    @GetMapping("/rules")
    public String rules() {
        return "rules";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/game")
    public String game() {
        return "game";
    }

    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "privacy";
    }
}