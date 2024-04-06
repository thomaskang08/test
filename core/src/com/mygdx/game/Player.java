package com.mygdx.game;

import static com.mygdx.game.MyGdxGame.PLAYER_ACTION_COUNT;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class Player {
    int actionLeft;
    TextButton playerInfo;
    Group game;

    public Player(int actionLeft, TextButton playerInfo, Group game) {
        this.actionLeft = actionLeft;
        this.playerInfo = playerInfo;
        this.game = game;
    }

    public void setActionLeft(int actionLeft) {
        this.actionLeft = actionLeft;
        playerInfo.setText("Action left: " + actionLeft);
    }

    public void act() {
        setActionLeft(this.actionLeft - 1);
        if (this.actionLeft <= 0) {
            game.setTouchable(Touchable.disabled);
        }
    }

    public void reset() {
        setActionLeft(PLAYER_ACTION_COUNT);
        game.setTouchable(Touchable.enabled);
    }
}
