package com.mygdx.game;

import static java.lang.Math.min;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/*
1. Player action: place card/flip card/move card
2. Drop
3. resolve black attack
4. resolve red attack
5. slide and fill
 */
public class MyGdxGame extends ApplicationAdapter {
	public static int MAX_WIDTH = 1080;
	public static int MAX_HEIGHT = 2400;
	public static int PLAYER_ACTION_COUNT = 3;
	Stage stage;
	Group game;
	Board board;
	Image image;
	Deck deck;
	Player player;

	@Override
	public void create() {
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

		Texture texture = new Texture("Belgian School Comics_asset_N8TbYDz3XxrojD97aKivsVkm_a steampunk world in an alternate universe reliant on clockwork technology, game backdrop_inference-img2img_1712103447.jpeg");
		image = new Image(texture);
		stage.addActor(image);

		game = new Group();
		float gameWidth = min(stage.getWidth(), MAX_WIDTH);
		game.setBounds(stage.getWidth()/2 - gameWidth/2, 0, gameWidth, min(stage.getHeight(), MAX_HEIGHT));
		stage.addActor(game);

		// Buttons
		float height = game.getHeight() * 0.05f;
		float width = 3f * height;
		Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
		TextButton dropButton = new TextButton("Action left: " + PLAYER_ACTION_COUNT, skin);
		dropButton.setBounds(game.getWidth() - width, 0, width, height);
		dropButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				game.setTouchable(Touchable.disabled);
				GameMechanic.letItFlow(board, player);
				player.reset();
			}
		});
		TextButton resetButton = new TextButton("Reset", skin);
		resetButton.setBounds(game.getWidth() - width, height, width, height);
		resetButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				board.initializeBoard();
				player.reset();
				deck.reset();
			}
		});
		player = new Player(PLAYER_ACTION_COUNT, dropButton, game);

		stage.addActor(dropButton);
		stage.addActor(resetButton);
		board = new Board(player);
		game.addActor(board);
		board.initializeBoard();
		deck = new Deck(10, board, player);
		game.addActor(deck);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public void render() {
		ScreenUtils.clear(Color.LIME);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		image.setBounds(0,0, stage.getWidth(), stage.getHeight());

		float gameWidth = min(stage.getWidth(), MAX_WIDTH);
		game.setBounds(stage.getWidth()/2 - gameWidth/2,0, min(stage.getWidth(), MAX_WIDTH), min(stage.getHeight(), MAX_HEIGHT));
		board.resize();
	}
}