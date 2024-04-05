package com.mygdx.game;

import static com.mygdx.game.GameMechanic.getCardsInPlayListener;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import java.util.Stack;

public class Deck extends Group {
    Stack<Card> handPile = new Stack<>();
    Card playedCard;
    Board board;
    Player player;
    int cardN;

    InputListener inputListener = new InputListener(){
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            Deck.this.drawCard();
            return true;
        }
    };

    public Deck(int cardN, Board board, Player player) {
        this.board = board;
        this.player = player;
        this.cardN = cardN;
        reset();
    }

    public void drawCard() {
        playedCard = handPile.pop();
        playedCard.removeListener(inputListener);
        playedCard.setX(playedCard.getX() + playedCard.getWidth());
        board.cardOnBoardGroup.addActor(playedCard);
        playedCard.addListener(getCardsInPlayListener(playedCard, board, player));
    }

    public void reset() {
        this.clearChildren();
        for (int i = 0; i < cardN; i++) {
            Card card = new Card(-1, -1, 3, board.cardOnBoard, player);
            Rectangle bound = board.cardSlotBounds[0][0];
            card.setBounds(0,0, bound.getWidth(), bound.getHeight());
            card.addListener(inputListener);
            handPile.add(card);
            this.addActor(card);
        }
    }
}
