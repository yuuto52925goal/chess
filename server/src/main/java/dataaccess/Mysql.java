package dataaccess;

import java.sql.SQLException;

public class Mysql {

    protected void configureDatabase(String[] createStatements) throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        try(var conn = DatabaseManager.getConnection()){
            for (var statement: createStatements){
                try(var prepareStatement = conn.prepareStatement(statement)){
                    prepareStatement.executeUpdate();
                }
            }
        }
    }
}
