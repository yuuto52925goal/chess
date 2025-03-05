package dataaccess;

import model.data.UserData;

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
        return null;
    }

    @Override
    public UserData createUser(UserData user) {
        return null;
    }

    @Override
    public void deleteAllUsers() {

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
