package com.mygdx.game;

import static com.mygdx.game.GameMechanic.getCardsInPlayListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import java.util.ArrayDeque;
import java.util.Queue;

public class Board extends Group {
    static int TOTAL_ROW = 5;
    static int TOTAL_COLUMN = 3;
    static int CARD_PER_ROW = 6;
    Queue<Card>[] blackDeck = new ArrayDeque[TOTAL_ROW];
    final Group cardSlotGroup = new Group();
    final Group cardOnBoardGroup = new Group();
    final Rectangle[][] cardSlotBounds = new Rectangle[TOTAL_COLUMN][TOTAL_ROW];
    final Card[][] cardOnBoard = new Card[TOTAL_COLUMN][TOTAL_ROW];
    final Player player;

    public Board(Player player) {
        this.addActor(cardSlotGroup);
        this.addActor(cardOnBoardGroup);
        this.player = player;
    }

    public void initializeBoard() {
        cardSlotGroup.clearChildren();
        cardOnBoardGroup.clearChildren();
        for (int row = 0; row < TOTAL_ROW; row++) {
            blackDeck[row] = new ArrayDeque<>();
            for (int i = 0; i < CARD_PER_ROW; i++) {
                blackDeck[row].add(new Card(-1, -1, 2, cardOnBoard, player));
            }
        }
        float cardWidth = (float) getParent().getWidth() / TOTAL_ROW;
        float cardHeight = ((float) getParent().getHeight() * 0.6f / TOTAL_COLUMN);
        for (int col = 0; col < TOTAL_COLUMN; col++) {
            for (int row = 0; row < TOTAL_ROW; row++) {
                CardSlot cardslot = new CardSlot(row, col);
                Rectangle bound = new Rectangle(row * cardWidth, Gdx.graphics.getHeight() - (col + 1) * cardHeight, cardWidth, cardHeight);
                cardSlotBounds[col][row] = bound;
                cardSlotGroup.addActor(cardslot);
                cardslot.setBounds(bound.x, bound.y, bound.width, bound.height);

                if (col == TOTAL_COLUMN - 1) {
                    continue;
                }
                Card card = blackDeck[row].poll();
                card.setPos(row, col, cardOnBoard);
                card.setBounds(bound.x, getParent().getHeight() + 100, bound.getWidth(), bound.getHeight());
                MoveToAction moveAction = Actions.moveTo(bound.getX(), bound.getY(), 2f - col * 0.4f, Interpolation.sineOut);
                card.addAction(moveAction);
                card.addListener(getCardsInPlayListener(card, this, player));
                cardOnBoardGroup.addActor(card);
            }
        }
    }

    public void resize() {
        float cardWidth = (float) getParent().getWidth() / TOTAL_ROW;
        float cardHeight = ((float) getParent().getHeight() * 0.6f / TOTAL_COLUMN);
        for (Actor cardSlot : cardSlotGroup.getChildren()) {
            int row = ((CardSlot) cardSlot).posX;
            int col = ((CardSlot) cardSlot).posY;
            Rectangle bound = new Rectangle(row * cardWidth, Gdx.graphics.getHeight() - (col + 1) * cardHeight, cardWidth, cardHeight);
            cardSlotBounds[col][row] = bound;
            cardSlot.setBounds(bound.x, bound.y, bound.width, bound.height);
        }

        for (Actor card : cardOnBoardGroup.getChildren()) {
            int row = ((Card) card).getPosX();
            int col = ((Card) card).getPosY();
            if (row != -1) {
                Rectangle bound = cardSlotBounds[col][row];
                if (card.getY() == getParent().getHeight() + 100) {
                    MoveToAction moveAction = Actions.moveTo(bound.getX(), bound.getY(), 3f, Interpolation.sineOut);
                    card.addAction(moveAction);
                } else {
                    card.setBounds(bound.x, bound.y, bound.width, bound.height);
                }
            }
        }
    }

    public void removeCardFromBoard(Card card) {
        Rectangle bound = cardSlotBounds[card.getPosY()][card.getPosX()];
        MoveToAction moveAction = Actions.moveTo(bound.getX(), -1, 2f, Interpolation.sineOut);
        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(moveAction);
        sequenceAction.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                card.remove();
                return true;
            }
        });
        card.setPosX(-1, cardOnBoard);
        card.addAction(sequenceAction);
    }

    public void moveCard(Card card, int x, int y) {
        card.setPos(x, y, cardOnBoard);
        Rectangle bound = cardSlotBounds[y][x];
        MoveToAction moveAction = Actions.moveTo(bound.getX(), bound.getY(), 2f, Interpolation.sineOut);
        card.addAction(moveAction);
    }

    public Card getCardOnBoard(int row, int col) {
        return cardOnBoard[col][row];
    }
}
