package com.mygdx.game;

import static com.mygdx.game.GameMechanic.isAdjacent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.Optional;

public class ListenerProvider {
    public static InputListener getCardsInPlayListener(Card card, Board board, Player player) {
        return new InputListener() {
            Vector2 offsets;
            Vector2 origins;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                origins = new Vector2(card.getX(), card.getY());
                offsets = card.stageToLocalCoordinates(new Vector2(event.getStageX(), event.getStageY()));
                card.setZIndex(10);
                card.setTouchable(Touchable.disabled);
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                card.moveBy(x - offsets.x, y - offsets.y);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                card.setZIndex(10);
                int originPosX = card.getPosX();
                int originPosY = card.getPosY();
                Vector2 hitCoordinate = board.stageToLocalCoordinates(new Vector2(event.getStageX(), event.getStageY()));
                CardSlot hitCardSlot = (CardSlot) board.cardSlotGroup.hit(hitCoordinate.x, hitCoordinate.y, false);
                Card hitCardOnBoard = (Card) board.cardOnBoardGroup.hit(hitCoordinate.x, hitCoordinate.y, true);
                if (hitCardSlot != null && card.isRed() && (originPosY != hitCardSlot.posY || originPosX != hitCardSlot.posX)
                        && (
                                (originPosX < 0 && hitCardOnBoard == null)
                             || (isAdjacent(originPosX, originPosY, hitCardSlot.posX, hitCardSlot.posY) && hitCardOnBoard == null)
                             || (originPosX >= 0 && hitCardOnBoard != null)
                )) {
                    if (hitCardOnBoard == null) {
                        board.moveCard(card, hitCardSlot.posX, hitCardSlot.posY);
                        if (originPosX < 0) {
                            player.act();
                        }
                    }
                    else {
                        hitCardOnBoard.setPos(-1, -1, board);
                        board.moveCard(card, hitCardSlot.posX, hitCardSlot.posY);
                        board.moveCard(hitCardOnBoard, originPosX, originPosY);
                        player.act();
                    }
                } else {
                    card.setX(origins.x);
                    card.setY(origins.y);
                }
                card.setTouchable(Touchable.enabled);
            }
        };
    }

    public static ClickListener getNextButtonListener(Group game, Board board, Deck deck, Player player) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Optional<Action> action = GameMechanic.letItFlow(board);
                if (action.isPresent()) {
                    board.setTouchable(Touchable.disabled);
                    game.addAction(Actions.sequence(action.get(), Actions.run(() -> board.setTouchable(Touchable.enabled))));
                } else {
                    board.setTouchable(Touchable.enabled);
                }
                deck.unusedToDiscardPile();
                deck.drawCard();
                player.reset();
            }
        };
    }
    public static ClickListener getResetButtonListener(Group game, Board board, Deck deck, Player player) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                board.newBoard().ifPresent(action -> {
                    game.setTouchable(Touchable.disabled);
                    game.addAction(Actions.sequence(action, Actions.run(() -> game.setTouchable(Touchable.enabled))));
                });
                deck.reset();
                player.reset();
                deck.drawCard();
            }
        };
    }
}
