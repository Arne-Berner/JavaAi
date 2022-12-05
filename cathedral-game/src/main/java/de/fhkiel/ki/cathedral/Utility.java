package de.fhkiel.ki.cathedral;

import java.util.ArrayList;
import java.util.List;

import de.fhkiel.ki.cathedral.game.Building;
import de.fhkiel.ki.cathedral.game.Color;
import de.fhkiel.ki.cathedral.game.Direction;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;
import de.fhkiel.ki.cathedral.game.Position;

public class Utility {
    public static List<Position> getFreeFields(Game game) {
        List<Position> freeFieldPositions = new ArrayList<Position>();
        Color[][] fields = game.getBoard().getField();

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (fields[y][x] == Color.None) {
                    freeFieldPositions.add(new Position(x, y));
                }
            }
        }

        return freeFieldPositions;
    }

    public static List<Position> getBlackFields(Game game) {
        List<Position> blackFieldPositions = new ArrayList<Position>();
        Color[][] fields = game.getBoard().getField();

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (fields[y][x] == Color.None) {
                    blackFieldPositions.add(new Position(x, y));
                }
            }
        }

        return blackFieldPositions;
    }

    public static List<Position> getWhiteFields(Game game) {
        List<Position> whiteFieldPositions = new ArrayList<Position>();
        Color[][] fields = game.getBoard().getField();

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (fields[y][x] == Color.None) {
                    whiteFieldPositions.add(new Position(x, y));
                }
            }
        }

        return whiteFieldPositions;
    }

    /**
     * gets the evaluated Score for the last Turn of current Player
     * 
     * @return Score as int
     */
    public static int getLastTurnScore(Game game) {
        Game tempGame = game.copy();
        boolean isFillPhase = Utility.isFillphase(tempGame);
        int turnScore = 0;

        if (!isFillPhase) {

            Color currentPlayer = tempGame.getCurrentPlayer();
            Color enemyPlayer = tempGame.getEnemyPlayer();

            // nach dem letzten zug
            tempGame.undoLastTurn();
            List<Position> freeFields = Utility.getFreeFields(tempGame);
            int turns = freeFields.size();
            int ownScore = tempGame.getPlayerScore(currentPlayer);
            int enemyScore = tempGame.getPlayerScore(enemyPlayer);
            int enemyBuildingScore = getPlaceAbleBuildingScore(tempGame, enemyPlayer);

            // vor dem letzten zug
            tempGame.undoLastTurn();
            List<Position> oldFreeFields = Utility.getFreeFields(tempGame);
            int oldTurns = oldFreeFields.size();
            int oldOwnScore = tempGame.getPlayerScore(currentPlayer);
            int oldEnemyScore = tempGame.getPlayerScore(enemyPlayer);
            int oldEnemyBuildingScore = getPlaceAbleBuildingScore(tempGame, enemyPlayer);

            // muss alles moeglichst hoch sein
            // wird doppelt gezaehlt durch die flaeche die eingenommen wird
            float ownScoreDiff = (oldOwnScore - ownScore);
            int enemyScoreDiff = (enemyScore - oldEnemyScore);
            int enemyTurnDiff = oldTurns - turns;
            int enemyBuildingScoreDiff = oldEnemyBuildingScore - enemyBuildingScore + (enemyScoreDiff);

            turnScore = (int) ownScoreDiff + enemyScoreDiff + enemyTurnDiff + enemyBuildingScoreDiff;
        }

        return turnScore;
    }

    public static int getPlaceAbleBuildingScore(Game game, Color player) {
        List<Building> buildings = game.getPlacableBuildings(player);
        int buildingScore = 0;

        for (Building building : buildings) {
            var possiblePlacements = building.getPossiblePlacements(game);
            if (possiblePlacements.size() != 0) {
                buildingScore += building.score();
            }
        }

        return buildingScore;
    }

    public static boolean isFillphase(Game tempGame) {

        List<Position> freeFields = Utility.getFreeFields(tempGame);
        List<Building> buildings = tempGame.getPlacableBuildings(tempGame.getCurrentPlayer());
        boolean isFillphase = !canPlaceInPositionList(tempGame, freeFields, buildings);

        return isFillphase;
    }

    private static boolean canPlaceInPositionList(Game tempGame, List<Position> freeFields, List<Building> buildings) {

        tempGame.ignoreRules(true);

        for (Building building : buildings) {
            for (Position field : freeFields) {
                Placement possiblePlacement = new Placement(field, Direction._0, building);
                if (tempGame.takeTurn(possiblePlacement, true)) {
                    tempGame.undoLastTurn();
                    return true;
                }
            }
        }

        tempGame.ignoreRules(false);

        return false;
    }

    public static List<Building> getSortedBuildings(Game game) {
        List<Building> buildings = new ArrayList<Building>();
        for (int i = 5; i > 0; i--) {
            for (Building building : game.getPlacableBuildings()) {
                if (building.score() == i) {
                    buildings.add(building);
                }
            }
        }

        return buildings;
    }
}
