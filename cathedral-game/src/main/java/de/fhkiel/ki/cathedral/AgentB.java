package de.fhkiel.ki.cathedral;

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

    public Placement firstTurn(Direction first, Direction second, Building building, Game tempGame){
        for (int x = 4; x <= 5; x++){
            for (int y = 4; y <= 5; y++){
                Placement firstPlacement = new Placement(x, y, first, building);
                if (tempGame.takeTurn(firstPlacement, true)) {
                    return firstPlacement;
                }
                else{
                    Placement possiblePlacement270 = new Placement(x, y, second, building);
                    if (tempGame.takeTurn(possiblePlacement270, true)) {
                        return firstPlacement;
                    }
                }
            }
        }
        Placement defaultPlacement = new Placement(2, 2, first, building);
        return defaultPlacement;
    }

    @Override
    public Optional<Placement> calculateTurn(Game game, int timeForTurn, int timeBonus) {
        List<Placement> possiblePlacements = new ArrayList<>();
        Game tempGame = game.copy();
        boolean placed = false;
        List<Building> buildings = new ArrayList<Building>();



        for (Building neuner : game.getPlacableBuildings()) {
            if (neuner.getNumberInGame() == 9) {
                Position cathedralPosition = game.getBoard().getPlacedBuildings().get(0).position();
                if ((cathedralPosition.x() < 4) && (cathedralPosition.y() > 4)){
                    possiblePlacements.add(firstTurn(Direction._0, Direction._90, neuner, tempGame));
                }
                if ((cathedralPosition.x() > 4) && (cathedralPosition.y() < 4)){
                    possiblePlacements.add(firstTurn(Direction._180, Direction._270, neuner, tempGame));
                }
                if ((cathedralPosition.x() > 4) && (cathedralPosition.y() > 4)){
                    possiblePlacements.add(firstTurn(Direction._0, Direction._270, neuner, tempGame));
                }
                if ((cathedralPosition.x() < 4) && (cathedralPosition.y() < 4)){
                    possiblePlacements.add(firstTurn(Direction._90, Direction._180, neuner, tempGame));
                }
            }
        }

        for (int i = 5; i > 0; i--) {
            for (Building building : game.getPlacableBuildings()) {
                if (building.score() == i) {
                    buildings.add(building);
                }

            }
        }

        // corners problem
        /*
        for (Building building : game.getPlacableBuildings()){
            if (building.getId() == 9){
                console.print(building.corners(Direction._90));
            }
        }
        */


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

