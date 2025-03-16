package sk.tuke.kpi.kp.checkers.service;

import sk.tuke.kpi.kp.checkers.entity.Score;

import java.util.List;

public interface ScoreService {
    void addScore(Score score) throws ScoreException;
    List<Score> getTopScores(String game) throws ScoreException;
    void reset() throws ScoreException;
}
