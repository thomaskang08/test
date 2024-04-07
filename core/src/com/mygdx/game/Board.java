package com.mygdx.game;

import static com.mygdx.game.CardSlot.padding;
import static com.mygdx.game.GameMechanic.fill;
import static com.mygdx.game.ListenerProvider.getCardsInPlayListener;
import static com.mygdx.game.MyGdxGame.TOTAL_COLUMN;
import static com.mygdx.game.MyGdxGame.TOTAL_ROW;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public class Board extends Group {
    static int TOTAL_BLACK_CARDS = 10;
    Queue<Card> blackDeck = new ArrayDeque<>();
    final Group cardSlotGroup = new Group();
    final Group cardOnBoardGroup = new Group();
    final Rectangle[][] cardSlotBounds = new Rectangle[TOTAL_COLUMN][TOTAL_ROW];
    Card[][] cardOnBoard = new Card[TOTAL_COLUMN][TOTAL_ROW];
    final Player player;

    public Board(Player player) {
        this.addActor(cardSlotGroup);
        this.addActor(cardOnBoardGroup);
        this.player = player;
    }

    public void resetBlackDeck() {
        for (int i = 0; i < TOTAL_BLACK_CARDS; i++) {
            Card card = new Card(-1, -1, 2, 1, player);
            card.getColor().a = 0;
            card.addListener(getCardsInPlayListener(card, this, player));
            blackDeck.add(card);
        }
    }

    public void setNewCardSlotBounds() {
        for (int col = 0; col < TOTAL_COLUMN; col++) {
            for (int row = 0; row < TOTAL_ROW; row++) {
                CardSlot cardslot = new CardSlot(row, col);
                Rectangle bound = new Rectangle(row * getCardWidth(), this.getHeight() - (col + 1) * getCardHeight(), getCardWidth(), getCardHeight());
                cardSlotBounds[col][row] = bound;
                cardSlotGroup.addActor(cardslot);
                cardslot.setBounds(bound.x, bound.y, bound.width, bound.height);
            }
        }
    }

    public Optional<Action> spawnNewBlackCard(int row, int col) {
        Rectangle bound = cardSlotBounds[col][row];
        Card card = blackDeck.poll();
        if (card != null) {
            card.setBounds(padding + bound.x, padding + getParent().getHeight() + 100, getCardWidth() - padding, getCardHeight() - padding);
            cardOnBoardGroup.addActor(card);
            moveCard(card, row, col);
            Action action = Actions.alpha(1);
            action.setActor(card);
            return Optional.of(action);
        }
        return Optional.empty();
    }

    public Optional<Action> init() {
        setNewCardSlotBounds();
        return newBoard();
    }

    public Optional<Action> newBoard() {
        cardOnBoardGroup.clearChildren();
        cardOnBoard = new Card[TOTAL_COLUMN][TOTAL_ROW];
        resetBlackDeck();
        return fill(this);
    }

    public void moveCard(Card card, int x, int y) {
        if (card.getPosX() == x && card.getPosY() == y) {
            return;
        }
        card.setPos(x, y, this);
        Rectangle bounds = cardSlotBounds[y][x];
        card.setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }

    public Card getCardOnBoard(int row, int col) {
        return cardOnBoard[col][row];
    }

    public float getCardWidth() {
        return (float) this.getWidth() / TOTAL_ROW;
    }

    public float getCardHeight() {
        return (float) this.getHeight() / TOTAL_COLUMN;
    }
}
