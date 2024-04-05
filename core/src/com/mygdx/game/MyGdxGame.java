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
	public static int MAX_WIDTH = 600;
	public static int MAX_HEIGHT = 1000;
	public static int PLAYER_ACTION_COUNT = 10;
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
		int height = 30;
		Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
		TextButton dropButton = new TextButton("Action left: " + PLAYER_ACTION_COUNT, skin);
		dropButton.setBounds(game.getX(), 400, 200, height);
		dropButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				game.setTouchable(Touchable.disabled);
				GameMechanic.blackAttack(board);
				GameMechanic.redCaptureAttack(board);
				GameMechanic.drop(board, false);
				GameMechanic.drop(board, true);
				GameMechanic.fill(board, player);
				player.reset();
			}
		});
		TextButton blackAttackButton = new TextButton("Black Attack", skin);
		blackAttackButton.setBounds(game.getX(), dropButton.getY() - height, 200, height);
		blackAttackButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				GameMechanic.blackAttack(board);
			}
		});
		TextButton redAttackButton = new TextButton("Red Attack", skin);
		redAttackButton.setBounds(game.getX(), dropButton.getY() - height * 2, 200, height);
		redAttackButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				GameMechanic.redCaptureAttack(board);
			}
		});
		TextButton fillButton = new TextButton("Fill", skin);
		fillButton.setBounds(game.getX(), dropButton.getY() - height * 3, 200, height);
		fillButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				GameMechanic.drop(board, true);
				GameMechanic.fill(board, player);
			}
		});
		TextButton resetButton = new TextButton("Reset", skin);
		resetButton.setBounds(game.getX(), dropButton.getY() - height, 200, height);
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
		// game.addActor(blackAttackButton);
		// game.addActor(redAttackButton);
		// game.addActor(fillButton);
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