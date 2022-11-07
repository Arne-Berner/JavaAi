package de.eki.labor.two;

import de.fhkiel.ki.cathedral.game.*;

import java.util.*;

public class DasErsteSpiel {
    public static void main(String[] args) {
        System.out.println("\n\nStart------------------------------");
        Game game = new Game();
        System.out.println(game);

        Placement cathedral = new Placement(5, 1, Direction._90, Building.Blue_Cathedral);

        if (game.takeTurn(cathedral)) {
            System.out.println("Erfolg?\n" + game);
        }

        while (!game.isFinished()) {
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
            System.out.println("Anzahl möglicher Züge: " + possiblePlacements.size());

            if (possiblePlacements.isEmpty()){
                game.forfeitTurn();
            } else {
                game.takeTurn(new ArrayList<>(possiblePlacements).get(new Random().nextInt(possiblePlacements.size())));
            }
            System.out.println("Erfolg?\n" + game);
        }

        System.out.println("Fretig? " + game.isFinished());
        System.out.println("Ende ------------------------------");
    }
}
