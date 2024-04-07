package com.mygdx.game;

import static com.mygdx.game.MyGdxGame.TOTAL_COLUMN;
import static com.mygdx.game.MyGdxGame.TOTAL_ROW;
import static com.mygdx.game.Textures.chipTexture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class GameMechanic {
    public final static TextureRegion attackRegion = new TextureRegion(chipTexture, 0, 76, 76, 76);
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public static Optional<Action> letItFlow(Board board) {
        GameMechanic.blackAttack(board);
        GameMechanic.redCaptureAttack(board);
        return GameMechanic.fill(board);
    }

    public static Optional<Action> fill(Board board) {
        Random random = new Random();
        List<Point> emptyPoints = new ArrayList<>();
        for (int col = 0; col < TOTAL_COLUMN; col++) {
            for (int row = 0; row < TOTAL_ROW; row++) {
                if (board.getCardOnBoard(row, col) == null) {
                    emptyPoints.add(new Point(row, col));
                }
            }
        }
        if (emptyPoints.isEmpty()) {
            return Optional.empty();
        }
        Point newSpawnPoint = emptyPoints.get(random.nextInt(emptyPoints.size()));
        return board.spawnNewBlackCard(newSpawnPoint.x, newSpawnPoint.y);
    }

    public static void blackAttack(Board board) {
        for (int col = 0; col < TOTAL_COLUMN; col++) {
            for (int row = 0; row < TOTAL_ROW; row++) {
                boolean attacked = false;
                Card card = board.getCardOnBoard(row, col);
                // attack
                if (card != null && !card.isRed() && !card.faceDown) {
                    Point attackedPoint = card.getAttackDirection();
                    if (attackedPoint != null && pointInBound(attackedPoint.x, attackedPoint.y)) {
                        Card attackedCard = board.getCardOnBoard(attackedPoint.x, attackedPoint.y);
                        if (attackedCard != null && attackedCard.isRed()) {
                            attackedCard.setValue(attackedCard.getValue() - card.getValue(), board.cardOnBoard);
                            attacked = true;
                        }
                    }
                }
                // reveal
                if (card != null && !card.isRed() && !attacked) {
                    for (int[] dir : DIRECTIONS) {
                        int newRow = card.getPosX() + dir[0];
                        int newCol = card.getPosY() + dir[1];
                        if (pointInBound(newRow, newCol)) {
                            Card neighborCard = board.getCardOnBoard(newRow, newCol);
                            if (neighborCard != null && neighborCard.isRed()) {
                                card.faceDown = false;
                                card.setAttackFace(newRow, newCol);
                                break;
                            }
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

    public static boolean pointInBound(int row, int col) {
        return row >= 0 && row < TOTAL_ROW && col >= 0 && col < TOTAL_COLUMN;
    }

    public static boolean isAdjacent(int row, int col, int newRow, int newCol) {
        if ((row == newRow + 1 || row == newRow - 1) && col == newCol) {
            return true;
        }
        if ((col == newCol + 1 || col == newCol - 1) && row == newRow) {
            return true;
        }
        return false;
    }
}
