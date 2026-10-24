package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;


public class MemoryGameDAO implements GameDAO {
    private ArrayList<GameData> gameDataList;
    private int nextGameID;

    public  MemoryGameDAO() {
        this.gameDataList = new ArrayList<>();
        this.nextGameID = 1;
    }

    @Override
    public void clear() throws DataAccessException {
        gameDataList.clear();
        nextGameID = 1;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData game : gameDataList) {
            if (game.gameID() == (gameID)) {
                return game;
            }
        }
        return null;
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        int gameID = nextGameID++;

        GameData game = new GameData(gameID, null, null, gameName, new ChessGame());

        gameDataList.add(game);
        return gameID;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(gameDataList);
    }

    @Override
    public void updateGame(GameData updatedGame) throws DataAccessException {
        for (int i = 0; i < gameDataList.size(); i++) {
            if (gameDataList.get(i).gameID() == updatedGame.gameID()) {
                gameDataList.set(i, updatedGame);
                return;
            }
        }
        throw new DataAccessException("Game not found");
    }

}