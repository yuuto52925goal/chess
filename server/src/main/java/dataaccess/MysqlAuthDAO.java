package dataaccess;

import com.google.gson.Gson;
import model.data.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import java.util.UUID;

public class MysqlAuthDAO extends Mysql implements AuthDAO {

    public MysqlAuthDAO() throws DataAccessException, SQLException {
        this.configureDatabase(createStatements);
    }
    @Override
    public String checkAuth(String authToken) throws DataAccessException, SQLException {

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
        }
        return null;
    }

    @Override
    public void deleteAllAuths() {
        String statement = "DELETE FROM auth";
        try{
            executeUpdate(statement);
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deleteAuth(String username) {
        String statement = "DELETE FROM auth WHERE username = ?";
        try{
            int rowsDeleted = executeUpdate(statement, username);
            if (rowsDeleted > 0){
                System.out.println("Deleted " + rowsDeleted + " rows from " + username);
            }else{
                System.out.println("No rows deleted from " + username);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String createAuth(String username) throws SQLException {
        String newAuthToken = UUID.randomUUID().toString();
        var statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
        var json = new Gson().toJson(new AuthData(username, newAuthToken));
        try {
            executeUpdate(statement, json);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return newAuthToken;
    }

    private int executeUpdate(String statement, Object... args) throws SQLException, DataAccessException {
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
            }
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
