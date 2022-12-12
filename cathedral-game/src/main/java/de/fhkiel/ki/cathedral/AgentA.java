package de.fhkiel.ki.cathedral;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.swing.JComponent;

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
                .build(), new AgentA(), new AgentB());
    }

    private PrintStream console;
    private Position lastPosition = new Position(3, 3);
    private boolean firstTurn = true;

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
        Game tempGame = game.copy();
        List<Position> freeFields = Utility.getFreeFields(tempGame);
        boolean isFillPhase = Utility.isFillphase(tempGame);
        List<Building> buildings = Utility.getSortedBuildings(game);
        Color playerColor = tempGame.getCurrentPlayer();

        // noch keine Füllphase
        if (!isFillPhase) {
            List<Placement> possiblePlacements = new ArrayList<>();

            //erster Zug
            if (firstTurn) {
                possiblePlacements = firstTurn(tempGame);

                if (possiblePlacements.size() > 0) {
                    firstTurn = false;
                    return Optional
                            .of(possiblePlacements.get(0));
                }
            }

            // zweiter zug die Wand beruehren lassen?
            // erstmal Steine moeglichst gut klauen
            Color[][] field = tempGame.getBoard().getField();
            List<Position> playerPlaced = Utility.placedByPlayer(field, tempGame.getCurrentPlayer());
            possiblePlacements = Utility.getAllPossiblePlacement(tempGame, playerColor, freeFields);
            List<Placement> connectingPlacements = new ArrayList<Placement>();

            for(Placement possiblePlacement : possiblePlacements){

                int placementScore = 0;

                for (Position corner : possiblePlacement.building().corners(possiblePlacement.direction())) {
                    Position placementPosition = possiblePlacement.position();
                    Position actualPosition = placementPosition.plus(corner);

                    if (playerPlaced.contains(actualPosition)) {
                        placementScore++;
                    }

                }

                if(placementScore == 1){
                    connectingPlacements.add(possiblePlacement);
                }
            }

            boolean canTakeBuilding = false;
            int bestScore = 500;
            Placement bestPlacement = null;
            for(Placement connectingPlacement : connectingPlacements){
                tempGame.takeTurn(connectingPlacement);
                
                int currentScore = 0;
                //first building that can steal, resets everything
                if(Utility.enemyScoreDiff(tempGame) > 0 && canTakeBuilding == false){
                    canTakeBuilding = true;
                    bestScore = Utility.getLastTurnScore(tempGame);
                    bestPlacement = connectingPlacement;
                } else {
                    currentScore = Utility.getLastTurnScore(tempGame);
                }


                if(!canTakeBuilding){
                    //forloop mit besseren placementscore
                    // add to currentscore this new score

                }

                if(currentScore > bestScore){
                    bestScore = currentScore;
                    bestPlacement = connectingPlacement;
                }
            }

            // fuer alle moeglichen Zuege an ein angrenzendes Feld
                // setze einen Zug an ein angrenzendes Feld (nur eine ueberschneidung)
                // evaluiere den Zug
                // wenn ein Stein geklaut werden kann, gehe nicht in die naechste schleife
                // bool?
                // fuer alle neuen moeglichen Zuege an ein angrenzendes Feld
                    // setze einen Zug an ein angrenzendes Feld
                    // evaluiere den Zug


            // random placement
            // if (possiblePlacements.isEmpty()) {
            //     for (Building building : game.getPlacableBuildings()) {
            //         for (int y = 0; y < 10; ++y) {
            //             for (int x = 0; x < 10; ++x) {
            //                 for (Direction direction : building.getTurnable().getPossibleDirections()) {
            //                     Placement possiblePlacement = new Placement(x, y, direction, building);
            //                     if (tempGame.takeTurn(possiblePlacement, true)) {
            //                         possiblePlacements.add(possiblePlacement);
            //                         tempGame.undoLastTurn();
            //                     }
            //                 }
            //             }
            //         }
            //     }
            // }

            if (possiblePlacements.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional
                        .of(new ArrayList<>(possiblePlacements)
                                .get(new Random().nextInt(possiblePlacements.size())));
            }
        } else {
            // fillphase

            //makes the recursive function possible
            tempGame.ignoreRules(true);

            //gets all placements, since it is fast enough (we used getGoodPlacements before)
            List<Placement> goodPlacements = Utility.getAllPossiblePlacement(tempGame, playerColor,
                    Utility.getOwnedFields(tempGame, playerColor));

            //if there is no placement to be made, return an empty turn
            if (goodPlacements.size() == 0) {
                tempGame.ignoreRules(false);
                return Optional.empty();
            }

            //entry point for the recursive function
            Placement bestPlacement = Utility.getBestPlacement(tempGame, goodPlacements, playerColor);
            tempGame.ignoreRules(false);
            return Optional.of(bestPlacement);
        }
    }

    /**
     * Gets a reason, why your turn was good or bad
     * 
     * @return Score as int
     */
    private String getLastTurnScoreAsString(Game game) {
        Game tempGame = game.copy();
        Color currentPlayer = tempGame.getCurrentPlayer();
        Color enemyPlayer = tempGame.getEnemyPlayer();

        String reason = "\n";
        if (Utility.getPlaceAbleBuildingScore(tempGame, currentPlayer) == 0) {
            reason = "Du kannst keinen Zug mehr machen.";
            return reason;
        }

        List<Position> freeFields = Utility.getFreeFields(tempGame);
        boolean isFillPhase = Utility.isFillphase(tempGame);
        if (!isFillPhase) {
            // nach dem letzten zug
            tempGame.undoLastTurn();
            List<Position> currentFreeFields = Utility.getFreeFields(tempGame);
            int turns = currentFreeFields.size();
            int ownScore = tempGame.getPlayerScore(currentPlayer);
            int enemyScore = tempGame.getPlayerScore(enemyPlayer);
            int enemyBuildingScore = Utility.getPlaceAbleBuildingScore(tempGame, enemyPlayer);

            // vor dem letzten zug
            tempGame.undoLastTurn();
            List<Position> oldFreeFields = Utility.getFreeFields(tempGame);
            int oldTurns = oldFreeFields.size();
            int oldOwnScore = tempGame.getPlayerScore(currentPlayer);
            int oldEnemyScore = tempGame.getPlayerScore(enemyPlayer);
            int oldEnemyBuildingScore = Utility.getPlaceAbleBuildingScore(tempGame, enemyPlayer);

            // muss alles moeglichst hoch sein
            // wird doppelt gezaehlt durch die flaeche die eingenommen wird
            int ownScoreDiff = (oldOwnScore - ownScore);
            int enemyScoreDiff = (enemyScore - oldEnemyScore);
            int enemyBuildingScoreDiff = oldEnemyBuildingScore - enemyBuildingScore + (enemyScoreDiff);
            int enemyTurnDiff = oldTurns - turns;

            int turnScore = (int) ownScoreDiff + enemyScoreDiff + enemyTurnDiff + enemyBuildingScoreDiff;

            reason += "\nEs gibt noch " + freeFields.size() + " freie Felder.\n";

            switch (ownScoreDiff) {
                case 1:
                case 2:
                case 3:
                    reason += "Du hast einen kleinen Stein gesetzt. Das gibt: " + ownScoreDiff + " Punkte\n";
                    break;
                case 4:
                case 5:
                    reason += "Du hast einen grossen Stein gesetzt. Das gibt: " + ownScoreDiff + " Punkte\n";
                    break;
                default:
                    reason += "Du hast keinen Stein gesetzt :(\n";
                    break;
            }

            switch (enemyScoreDiff) {
                case 1:
                case 2:
                case 3:
                    reason += "Du hast deinem Gegner einen kleinen Stein geklaut! Das gibt: " + enemyScoreDiff
                            + " Punkte\n";
                    break;
                case 4:
                case 5:
                    reason += "Du hast deinem Gegner einen grossen Stein geklaut! Das gibt: " + enemyScoreDiff
                            + " Punkte\n";
                    break;
                default:
                    reason += "Du hast keinen Stein geklaut :(\n";
                    break;
            }

            if (enemyBuildingScoreDiff == 0) {
                reason += "Dein Gegner kann gleich viele Gebäude wie vorher setzen.\n";
            } else if (enemyBuildingScore < 3) {
                reason += "Dein Gegner kann weniger Gebäude setzen. Das gibt: " + enemyBuildingScoreDiff + " Punkte\n";
            } else if (enemyBuildingScore < 6) {
                reason += "Dein Gegner kann viel weniger Gebäude setzen. Das gibt: " + enemyBuildingScoreDiff
                        + " Punkte\n";
            } else if (enemyBuildingScore > 6) {
                reason += "Dein Gegner kann sehr viel weniger Gebäude setzen!! Das gibt: " + enemyBuildingScoreDiff
                        + " Punkte\n";
            }

            if (enemyTurnDiff == 0) {
                reason += "Du hast keinen Zug gemacht.\n";
            } else if (enemyTurnDiff < 3) {
                reason += "Du hast wenig Fläche besetzt. Das gibt: " + enemyTurnDiff + " Punkte\n";
            } else if (enemyTurnDiff < 10) {
                reason += "Du hast mittelviel Fläche besetzt. Das gibt: " + enemyTurnDiff + " Punkte\n";
            } else if (enemyTurnDiff > 10) {
                reason += "Du hast viel Fläche besetzt!! Das gibt: " + enemyTurnDiff + " Punkte\n";
            }

            if (turnScore == 0) {
                reason += "Das ergibt: " + turnScore + "Punkte\nSchade.";
            } else if (turnScore < 5) {
                reason += "Das ergibt insgesamt " + turnScore + " Punkte\nDas nächste mal wirds besser.";
            } else if (turnScore < 10) {
                reason += "Das ergibt insgesamt " + turnScore + " Punkte\nBesser als gar nichts.";
            } else if (turnScore < 15) {
                reason += "Das ergibt insgesamt " + turnScore + " Punkte\nNicht schlecht, aber auch nicht gut.";
            } else if (turnScore < 20) {
                reason += "Das ergibt insgesamt " + turnScore + " Punkte\nIch bin beeindruckt. Aber nur leicht.";
            } else if (turnScore < 25) {
                reason += "Das ergibt insgesamt " + turnScore + " Punkte\nNicht schlecht, Herr Specht.";
            } else if (turnScore < 30) {
                reason += "Das ergibt insgesamt " + turnScore + " Punkte\nMacher.";
            } else if (turnScore < 35) {
                reason += "Das ergibt insgesamt " + turnScore + " Punkte\nIch bin schwer beeindruckt.";
            } else if (turnScore > 35) {
                reason += "Das ergibt insgesamt " + turnScore + " Punkte\nWow :)";
            }

        } else {
            reason += "\nJetzt ist Füllphase.\n";
        }

        return reason;
    }

    @Override
    public String evaluateLastTurn(Game game) {
        Utility.getLastTurnScore(game);
        return getLastTurnScoreAsString(game);
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

    private List<Placement> firstTurn(Game tempGame) {
        // Erster zug!
        List<Placement> possiblePlacements = new ArrayList<Placement>();
        for (Building neuner : tempGame.getPlacableBuildings()) {
            if (neuner.getId() == 9) {
                Position cathedralPosition = tempGame.getBoard().getPlacedBuildings().get(0).position();
                if (cathedralPosition.x() < 5) {
                    // nur rechte positionen setzen
                    var possiblePosition = new Position(7, 4);
                    Placement possiblePlacement = new Placement(possiblePosition, Direction._90, neuner);
                    if (tempGame.takeTurn(possiblePlacement, true)) {
                        possiblePlacements.add(possiblePlacement);
                    }

                    possiblePosition = new Position(7, 5);
                    possiblePlacement = new Placement(possiblePosition, Direction._90, neuner);
                    if (tempGame.takeTurn(possiblePlacement, true)) {
                        possiblePlacements.add(possiblePlacement);
                    }
                }
                if (cathedralPosition.x() > 4) {
                    // nur linke positionen setzen
                    var possiblePosition = new Position(2, 4);
                    Placement possiblePlacement = new Placement(possiblePosition, Direction._270, neuner);
                    if (tempGame.takeTurn(possiblePlacement, true)) {
                        possiblePlacements.add(possiblePlacement);
                    }

                    possiblePosition = new Position(2, 5);
                    possiblePlacement = new Placement(possiblePosition, Direction._270, neuner);
                    if (tempGame.takeTurn(possiblePlacement, true)) {
                        possiblePlacements.add(possiblePlacement);
                    }
                }
                if (cathedralPosition.y() < 5) {
                    // nur untere positionen setzen
                    var possiblePosition = new Position(4, 7);
                    Placement possiblePlacement = new Placement(possiblePosition, Direction._180, neuner);
                    if (tempGame.takeTurn(possiblePlacement, true)) {
                        possiblePlacements.add(possiblePlacement);
                    }

                    possiblePosition = new Position(5, 7);
                    possiblePlacement = new Placement(possiblePosition, Direction._180, neuner);
                    if (tempGame.takeTurn(possiblePlacement, true)) {
                        possiblePlacements.add(possiblePlacement);
                    }
                }
                if (cathedralPosition.y() > 4) {
                    // nur obere positionen setzen
                    var possiblePosition = new Position(4, 2);
                    Placement possiblePlacement = new Placement(possiblePosition, Direction._0, neuner);
                    if (tempGame.takeTurn(possiblePlacement, true)) {
                        possiblePlacements.add(possiblePlacement);
                    }

                    possiblePosition = new Position(5, 2);
                    possiblePlacement = new Placement(possiblePosition, Direction._0, neuner);
                    if (tempGame.takeTurn(possiblePlacement, true)) {
                        possiblePlacements.add(possiblePlacement);
                    }
                }
            }
        }

        return possiblePlacements;
    }
}