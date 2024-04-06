package com.mygdx.game;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.mygdx.game.Board.TOTAL_COLUMN;
import static com.mygdx.game.Board.TOTAL_ROW;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class GameMechanic {
    public final static Texture texture = new Texture(Gdx.files.internal("CuteCards.png"));
    public final static Texture chipTexture = new Texture(Gdx.files.internal("PokerChips.png"));
    public final static TextureRegion attackRegion = new TextureRegion(GameMechanic.chipTexture, 0, 76, 76, 76);
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public static void letItFlow(Board board, Player player) {
        GameMechanic.blackAttack(board);
        GameMechanic.slide(board,
                run(() -> {
                    GameMechanic.redCaptureAttack(board);
                    Gdx.app.error("flow", "fill");

                    GameMechanic.fill(board, player, null);
                }));
    }

    public static void slide(Board board, Action completeAction) {
        SequenceAction actions = Actions.sequence();
        for (int col = TOTAL_COLUMN - 2; col >= 0; col--) {
            for (int row = 0; row < TOTAL_ROW; row++) {
                Card card = board.getCardOnBoard(row, col);
                if (card != null) {
                    Card lowerCard = null;
                    int offset = 1;
                    while (lowerCard == null && col + offset <= TOTAL_COLUMN - 1) {
                        lowerCard = board.getCardOnBoard(row, col + offset);
                        offset += 1;
                    }
                    Action action;
                    if (lowerCard != null) {
                        action = board.moveCard(card, lowerCard.getPosX(), lowerCard.getPosY() - 1, true);
                    } else {
                        action = board.moveCard(card, card.getPosX(), TOTAL_COLUMN - 1, true);
                    }
                    if (action != null) {
                        actions.addAction(action);
                    }
                }
            }
        }
        actions.addAction(completeAction);
        board.clearActions();
        board.addAction(actions);
    }

    public static void fill(Board board, Player player, Action completeAction) {
        SequenceAction actions = Actions.sequence();
        for (int row = 0; row < TOTAL_ROW; row++) {
            for (int col = 0; col < TOTAL_COLUMN; col++) {
                Card card = board.getCardOnBoard(row, col);
                if (card != null) {
                    break;
                }
                Card newCard = board.blackDeck[row].poll();
                if (newCard == null) {
                    continue;
                }
                newCard.addListener(getCardsInPlayListener(newCard, board, player));
                board.cardOnBoardGroup.addActor(newCard);
                Rectangle bound = board.cardSlotBounds[col][row];
                newCard.setBounds(bound.x, board.getParent().getHeight() + 100, board.cardSlotBounds[0][0].width, board.cardSlotBounds[0][0].height);
                actions.addAction(board.moveCard(newCard, row, col, true));
                break;
            }
        }
        if (completeAction != null) actions.addAction(completeAction);
        board.addAction(actions);
    }

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

                if (hitCardSlot != null && (originPosX != hitCardSlot.posX || originPosY != hitCardSlot.posY)) {
                    if (hitCardOnBoard == null) {
                        board.moveCard(card, hitCardSlot.posX, hitCardSlot.posY, false);
                        player.act();
                        if (originPosX < 0) {
                            player.act();
                        }
                    } else if (card.isRed() && originPosX >= 0) {
                        hitCardOnBoard.setPosX(-1, board.cardOnBoard);
                        board.moveCard(card, hitCardSlot.posX, hitCardSlot.posY, false);
                        board.moveCard(hitCardOnBoard, originPosX, originPosY, false);
                    } else {
                        card.setX(origins.x);
                        card.setY(origins.y);
                    }
                } else {
                    card.setX(origins.x);
                    card.setY(origins.y);
                }
                card.setTouchable(Touchable.enabled);
            }
        };
    }

    public static void blackAttack(Board board) {
        for (int col = 0; col < TOTAL_COLUMN; col++) {
            for (int row = 0; row < TOTAL_ROW; row++) {
                Card card = board.getCardOnBoard(row, col);
                if (card != null && !card.isRed() && !card.faceDown) {
                    Point attackedPoint = card.getAttackDirection();
                    if (pointInBound(attackedPoint.x, attackedPoint.y)) {
                        Card attackedCard = board.getCardOnBoard(attackedPoint.x, attackedPoint.y);
                        if (attackedCard != null && attackedCard.isRed()) {
                            attackedCard.setValue(attackedCard.getValue() - card.getValue(), board.cardOnBoard);
                        }
                    }
                }
            }
        }
    }

    public static void redCaptureAttack(Board board) {
        Set<Point> visited = new HashSet<>();
        for (int col = 0; col < TOTAL_COLUMN; col++) {
            for (int row = 0; row < TOTAL_ROW; row++) {
                if (row == 2 && col == 0) continue;
                Card card = board.getCardOnBoard(row, col);
                if (card != null && !card.isRed() && !card.faceDown && !visited.contains(new Point(row, col))) {
                    for (Point blackIslandPoint : getBlackIsland(board, row, col, visited)) {
                        Card blackIslandCard = board.getCardOnBoard(blackIslandPoint.x, blackIslandPoint.y);
                        blackIslandCard.setValue(blackIslandCard.getValue() - 1, board.cardOnBoard);
                    }
                }
            }
        }
    }

    private static Collection<Point> getBlackIsland(Board board, int row, int col, Set<Point> visited) {
        Set<Point> islandIndex = new HashSet<>();

        Queue<Point> queue = new LinkedList<>();
        Point first = new Point(row, col);
        queue.offer(first);
        islandIndex.add(first);
        visited.add(first);
        boolean surrounded = true;

        while (!queue.isEmpty()) {
            Point curr = queue.poll();

            for (int[] dir : DIRECTIONS) {
                int newRow = curr.x + dir[0];
                int newCol = curr.y + dir[1];

                if (pointInBound(newRow, newCol)) {
                    Card neighborCard = board.getCardOnBoard(newRow, newCol);
                    Point newPoint = new Point(newRow, newCol);
                    if (!visited.contains(newPoint)) {
                        if (neighborCard == null || (newRow == 2 && newCol == 0)) {
                            surrounded = false;
                        } else if (!neighborCard.isRed()) {
                            if (neighborCard.faceDown) {
                                surrounded = false;
                            } else {
                                queue.offer(newPoint);
                                visited.add(newPoint);
                                islandIndex.add(newPoint);
                            }
                        }
                    }
                }
            }
        }
         return surrounded ? islandIndex : new HashSet<>();
    }

    public static boolean pointInBound(int row, int col) {
        return row >= 0 && row < TOTAL_ROW && col >= 0 && col < TOTAL_COLUMN;
    }
}
