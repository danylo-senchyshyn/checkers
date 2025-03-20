package sk.tuke.gamestudio.service;

import org.junit.Before;
import org.junit.Test;
import sk.tuke.kpi.kp.entity.Score;
import sk.tuke.kpi.kp.service.ScoreService;
import sk.tuke.kpi.kp.service.ScoreServiceJDBC;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ScoreServiceTest {
    ScoreService scoreService;

    @Before
    public void setUp() {
        scoreService = new ScoreServiceJDBC();
    }

    @Test
    public void resetTest() {
        scoreService.reset();

        Date date = new Date();
        scoreService.addScore(new Score("checkers", "player1", 100, date));
        scoreService.addScore(new Score("checkers", "player2", 200, date));

        List<Score> scoresBeforeReset = scoreService.getTopScores("checkers");
        assertEquals(2, scoresBeforeReset.size());

        scoreService.reset();

        List<Score> scoresAfterReset = scoreService.getTopScores("checkers");
        assertEquals(0, scoresAfterReset.size());

        scoreService.reset();
    }

    @Test
    public void addScore() {
        scoreService.reset();

        Date date = new Date();
        scoreService.addScore(new Score("checkers", "player1", 70, date));
        List<Score> scores = scoreService.getTopScores("checkers");

        assertEquals(1, scores.size());
        assertEquals("checkers", scores.get(0).getGame());
        assertEquals("player1", scores.get(0).getPlayer());
        assertEquals(70, scores.get(0).getPoints());
        assertEquals(date, scores.get(0).getPlayedOn());

        scoreService.reset();
    }

    @Test
    public void getTopScores() {
        scoreService.reset();

        Date date = new Date();
        scoreService.addScore(new Score("checkers", "player1", 150, date));
        scoreService.addScore(new Score("checkers", "player2", 120, date));
        scoreService.addScore(new Score("checkers", "player3", 180, date));

        List<Score> scores = scoreService.getTopScores("checkers");

        assertEquals(3, scores.size());

        assertEquals("checkers", scores.get(0).getGame());
        assertEquals("player3", scores.get(0).getPlayer());
        assertEquals(180, scores.get(0).getPoints());
        assertEquals(date, scores.get(0).getPlayedOn());

        assertEquals("checkers", scores.get(1).getGame());
        assertEquals("player1", scores.get(1).getPlayer());
        assertEquals(150, scores.get(1).getPoints());
        assertEquals(date, scores.get(1).getPlayedOn());

        assertEquals("checkers", scores.get(2).getGame());
        assertEquals("player2", scores.get(2).getPlayer());
        assertEquals(120, scores.get(2).getPoints());
        assertEquals(date, scores.get(2).getPlayedOn());

        scoreService.reset();
    }
}
