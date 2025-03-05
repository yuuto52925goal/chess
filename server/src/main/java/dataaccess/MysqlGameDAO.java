package dataaccess;

import chess.ChessGame;
import model.data.GameData;

import java.sql.ResultSet;
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
        String statement = "SELECT * FROM games";
        ArrayList<GameData> games = new ArrayList<>();
        try(var conn = DatabaseManager.getConnection()){
            try(var stmt = conn.prepareStatement(statement)){
                try(var rs = stmt.executeQuery()){
                    while(rs.next()){
                        int gameId = rs.getInt("gameId");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        ChessGame game = rs.getObject("game", ChessGame.class);
                        games.add(new GameData(gameId, whiteUsername, blackUsername, gameName, game));
                    }
                }
            }
        }catch (SQLException | DataAccessException e){
            System.out.println("SQLException: " + e.getMessage());
        }
        return games;
    }

    @Override
    public int createGame(String gameName) {
        String statement = "INSERT INTO games (gameName) VALUES (?)";
        this.executeUpdate(statement, gameName);
        String query = "SELECT gameId FROM games WHERE gameName = ?";
        try (var conn = DatabaseManager.getConnection()){
            try (var stmt = conn.prepareStatement(query)){
                try(var res = stmt.executeQuery()){
                    if(res.next()){
                        return res.getInt("gameId");
                    }
                }
            }
        }catch (SQLException | DataAccessException e){
            System.out.println("SQLException: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public GameData findGame(int gameId) {
        String statement = "SELECT * FROM game WHERE gameID = ?";
        try(var conn = DatabaseManager.getConnection();){
            try (var stmt = conn.prepareStatement(statement)) {
                stmt.setInt(1, gameId);
                try (var rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String gameName = rs.getString("gameName");
                        String whiteUser = rs.getString("whiteUsername");
                        String blackUser = rs.getString("blackUsername");
                        ChessGame game = rs.getObject("game", ChessGame.class);
                        return new GameData(gameId, whiteUser, blackUser, gameName, game);
                    }
                }
            }
        }catch (DataAccessException| SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public void updateGame(GameData game) {

    }

    @Override
    public void deleteGames() {
        String statement = "DELETE FROM game";
        this.executeUpdate(statement);

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
