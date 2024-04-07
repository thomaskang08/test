package com.mygdx.game;

import static com.mygdx.game.ListenerProvider.getCardsInPlayListener;
import static com.mygdx.game.MyGdxGame.HAND_DECK_VALUE;

import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Deck extends Group {
    Stack<Card> handPile;
    Stack<Card> discardPile;
    Board board;
    Player player;
    List<Card> drawnCards;

    public Deck(Board board, Player player) {
        this.board = board;
        this.player = player;
    }

    public void drawCard() {
        for (int i = 0; i < 3; i++) {
            if (handPile.isEmpty()) {
                shuffleDiscardToHand();
            }
            if (handPile.isEmpty()) {
                return;
            }
            Card playedCard = handPile.pop();
            playedCard.setBounds(getHeight() * 0.85f * i,0, getHeight() * 0.8f, getHeight() * 0.8f);
            playedCard.clearListeners();
            playedCard.addListener(getCardsInPlayListener(playedCard, board, player));
            drawnCards.add(playedCard);
        }
    }

    public void unusedToDiscardPile() {
        drawnCards.stream().filter(card -> card.getPosX() == -1).forEach(card -> {
            discardPile.add(card);
            card.remove();
        });
        drawnCards.clear();
    }

    public void shuffleDiscardToHand() {
        Collections.shuffle(discardPile);
        while (!discardPile.isEmpty()) {
            handPile.add(discardPile.pop());
        }
        handPile.forEach(card -> {
            card.setBounds(0,0, getHeight() * 0.8f, getHeight() * 0.8f);
            this.addActor(card);
        });
    }

    public void reset() {
        this.clearChildren();
        handPile = new Stack<>();
        discardPile = new Stack<>();
        drawnCards = new ArrayList<>();
        HAND_DECK_VALUE.forEach((value, count) -> {
            for (int i = 0; i < count; i++) {
                Card card = new Card(-1, -1, 3, value, player);
                card.faceDown = false;
                handPile.add(card);
                this.addActor(card);
            }
        });
        Collections.shuffle(handPile);
    }
}
