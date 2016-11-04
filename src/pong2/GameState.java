package pong2;

import java.awt.Point;
import java.io.Serializable;

public class GameState implements Serializable {

    public Score SCORE = new Score();
    
    private Point ball;
    private String Name;
    private Point paddle;
    private int dx;
    private int dy;

    public Score getSCORE() {
        return SCORE;
    }

    public void setSCORE(Score SCORE) {
        this.SCORE = SCORE;
    }

    public Point getBall() {
        return ball;
    }

    public void setBall(Point ball) {
        this.ball = ball;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public Point getPaddle() {
        return paddle;
    }

    public void setPaddle(Point paddle) {
        this.paddle = paddle;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }
  
}
