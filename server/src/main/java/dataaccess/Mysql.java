package dataaccess;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

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

    protected int executeUpdate(String statement, Object... args) {
        try(var conn = DatabaseManager.getConnection()){
            try(var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)){
                for (var i = 0; i < args.length; i++) {
                    var arg = args[i];
                    if (arg instanceof String) {
                        ps.setString(i + 1, (String) arg);
                    }
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
}
