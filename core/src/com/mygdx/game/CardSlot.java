package com.mygdx.game;

import static com.mygdx.game.Textures.cardSlotTexture;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class CardSlot extends Actor {
    int posX;
    int posY;
    static int padding = 10;

    public CardSlot(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        this.setZIndex(4);
        this.addListener(new InputListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                CardSlot.this.moveBy(2,2);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                CardSlot.this.moveBy(-2,-2);
            }
        });
    }

    @Override
    public void draw(Batch batch, float alpha){
        batch.draw(cardSlotTexture, getX(), getY(), getWidth(), getHeight());
    }
}
