import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;

public class UserDB {
    Connection dbConnection;

    public UserDB() throws SQLException, IOException {
        String[] connParams = {"", "", ""};
        getConnectionParameters(connParams);
        dbConnection = DriverManager.getConnection(connParams[0], connParams[1], connParams[2]);
    }

    public void getConnectionParameters(String[] connParams) throws IOException {
        Properties props = new Properties();
        try(InputStream in = Files.newInputStream(Paths.get("database.properties"))) {
            props.load(in);
        }
        connParams[0] = props.getProperty("url");
        connParams[1] = props.getProperty("username");
        connParams[2] = props.getProperty("password");
    }

    //проверка авторизованности пользователя
    public String CheckUserAuth (String username, String password) {
        String query = "SELECT username, telephone, password FROM users " +
                    "WHERE username = '" + username + "' AND password = '" + password + "'";
            try {
                PreparedStatement prSt = dbConnection.prepareStatement(query);
                ResultSet result =  prSt.executeQuery();
                if(result.next()) {
                    String id = result.getString(1);
                    System.out.println(id);
                    return id;
                }
                return "-1";
            }
            catch (SQLException e) {
                e.printStackTrace();
                return "-1";
            }
        }

    //добавление/создание профиля пользователя
    public boolean NewUser(String username,
                           String telephone,
                           String password) {
        try {
            String query = "INSERT INTO users (username, telephone, password) " +
                    "VALUES ('" + username + "','"+ telephone +"','"+ password +"')";
            PreparedStatement prSt = dbConnection.prepareStatement(query);
            boolean res = prSt.execute(query);
            System.out.println(res);
            if (res) {
                return true;
            } else {
                return true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
