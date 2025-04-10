package sk.tuke.gamestudio.server.controller;

    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.GetMapping;

    @Controller
    public class PageController {

        @GetMapping("/")
        public String home() {
            return "home_page"; // Файл: src/main/resources/templates/home_page.html
        }

        @GetMapping("/signup")
        public String signUp() {
            return "logger/sign_up"; // Файл: src/main/resources/templates/logger/sign_up.html
        }

        @GetMapping("/signin")
        public String signIn() {
            return "logger/sign_in"; // Файл: src/main/resources/templates/logger/sign_in.html
        }

        @GetMapping("/rules")
        public String rules() {
            return "rules"; // Файл: src/main/resources/templates/rules.html
        }

        @GetMapping("/about")
        public String about() {
            return "about"; // Файл: src/main/resources/templates/about.html
        }

//        @GetMapping("/checkers")
//        public String game() {
//            return "checkers"; // Файл: src/main/resources/templates/checkers.html
//        }

        @GetMapping("/terms")
        public String terms() {
            return "logger/terms"; // Файл: src/main/resources/templates/logger/terms.html
        }

        @GetMapping("/privacy")
        public String privacy() {
            return "logger/privacy"; // Файл: src/main/resources/templates/logger/privacy.html
        }
    }