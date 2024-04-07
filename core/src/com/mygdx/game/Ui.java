package com.mygdx.game;

import static com.mygdx.game.MyGdxGame.PLAYER_ACTION_COUNT;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class Ui extends Group {
    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
    TextButton nextButton;
    TextButton resetButton;

    public Ui(float buttonHeight, float buttonWidth) {
        nextButton = new TextButton(String.valueOf(PLAYER_ACTION_COUNT), skin);
        nextButton.setBounds(0, 0, buttonWidth, buttonHeight);

        resetButton = new TextButton("Reset", skin);
        resetButton.setBounds(buttonWidth, 0, buttonWidth, buttonHeight);
        this.addActor(nextButton);
        this.addActor(resetButton);
    }
}
