package sk.tuke.gamestudio.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;

import java.io.Serializable;
import java.util.Date;

//@Entity
//@NamedQuery( name = "Rating.getRating", query = "SELECT COALESCE(r.rating, 0) FROM Rating r WHERE r.game=:game AND r.player=:player")
//@NamedQuery( name = "Rating.getAverageRating", query = "SELECT r FROM Rating r WHERE r.game=:game")
//@NamedQuery( name = "Rating.resetScores", query = "DELETE FROM Rating")
public class Rating implements Serializable {
    private String game;
    private String player;
    private int rating;
    private Date ratedOn;

//    @Id
//    @GeneratedValue
//    private int ident;

    public Rating() {}
    public Rating(String game, String player, int rating, Date ratedOn) {
        this.game = game;
        this.player = player;
        this.rating = rating;
        this.ratedOn = ratedOn;
    }

    public String getGame() {
        return game;
    }
    public void setGame(String game) {
        this.game = game;
    }

    public String getPlayer() {
        return player;
    }
    public void setPlayer(String player) {
        this.player = player;
    }

    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }

    public Date getRatedOn() {
        return ratedOn;
    }
    public void setRatedOn(Date ratedOn) {
        this.ratedOn = ratedOn;
    }

//    public int getIdent() {
//        return ident;
//    }
//    public void setIdent(int ident) {
//        this.ident = ident;
//    }

    @Override
    public String toString() {
        return "Rating{" +
                "player='" + player + '\'' +
                ", game='" + game + '\'' +
                ", rating=" + rating +
                ", ratedOn=" + ratedOn +
                '}';
    }
}
