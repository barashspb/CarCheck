import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
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
            String query = "INSERT INTO orders (master_id, user_id, mark, color, stat, coment) " +
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
            String query = "UPDATE orders SET master_id = '"+master_id+"', user_id= '"+user_id+"'," +
                    "mark= '"+mark+"', color= '"+color+"', stat= '"+status+"', coment= '"+comment+"' WHERE order_id= '"+order_id+"'";
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

    //получение заявки по id клиента
    public String ShowClientOrder (int user_id) {
        String query = "SELECT order_id, mark, color, stat, coment FROM orders " +
                "WHERE user_id = '" + user_id + "' ";
        try {
            PreparedStatement prSt = dbConnection.prepareStatement(query);
            ResultSet result =  prSt.executeQuery();
            JSONArray list = new JSONArray();

            while (result.next()) {
                String order_id = result.getString(1);
                System.out.println(order_id);
                String mark = result.getString(2);
                System.out.println(mark);
                String color = result.getString(3);
                System.out.println(color);
                String stat = result.getString(4);
                System.out.println(stat);
                String coment = result.getString(5);
                System.out.println(coment);

                JSONObject resultJSON = new JSONObject();
                resultJSON.put("order_id", order_id);
                resultJSON.put("mark", mark);
                resultJSON.put("color", color);
                resultJSON.put("stat", stat);
                resultJSON.put("coment", coment);

                list.add(resultJSON);
            }
            return list.toJSONString();
        } catch(SQLException e) {
            e.printStackTrace();
            return "-1";
        }
    }

    //получение заявки по ее id
    public String ShowOrder (int order_id) {
        String query = "SELECT master_id, user_id, mark, color, stat, coment FROM orders " +
                "WHERE order_id = '" + order_id + "' ";
        try {
            PreparedStatement prSt = dbConnection.prepareStatement(query);
            ResultSet result =  prSt.executeQuery();
            JSONArray list = new JSONArray();

            while (result.next()) {
                String master_id = result.getString(1);
                System.out.println(master_id);
                String user_id = result.getString(2);
                System.out.println(user_id);
                String mark = result.getString(3);
                System.out.println(mark);
                String color = result.getString(4);
                System.out.println(color);
                String stat = result.getString(5);
                System.out.println(stat);
                String coment = result.getString(6);
                System.out.println(coment);

                JSONObject resultJSON = new JSONObject();
                resultJSON.put("master_id", master_id);
                resultJSON.put("user_id", user_id);
                resultJSON.put("mark", mark);
                resultJSON.put("color", color);
                resultJSON.put("stat", stat);
                resultJSON.put("coment", coment);

                list.add(resultJSON);
            }
            return list.toJSONString();
        } catch(SQLException e) {
            e.printStackTrace();
            return "-1";
        }
    }

    //получение списка заявок по id мастера
    public String ShowAllOrders (int master_id) {
        try {
            String query = "SELECT * FROM orders WHERE master_id = '" + master_id + "'";
            PreparedStatement prSt = dbConnection.prepareStatement(query);
            ResultSet result =  prSt.executeQuery();
            JSONArray list = new JSONArray();

            while (result.next()) {
                String order_id = result.getString(1);
                System.out.println(order_id);
                String user_id = result.getString(3);
                System.out.println(user_id);
                String mark = result.getString(4);
                System.out.println(mark);
                String color = result.getString(5);
                System.out.println(color);
                String stat = result.getString(6);
                System.out.println(stat);
                String coment = result.getString(7);
                System.out.println(coment);

                JSONObject resultJSON = new JSONObject();
                resultJSON.put("order_id", order_id);
                resultJSON.put("user_id", user_id);
                resultJSON.put("mark", mark);
                resultJSON.put("color", color);
                resultJSON.put("stat", stat);
                resultJSON.put("coment", coment);

                list.add(resultJSON);
            }
            return list.toJSONString();

        } catch(SQLException e) {
            e.printStackTrace();
            return "-1";
        }
    }
}
