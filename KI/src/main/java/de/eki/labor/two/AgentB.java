package de.eki.labor.two;

import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.Building;
import de.fhkiel.ki.cathedral.game.Direction;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;
import de.fhkiel.ki.cathedral.gui.CathedralGUI;

import javax.swing.*;
import java.io.PrintStream;
import java.util.*;

public class AgentB  implements Agent {


    private PrintStream console;

    @Override
    public String name() {
        return "Super duper cool Agent";
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
        Set<Placement> possiblePlacements = new HashSet<>();
        Game tempGame = game.copy();
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
        console.println("Anzahl möglicher Züge: " + possiblePlacements.size());

        if (possiblePlacements.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(new ArrayList<>(possiblePlacements).get(new Random().nextInt(possiblePlacements.size())));
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
