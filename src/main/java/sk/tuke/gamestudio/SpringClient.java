package sk.tuke.gamestudio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.tuke.gamestudio.game.checkers.Main;
import sk.tuke.gamestudio.game.checkers.consoleui.ConsoleUI;
import sk.tuke.gamestudio.game.checkers.core.Field;
import sk.tuke.gamestudio.service.*;

import javax.sql.DataSource;

@SpringBootApplication
@Configuration
public class SpringClient {

    public static void main(String[] args) {
        SpringApplication.run(SpringClient.class, args);
        try {
            Main.main(args);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    @Bean
    public CommandLineRunner runner(ConsoleUI ui) {
        return args -> ui.play();
    }

    @Bean
    public ConsoleUI consoleUI(Field field) {
        return new ConsoleUI(field);
    }

    @Bean
    public Field field() {
        return new Field();
    }

    @Bean
    public ScoreService scoreService(DataSource dataSource) {
        return new ScoreServiceJDBC(dataSource);
    }
    @Bean
    public CommentService commentService(DataSource dataSource) {
        return new CommentServiceJDBC(dataSource);
    }
    @Bean
    public RatingService ratingService(DataSource dataSource) {
        return new RatingServiceJDBC(dataSource);
    }
}
