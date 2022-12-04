import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class OrderDB {
    Connection dbConnection;

    public OrderDB() throws SQLException, IOException {
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

    //создание новой заявки
    public boolean NewOrder (int master_id,
                             int user_id,
                             String mark, String color,
                             String status, String comment) {
        try {
            String query = "INSERT INTO order (master_id, user_id, mark, color, status, comment) " +
                    "VALUES ('" + master_id + "','"+ user_id +"','"+ mark +"', '"+color+"', '" + status + "', '"+comment+"')";
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

    //редактирование заявки
    public boolean EditOrder (int order_id,
                              int master_id,
                              int user_id,
                              String mark, String color,
                              String status, String comment) {
        try {
            String query = "UPDATE order SET master_id = '"+master_id+"', user_id= '"+user_id+"'," +
                    "mark= '"+mark+"', color= '"+color+"', status= '"+status+"', comment= '"+comment+"' WHERE order_id= '"+order_id+"'";
            PreparedStatement prSt = dbConnection.prepareStatement(query);
            boolean res = prSt.execute(query);
            System.out.println(res);
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
