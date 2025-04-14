package sk.tuke.gamestudio.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.service.comment.CommentServiceJPA;
import sk.tuke.gamestudio.service.score.ScoreService;
import sk.tuke.gamestudio.service.score.ScoreServiceJPA;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ScoreServiceJPATest {
    @Autowired
    private ScoreService scoreService;

    @BeforeEach
    public void setUp() {
        scoreService.reset();
    }

    @Test
    public void testAddScore() {
        Score score = new Score("checkers", "player1", 100, new Date());
        scoreService.addScore(score);

        List<Score> scores = scoreService.getTopScores("checkers");
        assertEquals(1, scores.size());
        assertEquals("player1", scores.get(0).getPlayer());
        assertEquals(100, scores.get(0).getPoints());
    }

    @Test
    public void testFindScoresByGame() {
        scoreService.addScore(new Score("checkers", "player1", 100, java.sql.Timestamp.valueOf("2023-01-01 10:00:00")));
        scoreService.addScore(new Score("checkers", "player2", 200, java.sql.Timestamp.valueOf("2023-01-02 10:00:00")));
        scoreService.addScore(new Score("checkers", "player3", 150, java.sql.Timestamp.valueOf("2023-01-03 10:00:00")));

        List<Score> scores = scoreService.getTopScores("checkers");
        assertEquals(3, scores.size());
        assertEquals("player2", scores.get(0).getPlayer());
        assertEquals(200, scores.get(0).getPoints());
    }

    @Test
    public void testDeleteScores() {
        scoreService.addScore(new Score("checkers", "player1", 100, java.sql.Timestamp.valueOf("2023-01-01 10:00:00")));
        scoreService.addScore(new Score("checkers", "player2", 200, java.sql.Timestamp.valueOf("2023-01-02 10:00:00")));

        scoreService.reset();
        List<Score> scores = scoreService.getTopScores("checkers");
        assertTrue(scores.isEmpty());
    }
}