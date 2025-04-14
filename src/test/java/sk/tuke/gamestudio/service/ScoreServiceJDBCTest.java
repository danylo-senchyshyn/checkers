package sk.tuke.gamestudio.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.service.score.ScoreService;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class ScoreServiceJDBCTest {
    @Autowired
    ScoreService scoreService;

    @Test
    public void resetTest() {
        scoreService.reset();
        assertEquals(0, scoreService.getTopScores("checkers").size());
    }

    @Test
    public void addScore() {
        scoreService.reset();

        Date date = new Date();
        scoreService.addScore(new Score("checkers", "test-player1", 70, date));
        List<Score> scores = scoreService.getTopScores("checkers");

        assertEquals(1, scores.size());
        assertEquals("checkers", scores.get(0).getGame());
        assertEquals("test-player1", scores.get(0).getPlayer());
        assertEquals(70, scores.get(0).getPoints());
        assertEquals(date, scores.get(0).getPlayedOn());
    }

    @Test
    public void getTopScores() {
        scoreService.reset();

        Date date = new Date();
        scoreService.addScore(new Score("checkers", "test-player1", 150, date));
        scoreService.addScore(new Score("checkers", "test-player2", 120, date));
        scoreService.addScore(new Score("checkers", "test-player3", 180, date));

        List<Score> scores = scoreService.getTopScores("checkers");

        assertEquals(3, scores.size());

        assertEquals("checkers", scores.get(0).getGame());
        assertEquals("test-player3", scores.get(0).getPlayer());
        assertEquals(180, scores.get(0).getPoints());
        assertEquals(date, scores.get(0).getPlayedOn());

        assertEquals("checkers", scores.get(1).getGame());
        assertEquals("test-player1", scores.get(1).getPlayer());
        assertEquals(150, scores.get(1).getPoints());
        assertEquals(date, scores.get(1).getPlayedOn());

        assertEquals("checkers", scores.get(2).getGame());
        assertEquals("test-player2", scores.get(2).getPlayer());
        assertEquals(120, scores.get(2).getPoints());
        assertEquals(date, scores.get(2).getPlayedOn());
    }
}