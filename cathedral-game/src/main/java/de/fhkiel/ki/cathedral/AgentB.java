package de.fhkiel.ki.cathedral;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.swing.JComponent;

import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.Building;
import de.fhkiel.ki.cathedral.game.Direction;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;
import de.fhkiel.ki.cathedral.game.Position;
import de.fhkiel.ki.cathedral.gui.CathedralGUI;
import de.fhkiel.ki.cathedral.gui.Settings;

public class AgentB implements Agent {

    public static void main(String[] args) {
        CathedralGUI.start(Settings.Builder()
                .token("MTAzNDA4MjI4NTAyMjEwMTU2NA.G4LMpA.UUuWQC_AYk5UqhuJihNg4nYSXcHOu1JYfBU5mU")
                .build(), new AgentA());
    }

    private PrintStream console;
    private Position lastPosition = new Position(3, 3);

    @Override
    public String name() {
        return "Test Agent";
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

        if (possiblePlacements.isEmpty()) {
            for (Building building : game.getPlacableBuildings()) {
                var buildPlacements = building.getAllPossiblePlacements();
                for (Placement plac : buildPlacements) {
                    possiblePlacements.add(plac);
                }
            }
        }

        // sollte eigentlich die position des letzten zugs nehmen, funktioniert aber
        // nicht
        lastPosition = possiblePlacements.get(0).position();
        if (possiblePlacements.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional
                    .of(new ArrayList<>(possiblePlacements)
                            .get(new Random().nextInt(possiblePlacements.size())));
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
