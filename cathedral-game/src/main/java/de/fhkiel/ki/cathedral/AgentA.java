package de.fhkiel.ki.cathedral;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JComponent;

import org.junit.jupiter.api.extension.Extensions;

import com.fasterxml.jackson.datatype.jdk8.PackageVersion;

import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.Building;
import de.fhkiel.ki.cathedral.game.Color;
import de.fhkiel.ki.cathedral.game.Direction;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;
import de.fhkiel.ki.cathedral.game.Position;
import de.fhkiel.ki.cathedral.gui.CathedralGUI;
import de.fhkiel.ki.cathedral.gui.Settings;

public class AgentA implements Agent {

    public static void main(String[] args) {
        CathedralGUI.start(Settings.Builder()
                .token("MTAzNDA4MjI4NTAyMjEwMTU2NA.G4LMpA.UUuWQC_AYk5UqhuJihNg4nYSXcHOu1JYfBU5mU")
                .build(), new AgentA());
    }

    private PrintStream console;
    private Position lastPosition = new Position(3, 3);

    @Override
    public String name() {
        return "Improved Agent";
    }

    @Override
    public void initialize(Game game, PrintStream console) {
        this.console = console;
    }

    @Override
    public Optional<JComponent> guiElement() {
        return Optional.empty();
    }

    @Override
    public Optional<Placement> calculateTurn(Game game, int timeForTurn, int timeBonus) {
        List<Placement> possiblePlacements = new ArrayList<>();
        Game tempGame = game.copy();
        boolean placed = false;
        firstTurn(tempGame, possiblePlacements);
        List<Building> buildings = getSortedBuildings(game);

        for (Building building : buildings) {
            boolean gridloop = true;
            boolean goingDown = true;
            Position possiblePosition = new Position(lastPosition.x(), lastPosition.y());
            int y = possiblePosition.y();
            int x = possiblePosition.x();
            while (gridloop) {
                if (y < 9 && goingDown) {
                    ++y;
                } else if (y > 0) {
                    --y;
                    goingDown = false;
                } else {
                    gridloop = false;
                }
                possiblePosition = new Position(x, y);
                if (placed == false) {
                    for (Direction direction : building.getTurnable().getPossibleDirections()) {
                        Placement possiblePlacement = new Placement(possiblePosition, direction, building);
                        if (tempGame.takeTurn(possiblePlacement, true)) {
                            possiblePlacements.add(possiblePlacement);
                            tempGame.undoLastTurn();
                            placed = true;
                        }
                    }
                }
            }
            gridloop = true;
        }

        if (possiblePlacements.isEmpty())
            console.println("WIR SIND IM X SEKTOR");
        for (Building building : buildings) {
            boolean gridloop = true;
            boolean goingRight = true;
            boolean goingLeft = true;
            Position possiblePosition = new Position(lastPosition.x(), lastPosition.y());
            int y = possiblePosition.y();
            int x = possiblePosition.x();
            while (gridloop) {
                if (x < 9 && goingRight) {
                    ++x;
                } else if (x > 0 && goingLeft) {
                    --x;
                    goingRight = false;
                } else {
                    gridloop = false;
                }
                possiblePosition = new Position(x, y);
                if (placed == false) {
                    for (Direction direction : building.getTurnable().getPossibleDirections()) {
                        Placement possiblePlacement = new Placement(possiblePosition, direction, building);
                        if (tempGame.takeTurn(possiblePlacement, true)) {
                            possiblePlacements.add(possiblePlacement);
                            tempGame.undoLastTurn();
                            placed = true;
                        }
                    }
                }
            }
            gridloop = true;
        }

        if (possiblePlacements.isEmpty()) {
            console.println("Nu isses random...");
            for (Building building : game.getPlacableBuildings()) {
                for (int y = 0; y < 10; ++y) {
                    for (int x = 0; x < 10; ++x) {
                        for (Direction direction : building.getTurnable().getPossibleDirections()) {
                            Placement possiblePlacement = new Placement(x, y, direction, building);
                            if (tempGame.takeTurn(possiblePlacement, true)) {
                                possiblePlacements.add(possiblePlacement);
                                tempGame.undoLastTurn();
                            }
                        }
                    }
                }
            }
        }

        console.println("Anzahl möglicher Züge: " + possiblePlacements.size());

        console.println(possiblePlacements.get(0).position().toString());
        // sollte eigentlich die position des letzten zugs nehmen, funktioniert aber
        // nicht
        lastPosition = possiblePlacements.get(0).position();
        if (possiblePlacements.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional
                    // .of(new ArrayList<>(possiblePlacements).get(new
                    // Random().nextInt(possiblePlacements.size())));
                    .of(possiblePlacements.get(0));
        }
    }

    /**
     * gets the evaluated Score for the last Turn of current Player
     * 
     * @return Score as int
     */
    private int getLastTurnScore(Game game) {
        Game tempGame = game.copy();

        Color currentPlayer = tempGame.getCurrentPlayer();
        Color enemyPlayer = tempGame.getEnemyPlayer();
        // nach dem letzten zug
        tempGame.undoLastTurn();
        int enemyTurns = getTurnCount(tempGame, enemyPlayer);
        int ownScore = tempGame.getPlayerScore(currentPlayer);
        int enemyScore = tempGame.getPlayerScore(enemyPlayer);
        int enemyBuildingScore = getPlaceAbleBuildingScore(tempGame, enemyPlayer);

        // vor dem letzten zug
        tempGame.undoLastTurn();
        int oldEnemyTurns = getTurnCount(tempGame, enemyPlayer);
        int oldOwnScore = tempGame.getPlayerScore(currentPlayer);
        int oldEnemyScore = tempGame.getPlayerScore(enemyPlayer);
        int oldEnemyBuildingScore = getPlaceAbleBuildingScore(tempGame, enemyPlayer);

        // muss alles moeglichst hoch sein
        float ownScoreDiff = (oldOwnScore - ownScore) * 1.5f;
        int enemyScoreDiff = (enemyScore - oldEnemyScore) * 2;
        int enemyTurnDiff = oldEnemyTurns - enemyTurns;
        // cancels enemyturndiff?
        int enemyBuildingScoreDiff = oldEnemyBuildingScore - enemyBuildingScore + (enemyScoreDiff/2);

        int turnScore = (int) ownScoreDiff + enemyScoreDiff + enemyTurnDiff + enemyBuildingScoreDiff;

        return turnScore;
    }

    @Override
    public String evaluateLastTurn(Game game) {
        return Integer.toString(getLastTurnScore(game));
    }

    @Override
    public void stop() {

    }

    private Position tryTopLeftCorner(Game tempGame, Direction direction, Building building) {
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                var position = new Position(x, y);
                Placement possiblePlacement = new Placement(position, direction, building);
                if (tempGame.takeTurn(possiblePlacement, true)) {
                    return position;
                }
            }
        }
        return null;
    }

    private Position tryBottomRightCorner(Game tempGame, Direction direction, Building building) {
        for (int y = 9; y > 7; y--) {
            for (int x = 9; x > 7; x--) {
                var position = new Position(x, y);
                Placement possiblePlacement = new Placement(position, direction, building);
                if (tempGame.takeTurn(possiblePlacement, true)) {
                    return position;
                }
            }
        }
        return null;
    }

    private List<Placement> getConnectingPlacement(Game tempGame, List<Building> buildings) {
        List<Placement> connectingPlacements = new ArrayList<Placement>();
        Direction[] directions = Direction.values();
        for (Building building : buildings) {
            for (int i = 0; i < 4; i++) {
                Position topleft = tryTopLeftCorner(tempGame, directions[i], building);
                Position bottomright = tryBottomRightCorner(tempGame, directions[i], building);

                for (int x = topleft.x(); x <= bottomright.x(); x++) {
                    connectingPlacements.add(new Placement(new Position(x, topleft.y()), directions[i], building));
                    connectingPlacements.add(new Placement(new Position(x, bottomright.y()), directions[i], building));
                }

                for (int y = topleft.y() + 1; y < bottomright.y(); y++) {
                    connectingPlacements.add(new Placement(new Position(topleft.x(), y), directions[i], building));
                    connectingPlacements.add(new Placement(new Position(bottomright.x(), y), directions[i], building));
                }

            }
        }

        // speicher die relativen Punkte des Gegners: moegliche Zuege - jetzige punkte

        // (vielleicht noch unsere eigenen relativen punkte mit einberechnen,
        // dadurch werden automatisch groessere Steine genommen, wenn moeglich
        // [vielleicht die eigenen
        // punkte die verschwinden sogar mal ])
        // differenz = relativeEnemyScore - newEnemyScore sollte moeglichst gross sein
        // für jede Position in fourDirectionPositions wird der move mit der direction
        // ausgeführt
        // dann wird erstmal geschaut, ob sich die gegnerischen punkte diesen zug
        // erhöhen lassen
        // dann wird der zug gewählt der die größte differenz hat
        // kann man nichts vom gegner entfernen
        // wird erstmal ein Zug gesetzt der an einem anderen Stein angrenzt
        // und ein stein der an der Wand angrenzt als nächstes
        // diese Kombination wird wieder als Differenz ausgezählt
        // die höchste Differenz wird als Zug wirklich gesetzt

        // Es kann mit dem Füllen begonnen werden, wenn es keinen Zug gibt, der dem
        // gegner Zugmöglichkeiten klaut.

        return connectingPlacements;
    }

    private int placeAbleBuildingCount(Game game, Color playerColor) {
        var tempGame = game.copy();

        return 0;
    }

    private void firstTurn(Game tempGame, List<Placement> possiblePlacements) {
        // Erster zug!
        for (Building neuner : tempGame.getPlacableBuildings()) {
            if (neuner.getId() == 9) {
                Position cathedralPosition = tempGame.getBoard().getPlacedBuildings().get(0).position();
                if (cathedralPosition.x() < 5) {
                    // nur rechte positionen setzen
                    var possiblePosition = new Position(6, 4);
                    Placement possiblePlacement = new Placement(possiblePosition, Direction._90, neuner);
                    if (tempGame.takeTurn(possiblePlacement, true)) {
                        possiblePlacements.add(possiblePlacement);
                    }

                    possiblePosition = new Position(6, 5);
                    possiblePlacement = new Placement(possiblePosition, Direction._90, neuner);
                    if (tempGame.takeTurn(possiblePlacement, true)) {
                        possiblePlacements.add(possiblePlacement);
                    }
                }
                if (cathedralPosition.x() > 4) {
                    // nur linke positionen setzen
                    var possiblePosition = new Position(3, 4);
                    Placement possiblePlacement = new Placement(possiblePosition, Direction._270, neuner);
                    if (tempGame.takeTurn(possiblePlacement, true)) {
                        possiblePlacements.add(possiblePlacement);
                    }

                    possiblePosition = new Position(3, 5);
                    possiblePlacement = new Placement(possiblePosition, Direction._270, neuner);
                    if (tempGame.takeTurn(possiblePlacement, true)) {
                        possiblePlacements.add(possiblePlacement);
                    }
                }
                if (cathedralPosition.y() < 5) {
                    // nur untere positionen setzen
                    var possiblePosition = new Position(4, 6);
                    Placement possiblePlacement = new Placement(possiblePosition, Direction._180, neuner);
                    if (tempGame.takeTurn(possiblePlacement, true)) {
                        possiblePlacements.add(possiblePlacement);
                    }

                    possiblePosition = new Position(5, 6);
                    possiblePlacement = new Placement(possiblePosition, Direction._180, neuner);
                    if (tempGame.takeTurn(possiblePlacement, true)) {
                        possiblePlacements.add(possiblePlacement);
                    }
                }
                if (cathedralPosition.y() > 4) {
                    // nur obere positionen setzen
                    var possiblePosition = new Position(4, 3);
                    Placement possiblePlacement = new Placement(possiblePosition, Direction._0, neuner);
                    if (tempGame.takeTurn(possiblePlacement, true)) {
                        possiblePlacements.add(possiblePlacement);
                    }

                    possiblePosition = new Position(5, 3);
                    possiblePlacement = new Placement(possiblePosition, Direction._0, neuner);
                    if (tempGame.takeTurn(possiblePlacement, true)) {
                        possiblePlacements.add(possiblePlacement);
                    }
                }
            }
        }

    }

    private List<Building> getSortedBuildings(Game game) {
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

    /**
     * Gets the number of possible turns
     * 
     * @return number of enemy turns as int
     */
    public int getTurnCount(Game tempGame, Color player) {
        tempGame.ignoreRules(true);
        int enemyPlacement = 0;
        Building building = null;
        if (player == Color.White) {
            building = Building.White_Tavern;
        } else {
            building = Building.Black_Tavern;
        }

        for (int y = 0; y < 10; ++y) {
            for (int x = 0; x < 10; ++x) {
                Placement possiblePlacement = new Placement(x, y, Direction._0, building);
                if (tempGame.takeTurn(possiblePlacement, true)) {
                    enemyPlacement++;
                    tempGame.undoLastTurn();
                }
            }
        }
        tempGame.ignoreRules(false);

        return enemyPlacement;
    }

  private int getPlaceAbleBuildingScore(Game game, Color player){
    List<Building> buildings = game.getPlacableBuildings(player);
    int buildingScore = 0;
    for(Building building : buildings){
        buildingScore += building.score();
    }

    return buildingScore;
  }
}