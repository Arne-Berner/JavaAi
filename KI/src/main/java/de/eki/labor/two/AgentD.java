package de.eki.labor.two;

import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.Building;
import de.fhkiel.ki.cathedral.game.Direction;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;
import de.fhkiel.ki.cathedral.game.Position;
import de.fhkiel.ki.cathedral.game.Turn;
import de.fhkiel.ki.cathedral.gui.CathedralGUI;

import javax.swing.*;
import java.io.PrintStream;
import java.text.ParsePosition;
import java.util.*;

public class AgentD implements Agent {

    public static void main(String[] args) {
        CathedralGUI.start(new AgentD(), new AgentB());
    }

    private PrintStream console;
    private Position lastPosition = new Position(3, 4);

    @Override
    public String name() {
        return "Super cool Agent";
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
            boolean goingUp = true;
            boolean yReset = true;
            boolean goingLeft = true;
            boolean goingRight = true;
            Position possiblePosition = new Position(lastPosition.x(), lastPosition.y());
            int y = possiblePosition.y();
            int x = possiblePosition.x();
            while (gridloop) {
                if (y < 9 && goingDown) {
                    ++y;
                } else if (y > 0 && goingUp) {
                    --y;
                    goingDown = false;
                } else if (yReset) {
                    possiblePosition = new Position(lastPosition.x(), lastPosition.y());
                    goingUp = false;
                    yReset = false;
                } else if (x < 9 && goingRight) {
                    ++x;
                    console.println("now is x right");
                } else if (x > 0 && goingLeft) {
                    --x;
                    goingRight = false;
                    console.println("now is x left");
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

        // lastPosition = possiblePlacements.get(0).position();
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
