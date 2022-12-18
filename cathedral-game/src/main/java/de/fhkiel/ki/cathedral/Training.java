package de.fhkiel.ki.cathedral;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import de.fhkiel.ki.cathedral.game.Building;
import de.fhkiel.ki.cathedral.game.Color;
import de.fhkiel.ki.cathedral.game.Direction;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;

public class Training {
    // god i hate this shit
    AgentA player1 = new AgentA();
    AgentA player2 = new AgentA();

    // public static final void main(String args[]) {
    // train(1);
    // }

    public String train(int numberGames) {
        Game game = new Game();

        String result = "";
        int[] testfield = {5,5};
        Serializer.serializeFirstResult(new MatchResult(testfield, -500));

        for (int i = 0; i < numberGames; i++) {
            // fuer alle zuege
            Set<Placement> catPlacs = Building.Blue_Cathedral.getPossiblePlacements(game);
            for (Placement catPlac : catPlacs) {
                int[] catPosition = { catPlac.position().x(), catPlac.position().y() };
                game.takeTurn(catPlac);

                while (!game.isFinished()) {
                    Placement placement = new Placement(0, 0, Direction._0, Building.Black_Abbey);

                    if (game.getCurrentPlayer() == Color.Black) {
                        Optional<Placement> playerTurn = player2.calculateTurn(game, 30, 120);

                        if (playerTurn.isEmpty()) {
                            game.forfeitTurn();
                        } else {
                            placement = playerTurn.get();
                        }

                    } else {
                        Optional<Placement> playerTurn = player1.calculateTurn(game, 30, 120);

                        if (playerTurn.isEmpty()) {
                            game.forfeitTurn();
                        } else {
                            placement = playerTurn.get();
                        }
                    }

                    game.takeTurn(placement);
                }

                int scoreDiff = game.getPlayerScore(Color.Black) - game.getPlayerScore(Color.White);
                MatchResult matchResult = new MatchResult(catPosition, scoreDiff);
                Serializer.addResult(matchResult);
                game = new Game();

            }

        }
        List<MatchResult> matchResults = Serializer.deserialize();
        int bestScore = 0;
        int[] bestPosition = new int[2];
        for (MatchResult matchResult : matchResults) {
            if (matchResult.getScoreDifference() > bestScore) {
                bestPosition = matchResult.getCatPosition();
                bestScore = matchResult.getScoreDifference();
            }
        }
        result = "\nDas beste Placement ist: " + bestPosition[0] + ", " + bestPosition[1]
                + " und gibt eine Punktedifferenz von " + bestScore + " für weiß.";

        return result;
    }

}

class SimpleTraining {
    public static final void main(String args[]) {
        Training training = new Training();
        String result = training.train(1);
        System.out.println(result);
    }

}
