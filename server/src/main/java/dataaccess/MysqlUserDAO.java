package dataaccess;

import model.data.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class MysqlUserDAO extends Mysql implements UserDAO{

    public MysqlUserDAO() {
        try{
            this.configureDatabase(createStatements);
        }catch(SQLException | DataAccessException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) {
        String statement = "SELECT password, email FROM user WHERE username = ?";
        try(var conn = DatabaseManager.getConnection()){
            try(var stmt = conn.prepareStatement(statement)){
                stmt.setString(1, username);
                try(var rs = stmt.executeQuery()){
                    if(rs.next()){
                        String hashedPassword = rs.getString("password");
                        String email = rs.getString("email");
                        return new UserData(username, hashedPassword, email);
                    }
                }
            }
        }catch (DataAccessException | SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public UserData createUser(UserData user) {
        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        String hashPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        executeUpdate(statement, user.username(), hashPassword, user.email());
        return user;
    }

    @Override
    public void deleteAllUsers() {
        String statement = "DELETE FROM user";
        this.executeUpdate(statement);
    }

    private final String[] createStatements ={
            """
            CREATE TABLE IF NOT EXISTS user (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL UNIQUE,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL UNIQUE,
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}
