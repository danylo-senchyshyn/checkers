package sk.tuke.gamestudio.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;

import java.io.Serializable;
import java.util.Date;

//@Entity
//@NamedQuery(name = "Comment.getComments", query = "SELECT c FROM Comment c WHERE c.game=:game ORDER BY c.commentedOn DESC")
//@NamedQuery(name = "Comment.resetComments", query = "DELETE FROM Comment")
public class Comment implements Serializable {
    private String player;
    private String game;
    private String comment;
    private Date commentedOn;

//    @Id
//    @GeneratedValue
//    private int ident;
//
//    public Comment() {}
    public Comment(String game, String player, String comment, Date commentedOn) {
        this.game = game;
        this.player = player;
        this.comment = comment;
        this.commentedOn = commentedOn;
    }

    public Date getCommentedOn() {
        return commentedOn;
    }
    public void setCommentedOn(Date commentedOn) {
        this.commentedOn = commentedOn;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
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

//    public int getIdent() {
//        return ident;
//    }
//    public void setIdent(int ident) {
//        this.ident = ident;
//    }

    @Override
    public String toString() {
        return "Comment{" +
                "player='" + player + '\'' +
                ", game='" + game + '\'' +
                ", comment='" + comment + '\'' +
                ", commentedOn=" + commentedOn +
                '}';
    }
}
