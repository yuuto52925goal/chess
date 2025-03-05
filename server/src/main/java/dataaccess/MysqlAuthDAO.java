package dataaccess;

import java.sql.SQLException;

public class MysqlAuthDAO extends Mysql implements AuthDAO {

    public MysqlAuthDAO() throws DataAccessException, SQLException {
        super();
        this.configureDatabase(createStatements);
    }
    @Override
    public String checkAuth(String authToken) {
        return "";
    }

    @Override
    public void deleteAllAuths() {

    }

    @Override
    public void deleteAuth(String username) {

    }

    @Override
    public String createAuth(String username) {
        return "";
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
