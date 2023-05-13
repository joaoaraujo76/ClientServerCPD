package server.game;

import protocol.Message;
import protocol.MessageType;
import server.models.Player;
import server.models.UserState;


import java.io.IOException;
import java.util.List;

import static server.data.UsersData.updateEloByUsername;
import static server.data.UsersData.updateStateByUsername;


public class Game implements Runnable {

    private final List<Player> players;
    public Game(List<Player> players) {
        this.players = players;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(5 * 1000);

            Player playerOne = players.get(0);
            Player playerTwo = players.get(1);

            int playerOneElo = playerOne.getUser().getElo();
            int playerTwoElo = playerTwo.getUser().getElo();

            updateEloByUsername(playerOne.getUser().getUsername(), playerOneElo + 50);
            updateEloByUsername(playerTwo.getUser().getUsername(), playerTwoElo - 50);

            updateStateByUsername(playerOne.getUser().getUsername(), -1L, UserState.NONE);
            updateStateByUsername(playerTwo.getUser().getUsername(), -1L, UserState.NONE);

            playerOne.getOutputStream().writeObject(new Message(MessageType.END_GAME, playerOne.getUser().getToken(), "Game ended. You won!"));
            playerOne.getOutputStream().flush();

            playerTwo.getOutputStream().writeObject(new Message(MessageType.END_GAME, playerTwo.getUser().getToken(), "Game ended. You lost!"));
            playerTwo.getOutputStream().flush();

        } catch (InterruptedException | IOException e) {
            // TODO: handle exceptions
        }
    }
}
