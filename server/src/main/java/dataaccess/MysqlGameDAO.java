package dataaccess;

import chess.ChessGame;
import model.data.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.google.gson.Gson;

public class MysqlGameDAO extends Mysql implements GameDAO{

    public MysqlGameDAO() {
        try{
            this.configureDatabase(createStatements);
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

//    Need to be edited in the future
    @Override
    public ArrayList<GameData> listGames() {
        String statement = "SELECT * FROM game";
        ArrayList<GameData> games = new ArrayList<>();
        try(var conn = DatabaseManager.getConnection()){
            try(var stmt = conn.prepareStatement(statement)){
                try(var rs = stmt.executeQuery()){
                    while(rs.next()){
                        int gameId = rs.getInt("gameID");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);
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
        String statement = "INSERT INTO game (gameName) VALUES (?)";
        if (gameName == null || gameName.isEmpty()) {
            return -1;
        }
        this.executeUpdate(statement, gameName);
        String query = "SELECT gameID FROM game WHERE gameName = ?";
        try (var conn = DatabaseManager.getConnection()){
            try (var stmt = conn.prepareStatement(query)){
                stmt.setString(1, gameName);
                try(var res = stmt.executeQuery()){
                    if(res.next()){
                        return res.getInt("gameID");
                    }
                }
            }
        }catch (SQLException | DataAccessException e){
            System.out.println("SQLException: " + e.getMessage());
        }
        return -1;
    }

//    Needed to be edited for future
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
                        ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                        return new GameData(gameId, whiteUser, blackUser, gameName, game);
                    }
                }
            }
        }catch (DataAccessException| SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

//    Needed to be edited in the future
    @Override
    public void updateGame(GameData game) {
        String statement = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
        try(var conn = DatabaseManager.getConnection()){
            try (var stmt = conn.prepareStatement(statement)) {
                stmt.setString(1, game.whiteUsername());
                stmt.setString(2, game.blackUsername());
                stmt.setString(3, game.gameName());
                stmt.setInt(5, game.gameID());
                if (game.game() != null) {
                    stmt.setObject(4, new Gson().toJson(game.game()));
                }else{
                    System.out.println("null");
                    stmt.setObject(4, null);
                }
                stmt.executeUpdate();
            }
        }catch (SQLException | DataAccessException e){
            System.out.println("SQLException: " + e.getMessage());
        }
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
            `whiteUsername` varchar(256) DEFAULT NULL,
            `blackUsername` varchar(256) DEFAULT NULL,
            `gameName` varchar(256) NOT NULL,
            `game` JSON DEFAULT NULL,
            PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}
