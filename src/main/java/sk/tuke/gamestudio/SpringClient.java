package sk.tuke.gamestudio;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.game.checkers.consoleui.ConsoleUI;
import sk.tuke.gamestudio.game.checkers.core.CheckersField;
import sk.tuke.gamestudio.server.GameStudioServer;
import sk.tuke.gamestudio.service.comment.CommentService;
import sk.tuke.gamestudio.service.comment.CommentServiceRestClient;
import sk.tuke.gamestudio.service.rating.RatingService;
import sk.tuke.gamestudio.service.rating.RatingServiceRestClient;
import sk.tuke.gamestudio.service.score.ScoreService;
import sk.tuke.gamestudio.service.score.ScoreServiceRestClient;

@SpringBootApplication
@Configuration
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
        pattern = "sk.tuke.gamestudio.server.*"))
public class SpringClient {

    public static void main(String[] args) {
        //SpringApplication.run(SpringClient.class, args);
        new SpringApplicationBuilder(SpringClient.class).web(WebApplicationType.NONE).run(args);
    }

    @Bean
    public CommandLineRunner runner(ConsoleUI ui) {
        return args -> ui.play();
    }
    @Bean
    public ConsoleUI consoleUI(CheckersField field) {
        return new ConsoleUI(field);
    }
    @Bean
    public CheckersField field() {
        return new CheckersField();
    }

    @Bean
    public ScoreService scoreService() {
        //return new ScoreServiceJPA();
        return new ScoreServiceRestClient();
    }
    @Bean
    public CommentService commentService() {
        //return new CommentServiceJPA();
        return new CommentServiceRestClient();
    }
    @Bean
    public RatingService ratingService() {
        //return new RatingServiceJPA();
        return new RatingServiceRestClient();
    }

    @Bean
    public GameStudioServer gameStudioServer() {
        return new GameStudioServer();
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
