package com.mygdx.game;

import static com.mygdx.game.GameMechanic.attackRegion;
import static com.mygdx.game.Textures.cardBackTexture;
import static com.mygdx.game.Textures.cardFrontBlackTexture;
import static com.mygdx.game.Textures.cardFrontWhiteTexture;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Card extends Actor {
    private int posX;
    private int posY;
    private int value;
    boolean faceDown = true;
    int attackFace;
    int rank;
    final Player player;

    public Card(int posX, int posY, int rank, int value, Player player) {
        this.posX = posX;
        this.posY = posY;
        this.player = player;
        this.rank = rank;
        this.value = value; //rand.nextInt(13) + 1;
        this.setZIndex(5);
    }

    @Override
    public void draw(Batch batch, float alpha){
        batch.draw(faceDown ? cardBackTexture : isRed() ? cardFrontWhiteTexture : cardFrontBlackTexture, getX(), getY(), getWidth(), getHeight());
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

    public void setPos(int x, int y, Board board) {
        if (posX < 0 && x >= 0) {
            this.remove();
            board.cardOnBoardGroup.addActor(this);
        }
        if (posX >= 0 && posY >= 0) {
            board.cardOnBoard[posY][posX] = null;
        }
        posY = y;
        posX = x;
        if (posX >= 0 && posY >= 0) {
            board.cardOnBoard[posY][posX] = this;
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

    public void setAttackFace(int row, int col) {
        if (row == posX && col == posY - 1) {
            attackFace = 1;
        } else if (row == posX && col == posY + 1) {
            attackFace = 2;
        } else if (row == posX - 1 && col == posY) {
            attackFace = 3;
        } else if (row == posX + 1 && col == posY) {
            attackFace = 4;
        }
    }

    @Override
    public String toString() {
        return "Red: " + isRed() + ", " + this.value;
    }
}
