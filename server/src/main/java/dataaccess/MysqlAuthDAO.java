package dataaccess;

import com.google.gson.Gson;
import model.data.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import java.util.UUID;

public class MysqlAuthDAO extends Mysql  implements AuthDAO {

    public MysqlAuthDAO() {
        try{
            this.configureDatabase(createStatements);
        }catch(SQLException | DataAccessException e){
            System.out.println(e.getMessage());
        }
    }
    public String checkAuth(String authToken) {

        String statement = "SELECT authToken, username FROM auth WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement stmt = conn.prepareStatement(statement)){
                stmt.setString(1, authToken);
                try (ResultSet rs = stmt.executeQuery()){
                    if (rs.next()){
                        return rs.getString("username");
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
        this.executeUpdate(statement);
    }

    public void deleteAuth(String authToken) {
        String statement = "DELETE FROM auth WHERE authToken = ?";
        int rowsDeleted = this.executeUpdate(statement, authToken);
        if (rowsDeleted > 0){
            System.out.println("Deleted " + rowsDeleted + " rows from " + authToken);
        }else{
            System.out.println("No rows deleted from " + authToken);
        }
    }

    public String createAuth(String username){
        String newAuthToken = UUID.randomUUID().toString();
        var statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
        executeUpdate(statement, username, newAuthToken);
        return newAuthToken;
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
