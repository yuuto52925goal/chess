package dataaccess;

import com.google.gson.Gson;
import model.data.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import java.util.UUID;

public class MysqlAuthDAO extends Mysql  {

    public MysqlAuthDAO() {
        try{
            this.configureDatabase(createStatements);
        }catch(SQLException | DataAccessException e){
            System.out.println(e.getMessage());
        }
    }
    public String checkAuth(String authToken) {

        try (var conn = DatabaseManager.getConnection()){
            String statement = "SELECT authToken FROM auth WHERE authToken = ?";
            try (PreparedStatement stmt = conn.prepareStatement(statement)){
                stmt.setString(1, authToken);
                try (ResultSet rs = stmt.executeQuery()){
                    if (rs.next()){
                        return rs.getString("authToken");
                    }
                }
            }
        }catch (SQLException | DataAccessException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void deleteAllAuths() {
        String statement = "DELETE FROM auth";
        executeUpdate(statement);
    }

    public void deleteAuth(String username) {
        String statement = "DELETE FROM auth WHERE username = ?";
        int rowsDeleted = executeUpdate(statement, username);
        if (rowsDeleted > 0){
            System.out.println("Deleted " + rowsDeleted + " rows from " + username);
        }else{
            System.out.println("No rows deleted from " + username);
        }
    }

    public String createAuth(String username){
        String newAuthToken = UUID.randomUUID().toString();
        var statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
        var json = new Gson().toJson(new AuthData(username, newAuthToken));
        executeUpdate(statement, json);
        return newAuthToken;
    }

    private int executeUpdate(String statement, Object... args) {
        try(var conn = DatabaseManager.getConnection()){
            try(var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)){
                for (var i = 0; i < args.length; i++) {
                    var arg = args[i];
                    if (arg instanceof String) ps.setString(i + 1, (String) arg);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }catch (SQLException e){
                System.out.println(e.getMessage());
                return 0;
            }
        }catch (SQLException | DataAccessException e){
            System.out.println(e.getMessage());
            return 0;
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `authToken` varchar(256) NOT NULL UNIQUE,
              PRIMARY KEY (`id`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

}
