package com.mygdx.game;

import static com.mygdx.game.Textures.backgroundTexture;
import static java.lang.Math.min;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
todo: move action, attack direction redirect to first adjacent red
 */
public class MyGdxGame extends ApplicationAdapter {
	public static int MAX_WIDTH = 1800;
	public static int MAX_HEIGHT = 3200;
	public static float BOARD_PORTION = 0.24f;
	public static int PADDING = 100;
	public static int PLAYER_ACTION_COUNT = 1;
	public static int TOTAL_ROW = 4;
	public static int TOTAL_COLUMN = 4;
	public static float BUTTON_SIZE = 100;
	public static Map<Integer, Integer> HAND_DECK_VALUE = Stream.of(new Integer[][] {
			{ 1, 5 },
			{ 2, 3 },
	}).collect(Collectors.toMap(data -> data[0], data -> data[1]));

	Stage stage;
	Group game;
	Board board;
	Image stageImage;
	Deck deck;
	Player player;
	Ui ui;

	@Override
	public void create() {
		stage = new Stage(new ExtendViewport(1080, 2040));
		stageImage = new Image(backgroundTexture);
		stage.addActor(stageImage);
		Gdx.input.setInputProcessor(stage);


		// game
		game = new Group();
		setGameBounds();
		stage.addActor(game);

		// board
		board = new Board(player);
		setBoardBounds();
		game.addActor(board);
		Optional<Action> actionOptional = board.init();
		actionOptional.ifPresent(action -> {
			game.setTouchable(Touchable.disabled);
			game.addAction(Actions.sequence(action, Actions.run(() -> game.setTouchable(Touchable.enabled))));
		});

		// ui
		ui = new Ui(BUTTON_SIZE, BUTTON_SIZE * 2);
		setUiBounds();
		player = new Player(PLAYER_ACTION_COUNT, ui.nextButton, board);
		game.addActor(ui);

		// deck
		deck = new Deck(board, player);
		deck.setY(BUTTON_SIZE);
		game.addActor(deck);
		setDeckBounds();
		deck.reset();
		deck.drawCard();

		ui.nextButton.addListener(ListenerProvider.getNextButtonListener(game, board, deck, player));
		ui.resetButton.addListener(ListenerProvider.getResetButtonListener(game, board, deck, player));
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public void render() {
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		stageImage.setBounds(0, 0, stage.getWidth(), stage.getHeight());
		setGameBounds();
		setBoardBounds();
		setUiBounds();
		setDeckBounds();
	}

	public void setGameBounds() {
		game.setBounds(stage.getWidth()/2f - getGameWidth()/2f,0, getGameWidth(), min(stage.getHeight(), MAX_HEIGHT) - PADDING);
	}

	public void setBoardBounds() {
		board.setBounds(0, game.getHeight() * BOARD_PORTION, game.getWidth(), game.getHeight() * (0.85f - BOARD_PORTION));
	}

	public void setUiBounds() {
		ui.setBounds(0, 0, game.getWidth(), BUTTON_SIZE);
	}

	public void setDeckBounds() {
		deck.setBounds(0, BUTTON_SIZE, game.getWidth(), game.getHeight() * BOARD_PORTION - BUTTON_SIZE - PADDING/4f);
	}

	public float getGameWidth() {
		return min(stage.getWidth() - PADDING/4f, MAX_WIDTH);
	}
}