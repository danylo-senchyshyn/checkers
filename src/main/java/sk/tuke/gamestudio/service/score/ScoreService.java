package sk.tuke.gamestudio.service.score;

import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.service.gamestudio.GameStudioService;

import java.util.List;

public interface ScoreService extends GameStudioService {
    void addScore(Score score) throws ScoreException;
    List<Score> getTopScores(String game) throws ScoreException;
    void reset() throws ScoreException;
}
