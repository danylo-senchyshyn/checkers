package sk.tuke.gamestudio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.Score;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ScoreServiceRestClient implements ScoreService {
    private final String url = "http://localhost:8080/api/score";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void addScore(Score score) {
        restTemplate.postForEntity(url, score, Score.class);
    }

    @Override
    public List<Score> getTopScores(String gameName) {
        List<Score> list = Arrays.asList(restTemplate.getForEntity(url + "/" + gameName, Score[].class).getBody());
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        return list;
    }

    @Override
    public void reset() {
        restTemplate.delete(url);
    }
}