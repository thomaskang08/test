package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import java.util.Random;

public class Card extends Actor {
    private final TextureRegion flippedRegion;
    private TextureRegion region;
    private int posX;
    private int posY;
    private int value;
    Random rand = new Random();
    boolean faceDown = true;
    int rank;

    public Card(int posX, int posY, int rank, Card[][] cardOnBoard, Player player) {
        this.posX = posX;
        this.posY = posY;
        if (posX >= 0) {
            cardOnBoard[posY][posX] = this;
        }
        this.rank = rank;
        this.value = rand.nextInt(12) + 1;
        region = new TextureRegion(GameMechanic.texture, (this.value - 1) * 100, this.rank * 144, 100, 144);
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
        batch.draw(faceDown ? flippedRegion : region, getX(), getY(), getWidth(), getHeight());
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
        region = new TextureRegion(GameMechanic.texture, (this.value - 1) * 100, this.rank * 144, 100, 144);
    }

    @Override
    public String toString() {
        return "Red: " + isRed() + ", " + this.value;
    }
}
