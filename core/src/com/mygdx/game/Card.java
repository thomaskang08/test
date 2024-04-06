package com.mygdx.game;

import static com.mygdx.game.GameMechanic.attackRegion;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import java.util.Random;

public class Card extends Actor {
    private final TextureRegion flippedRegion;
    private int posX;
    private int posY;
    private int value;
    Random rand = new Random();
    boolean faceDown = false;
    int attackFace;
    int rank = 0;

    public Card(int posX, int posY, int rank, Card[][] cardOnBoard, Player player) {
        this.posX = posX;
        this.posY = posY;
        if (posX >= 0) {
            cardOnBoard[posY][posX] = this;
        }
        this.rank = rank;
        this.value = 1; //rand.nextInt(13) + 1;
        if (rank == 2) {
            this.attackFace = rand.nextInt(4) + 1;
        }
        flippedRegion = new TextureRegion(GameMechanic.texture, 14 * 100, this.rank * 144, 100, 144);
        this.setZIndex(5);
        this.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (Card.this.faceDown) {
                    Card.this.faceDown = false;
                    player.act();
                }
                return true;
            }
        });
    }

    @Override
    public void draw(Batch batch, float alpha){
        batch.draw(faceDown ? flippedRegion : getRegion(), getX(), getY(), getWidth(), getHeight());
        switch (attackFace) {
            case 1:
                batch.draw(attackRegion, getX() + getWidth()/2f, getY() + getHeight() - 25, getWidth() * 0.2f, getWidth() * 0.2f);
                return;
            case 2:
                batch.draw(attackRegion, getX() + getWidth()/2f, getY(), getWidth() * 0.2f, getWidth() * 0.2f);
                return;
            case 3:
                batch.draw(attackRegion, getX(), getY() + getHeight()/2f, getWidth() * 0.2f, getWidth() * 0.2f);
                return;
            case 4:
                batch.draw(attackRegion, getX() + getWidth() - 25, getY() + getHeight()/2f, getWidth() * 0.2f, getWidth() * 0.2f);
                return;
        }
    }

    public boolean isRed() {
        return rank == 1 || rank == 3;
    }

    public void setPosX(int x, Card[][] cardOnBoard) {
        setPos(x, posY, cardOnBoard);
    }

    public void setPosY(int y, Card[][] cardOnBoard) {
        setPos(posX, y, cardOnBoard);
    }

    public void setPos(int x, int y, Card[][] cardOnBoard) {
        if (posX >= 0 && posY >= 0) {
            cardOnBoard[posY][posX] = null;
        }
        posY = y;
        posX = x;
        if (posX >= 0 && posY >= 0) {
            cardOnBoard[posY][posX] = this;
        }
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value, Card[][] cardOnBoard) {
        this.value = value;
        if (value <= 0 || value > 13) {
            this.remove();
            cardOnBoard[posY][posX] = null;
        }
    }

    private TextureRegion getRegion() {
        if (posX == 2 && posY == 0){
            return new TextureRegion(GameMechanic.texture, 13 * 100, this.rank * 144, 100, 144);
        }
        return new TextureRegion(GameMechanic.texture, (this.value - 1) * 100, this.rank * 144, 100, 144);
    }

    public Point getAttackDirection() {
        switch (attackFace) {
            case 1:
                return new Point(posX, posY - 1);
            case 2:
                return new Point(posX, posY + 1);
            case 3:
                return new Point(posX - 1, posY);
            case 4:
                return new Point(posX + 1, posY);
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return "Red: " + isRed() + ", " + this.value;
    }
}
