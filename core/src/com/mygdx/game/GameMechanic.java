package com.mygdx.game;

import static com.mygdx.game.Board.TOTAL_COLUMN;
import static com.mygdx.game.Board.TOTAL_ROW;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class GameMechanic {
    public final static Texture texture = new Texture(Gdx.files.internal("CuteCards.png"));
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};


    public static void drop(Board board, boolean faceDown) {
        for (int col = TOTAL_COLUMN - 1; col >= 0; col--) {
            for (int row = 0; row < TOTAL_ROW; row++) {
                Card card = board.getCardOnBoard(row, col);
                if (card != null && !card.isRed() && faceDown == card.faceDown) {
                    if (col == TOTAL_COLUMN - 1) {
                        board.removeCardFromBoard(card);
                    } else {
                        Card lowerCard = board.getCardOnBoard(row, col + 1);
                        if (lowerCard == null) {
                            board.moveCard(card, row, col + 1);
                        }
                    }
                }
            }
        }
    }

    public static void slide(Board board) {
        for (int col = TOTAL_COLUMN - 1; col >= 0; col--) {
            for (int row = 0; row < TOTAL_ROW; row++) {
                Card card = board.getCardOnBoard(row, col);
                if (card != null && !card.isRed() && card.faceDown) {
                    if (col == TOTAL_COLUMN - 1) {
                        board.removeCardFromBoard(card);
                    } else {
                        Card lowerCard = null;
                        int offset = 1;
                        while (lowerCard == null && col + offset <= TOTAL_COLUMN - 1) {
                            lowerCard = board.getCardOnBoard(row, col + offset);
                            offset += 1;
                        }

                        if (lowerCard != null) {
                            board.moveCard(card, lowerCard.getPosX(), lowerCard.getPosY() - 1);
                        } else {
                            board.removeCardFromBoard(card);
                        }
                    }
                }
            }
        }
    }

    public static void fill(Board board, Player player) {
        for (int col = 0; col < TOTAL_COLUMN - 1; col++) {
            for (int row = 0; row < TOTAL_ROW; row++) {
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
                board.moveCard(newCard, row, col);
            }
        }
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
                Actor hitCardOnBoard = board.cardOnBoardGroup.hit(hitCoordinate.x, hitCoordinate.y, true);

                if (hitCardSlot != null && hitCardOnBoard == null && (originPosX != hitCardSlot.posX || originPosY != hitCardSlot.posY)) {
                    card.setPos(hitCardSlot.posX, hitCardSlot.posY, board.cardOnBoard);
                    Rectangle bounds = board.cardSlotBounds[card.getPosY()][card.getPosX()];
                    card.setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
                    player.act();
                    if (originPosX < 0) {
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

    public static void blackAttack(Board board) {
        for (int col = 0; col < TOTAL_COLUMN - 1; col++) {
            for (int row = 0; row < TOTAL_ROW; row++) {
                Card card = board.getCardOnBoard(row, col);
                if (card != null && !card.isRed() && !card.faceDown) {
                    Card lowerCard = board.getCardOnBoard(row, col + 1);
                    if (lowerCard != null && lowerCard.isRed()) {
                        lowerCard.setValue(lowerCard.getValue() - card.getValue(), board.cardOnBoard);
                    }
                }
            }
        }
    }

    public static void redAttack(Board board) {
        for (int col = 0; col < TOTAL_COLUMN; col++) {
            for (int row = 0; row < TOTAL_ROW; row++) {
                Card card = board.getCardOnBoard(row, col);
                if (card != null && !card.isRed() && !card.faceDown) {
                    boolean surrounded = true;
                    Point[] adjacent = new Point[] {
                            new Point(row + 1, col),
                            new Point(row - 1, col),
                            new Point(row, row + 1),
                            new Point(row, row - 1),
                    };
                    for (Point neighbor : adjacent) {
                        if (neighbor.x >= 0 && neighbor.x < TOTAL_ROW && neighbor.y >= 0 && neighbor.y < TOTAL_COLUMN) {
                            Card neighborCard = board.getCardOnBoard(neighbor.x, neighbor.y);
                            if (neighborCard == null || !neighborCard.isRed()) {
                                surrounded = false;
                                break;
                            }
                        }
                    }
                    if (surrounded) {
                        card.setValue(card.getValue() - 1, board.cardOnBoard);
                    }
                }
            }
        }
    }

    public static void redCaptureAttack(Board board) {
        List<Integer> islandSizes = new ArrayList<>();
        Set<Point> visited = new HashSet<>();
        for (int col = 0; col < TOTAL_COLUMN; col++) {
            for (int row = 0; row < TOTAL_ROW; row++) {
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
        int rows = TOTAL_ROW;
        int cols = TOTAL_COLUMN;

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

                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                    Card neighborCard = board.getCardOnBoard(newRow, newCol);
                    Point newPoint = new Point(newRow, newCol);
                    if (!visited.contains(newPoint)) {
                        if (neighborCard == null) {
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
}
