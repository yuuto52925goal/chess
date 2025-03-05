package dataaccess;

import model.data.GameData;

import java.sql.SQLException;
import java.util.ArrayList;

public class MysqlGameDAO extends Mysql implements GameDAO{

    public MysqlGameDAO() {
        try{
            this.configureDatabase(createStatements);
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<GameData> listGames() {
        return null;
    }

    @Override
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public GameData findGame(int gameId) {
        return null;
    }

    @Override
    public void updateGame(GameData game) {

    }

    @Override
    public void deleteGames() {

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
            `gameID` int NOT NULL AUTO_INCREMENT,
            `whiteUsername` varchar(256),
            `blackUsername` varchar(256),
            `gameName` varchar(256) NOT NULL,
            `game` TEXT,
            PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}
