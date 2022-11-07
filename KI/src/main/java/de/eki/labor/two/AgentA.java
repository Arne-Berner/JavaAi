package de.eki.labor.two;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JComponent;

import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.Building;
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

    // problem: wenn er nicht nach links, rechts, unten, oben setzen kann, setzt er
    // nichtmehr

    @Override
    public Optional<Placement> calculateTurn(Game game, int timeForTurn, int timeBonus) {
        List<Placement> possiblePlacements = new ArrayList<>();
        Game tempGame = game.copy();
        boolean placed = false;
        List<Building> buildings = new ArrayList<Building>();
        for (int i = 5; i > 0; i--) {
            for (Building building : game.getPlacableBuildings()) {
                if (building.score() == i) {
                    buildings.add(building);
                }

            }
        }

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

        if(possiblePlacements.isEmpty())
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
        // sollte eigentlich die position des letzten zugs nehmen, funktioniert aber nicht
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

    @Override
    public String evaluateLastTurn(Game game) {
        return "Kann ich net!";
    }

    @Override
    public void stop() {

    }
}

