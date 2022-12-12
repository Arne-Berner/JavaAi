package de.fhkiel.ki.cathedral;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.io.TempDir;

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

    public static List<Position> getOwnedFields(Game game, Color ownedColor) {
        List<Position> ownedFieldPositions = new ArrayList<Position>();
        Color[][] fields = game.getBoard().getField();
        if (ownedColor == Color.Black) {
            ownedColor = Color.Black_Owned;
        } else {
            ownedColor = Color.White_Owned;
        }

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (fields[y][x] == ownedColor) {
                    ownedFieldPositions.add(new Position(x, y));
                }
            }
        }

        return ownedFieldPositions;
    }

    public static int getPlacementScore(
            Game tempGame, Placement goodPlacement, int score, Color playerColor) {

        tempGame.takeTurn(goodPlacement);
        int currentScore = tempGame.getPlayerScore(tempGame.getCurrentPlayer());

        if (currentScore == 0) {
            tempGame.undoLastTurn();
            return 0;
        }

        var goodPlacements = getAllPossiblePlacement(tempGame, playerColor, getOwnedFields(tempGame, playerColor));

        if (goodPlacements.size() == 0) {
            tempGame.undoLastTurn();
            return currentScore;
        }

        for (Placement placement : goodPlacements) {

            currentScore = getPlacementScore(tempGame, placement, score, playerColor);

            if (score > currentScore) {
                score = currentScore;
            }

            if (score == 0) {
                tempGame.undoLastTurn();
                return score;
            }
        }

        tempGame.undoLastTurn();
        return score;
    }

    public static Placement getBestPlacement(
            Game tempGame, List<Placement> goodPlacements, Color playerColor) {

        int score = 500;
        Placement bestPlacement = null;
        for (Placement goodPlacement : goodPlacements) {
            int currentScore = getPlacementScore(tempGame, goodPlacement, score, playerColor);

            if (score > currentScore) {
                bestPlacement = goodPlacement;
                score = currentScore;
            }

            if (score == 0) {
                return bestPlacement;
            }
        }

        return bestPlacement;
    }

    public static List<Placement> getAllPossiblePlacement(Game tempGame, Color playerColor, List<Position> freeFields) {
        List<Placement> goodPlacements = new ArrayList<Placement>();
        for (Building building : tempGame.getPlacableBuildings(playerColor)) {

            for (Placement placement : Utility.getPossiblePlacements(freeFields, building, tempGame)) {
                goodPlacements.add(placement);
            }
        }
        return goodPlacements;
    }

    public static List<Placement> getGoodPlacements(Game tempGame, Color playerColor) {
        // vielleicht brauche ich eine funktion "get player buildings" anstatt
        // getSortedBuildings

        List<Building> buildings = Utility.getSortedBuildingsForColor(tempGame, playerColor);
        Color[][] field = tempGame.getBoard().getField();
        List<Position> ownedFields = Utility.getOwnedFields(tempGame);
        List<Position> playerPlaced = Utility.placedByPlayer(field, tempGame.getCurrentPlayer());

        List<Placement> goodPlacements = new ArrayList<Placement>();
        int finalscore = 0;

        for (Building building : buildings) {
            List<Placement> possiblePlacements = Utility.getPossiblePlacements(ownedFields, building, tempGame);

            for (Placement possiblePlacement : possiblePlacements) {
                int placementScore = 0;

                for (Position corner : building.corners(possiblePlacement.direction())) {
                    // dummy
                    Position placementPosition = possiblePlacement.position();
                    Position actualPosition = placementPosition.plus(corner);

                    if (actualPosition.y() < 0 || actualPosition.y() > 9) {
                        placementScore++;
                    } else if (actualPosition.x() < 0 || actualPosition.x() > 9) {
                        placementScore++;
                    } else if (playerPlaced.contains(actualPosition)) {
                        placementScore++;
                    }

                }
                if (finalscore == placementScore) {
                    // dummy
                    goodPlacements.add(possiblePlacement);
                }

                if (finalscore < placementScore) {
                    // dummy
                    goodPlacements = new ArrayList<Placement>();
                    goodPlacements.add(possiblePlacement);
                    finalscore = placementScore;
                }
            }
        }

        return goodPlacements;
    }

    public static List<Placement> getPossiblePlacements(List<Position> ownedFields, Building building, Game tempGame) {
        List<Placement> possiblePositions = new ArrayList<Placement>();

        if (building.score() <= ownedFields.size()) {
            for (var direction : Direction.values()) {
                for (Position ownedField : ownedFields) {
                    Placement possiblePlacement = new Placement(ownedField, direction, building);
                    if (tempGame.takeTurn(possiblePlacement, true)) {
                        possiblePositions.add(possiblePlacement);
                        tempGame.undoLastTurn();
                    }

                }
            }
        }

        return possiblePositions;
    }

    public static List<Position> placedByPlayer(Color[][] field, Color playerColor) {
        List<Position> playerPlaced = new ArrayList<Position>();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (field[x][y] == playerColor) {
                    playerPlaced.add(new Position(x, y));
                }
            }
        }
        return playerPlaced;
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

    public static List<Building> getSortedBuildingsForColor(Game game, Color playerColor) {
        // TODO sehr teure sortierung
        List<Building> buildings = new ArrayList<Building>();
        for (int i = 5; i > 0; i--) {
            for (Building building : game.getPlacableBuildings()) {
                if (building.getColor() == playerColor) {
                    if (building.score() == i) {
                        buildings.add(building);
                    }
                }
            }
        }

        return buildings;
    }
}
