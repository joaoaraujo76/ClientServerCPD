package server.game;

import protocol.Message;
import protocol.MessageType;
import server.models.Player;
import server.models.UserState;


import java.io.IOException;
import java.util.List;
import java.util.Random;

import static server.data.UsersData.updateEloByUsername;
import static server.data.UsersData.updateStateByUsername;



public class Game implements Runnable {

    private final List<Player> players;
    private final String gameType;
    public Game(List<Player> players, String gameType) {
        this.players = players;
        this.gameType = gameType;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(30 * 1000);

            int numPlayers = players.size();
            Random random = new Random();

            for (Player currentPlayer : players) {
                int currentPlayerElo = currentPlayer.getUser().getElo();

                // Gerar um número aleatório entre -50 e 50
                int eloChange = random.nextInt(101) - 50;

                if (gameType.equals("RANKED")) {
                    int newElo = currentPlayerElo + eloChange;
                    updateEloByUsername(currentPlayer.getUser().getUsername(), newElo);
                }

                updateStateByUsername(currentPlayer.getUser().getUsername(), -1L, UserState.NONE);

                String resultMessage;
                if (eloChange >= 0) {
                    resultMessage = "Game ended. You won! Your ELO changed by +" + eloChange;
                } else {
                    resultMessage = "Game ended. You lost! Your ELO changed by " + eloChange;
                }

                System.out.println("Game ended. " + currentPlayer.getUser().getUsername() + " - ELO change: " + eloChange);
                currentPlayer.getOutputStream().writeObject(new Message(MessageType.END_GAME, currentPlayer.getUser().getToken(), resultMessage));
                currentPlayer.getOutputStream().flush();
            }
        } catch (InterruptedException | IOException e) {
            // TODO: handle exceptions
        }
    }
}
