package sk.tuke.gamestudio.entity;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@NamedQuery( name = "Score.getTopScores", query = "SELECT s FROM Score s WHERE s.game=:game ORDER BY s.points DESC")
@NamedQuery( name = "Score.resetScores", query = "DELETE FROM Score")
public class Score implements Serializable {
    @Id
    @GeneratedValue
    private int ident;

    @Getter
    @Setter
    private String game;
    @Getter
    @Setter
    private String player;
    @Getter
    @Setter
    private int points;
    @Getter
    @Setter
    private Date playedOn;

    public Score(String game, String player, int points, Date playedOn) {
        this.game = game;
        this.player = player;
        this.points = points;
        this.playedOn = playedOn != null ? playedOn : new Date();
    }
}
