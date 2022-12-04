import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.SQLException;

public class Server {
    private HttpServer _server = null;

    public Server () throws IOException {
        _server = HttpServer.create(new InetSocketAddress("localhost", 8001), 0);
        System.out.println("server started at port 8001 (localhost)");

        _server.createContext("/", new MyHttpHandler() {
            @Override
            public int HandleHtml(String request, StringBuilder answer, String request_url) {
                return 0;
            }
        });
        _server.createContext("/login", new LoginHandler()); //вход
        _server.createContext("/reg", new NewUserHandler()); //регистрация
        _server.createContext("/edit", new EditUserHandler()); //редактирование профиля
        _server.createContext("/neworder", new NewOrderHandler()); //создание новой заявки
        _server.createContext("/editorder", new NewOrderHandler()); //редактирование заявки
    }

    public void start() {
        _server.start();
    }

    abstract static class MyHttpHandler implements HttpHandler {

        public abstract int HandleHtml(String request, StringBuilder answer, String request_url);

        public void handle (HttpExchange t) throws IOException {
            String reguest_url = t.getRequestURI().toString();
            System.out.println("Reguest URL : " + reguest_url);
            String requestMethod = t.getRequestMethod();
            InputStream is;
            String requestObj = "";
            if (requestMethod.equalsIgnoreCase("POST")) {
                try {
                    StringBuilder requestBuffer = new StringBuilder();
                    is = t.getRequestBody();
                    int rByte;
                    while ((rByte = is.read()) != -1) {
                        requestBuffer.append((char) rByte);
                    }
                    is.close();
                    if (requestBuffer.length() > 0) {
                        requestObj = URLDecoder.decode(requestBuffer.toString(), "UTF-8");
                    } else {
                        requestObj = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String response = "Error";
            StringBuilder answer = new StringBuilder();
            HandleHtml(requestObj, answer, reguest_url);
            response = answer.toString();

            ByteBuffer buffer = Charset.forName("UTF-8").encode(response);
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            try {
                os.write(bytes);
            } catch(Exception e) {
                System.out.println(e.getMessage());
            }
            os.close();
        }
    }

    //авторизация пользователя
    public static class LoginHandler extends MyHttpHandler {
        private UserDB _dataBase;
        @Override
        public int HandleHtml(String request, StringBuilder answer, String request_url) {
            try {
                _dataBase = new UserDB();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(request);
            String obrabotka = ParceRequest(request);
            System.out.println(obrabotka);
            answer.append(obrabotka);
            return 200;
        }

        private String ParceRequest (String request) {
            JSONObject answer = new JSONObject();
            answer.put("requestID", "0");
            answer.put("answer", "server error!");
            String result = answer.toJSONString();
            try {
                Object obj = new JSONParser().parse(request);
                JSONObject req = (JSONObject) obj;
                String mod = (String) req.get("mod");

                if (mod.contains("CheckUserAuth")) {
                    String login = (String) req.get("login");
                    String password = (String) req.get("password");
                    System.out.println("user: " + login + " " + password);
                    String resultDataBase =_dataBase.CheckUserAuth(login, password);
                    if ( resultDataBase != ("-1")) {
                        answer.put("requestID", "1");
                        answer.put("answer", resultDataBase);
                        result = answer.toJSONString();
                    } else {
                        answer.put("requestID", "0");
                        answer.put("answer", "auth error!");
                        result = answer.toJSONString();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    //регистрация пользователя
    public static class NewUserHandler extends MyHttpHandler {
        UserDB _dataBase;
        @Override
        public int HandleHtml(String request, StringBuilder answer, String request_url) {
            try {
                _dataBase = new UserDB();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(request);
            String obrabotka = ParceRequest(request);
            System.out.println(obrabotka);
            answer.append(obrabotka);
            return 200;
        }

        private String ParceRequest (String request) {
            JSONObject answer = new JSONObject();
            answer.put("answer", "server error!");
            String result = answer.toJSONString();
            try {
                Object obj = new JSONParser().parse(request);
                JSONObject req = (JSONObject) obj;
                String mod = (String) req.get("mod");

                if (mod.contains("NewUserReg")) {
                    String username = (String) req.get("username");
                    String telephone = (String) req.get("telephone");
                    String password = (String) req.get("password");
                    System.out.println("user: " + username + " " + telephone + " " + password);

                    if (_dataBase.NewUser(username, telephone, password)) {
                        answer.put("answer", "you sign in");
                        result = answer.toJSONString();
                    } else {
                        answer.put("answer", "registration error !!!");
                        result = answer.toJSONString();
                    }
                }
            } catch (org.json.simple.parser.ParseException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    //редактирование профиля
    public static class EditUserHandler extends MyHttpHandler {
        UserDB _dataBase;
        @Override
        public int HandleHtml(String request, StringBuilder answer, String request_url) {
            try {
                _dataBase = new UserDB();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(request);
            String obrabotka = ParceRequest(request);
            System.out.println(obrabotka);
            answer.append(obrabotka);
            return 200;
        }

        private String ParceRequest (String request) {
            JSONObject answer = new JSONObject();
            answer.put("answer", "server error!");
            String result = answer.toJSONString();

            try {
                Object obj = new JSONParser().parse(request);
                JSONObject req = (JSONObject) obj;
                String mod = (String) req.get("mod");

                if (mod.contains("EditUser")) {
                    int id = (int) req.get("id");
                    String username = (String) req.get("username");
                    String telephone = (String) req.get("telephone");
                    String password = (String) req.get("password");
                    System.out.println("user with id: " + id + " " + username + " " + telephone + " " + password);

                    if (_dataBase.EditUser(id, username, telephone, password)) {
                        answer.put("answer", "your profile was edited");
                        result = answer.toJSONString();
                    } else {
                        answer.put("answer", "error!");
                        result = answer.toJSONString();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    //создание новой заявки
    public static class NewOrderHandler extends MyHttpHandler {
        OrderDB _dataBase;
        @Override
        public int HandleHtml(String request, StringBuilder answer, String request_url) {
            try {
                _dataBase = new OrderDB();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(request);
            String obrabotka = ParceRequest(request);
            System.out.println(obrabotka);
            answer.append(obrabotka);
            return 200;
        }

        private String ParceRequest (String request) {
            JSONObject answer = new JSONObject();
            answer.put("answer", "server error!");
            String result = answer.toJSONString();

            try {
                Object obj = new JSONParser().parse(request);
                JSONObject req = (JSONObject) obj;
                String mod = (String) req.get("mod");

                if (mod.contains("NewOrder")) {
                    int master_id = (int) req.get("master_id");
                    int user_id = (int) req.get("user_id");
                    String mark = (String) req.get("mark");
                    String color = (String) req.get("color");
                    String status = (String) req.get("status");
                    String comment = (String) req.get("comment");
                    System.out.println("order: " + master_id + " " + user_id + " " + mark + " " + color + ""+ status +""+ comment);

                    if (_dataBase.NewOrder(master_id, user_id, mark, color, status, comment)) {
                        answer.put("answer", "new order was added");
                        result = answer.toJSONString();
                    } else {
                        answer.put("answer", "new order error!");
                        result = answer.toJSONString();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    //редактирование заявки
    public static class EditOrderHandler extends MyHttpHandler {
        OrderDB _dataBase;
        @Override
        public int HandleHtml(String request, StringBuilder answer, String request_url) {
            try {
                _dataBase = new OrderDB();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(request);
            String obrabotka = ParceRequest(request);
            System.out.println(obrabotka);
            answer.append(obrabotka);
            return 200;
        }

        private String ParceRequest (String request) {
            JSONObject answer = new JSONObject();
            answer.put("answer", "server error!");
            String result = answer.toJSONString();

            try {
                Object obj = new JSONParser().parse(request);
                JSONObject req = (JSONObject) obj;
                String mod = (String) req.get("mod");

                if (mod.contains("EditOrder")) {
                    int order_id = (int) req.get("order_id");
                    int master_id = (int) req.get("master_id");
                    int user_id = (int) req.get("user_id");
                    String mark = (String) req.get("mark");
                    String color = (String) req.get("color");
                    String status = (String) req.get("status");
                    String comment = (String) req.get("comment");
                    System.out.println("order with order_id: " + order_id + master_id + " " + user_id + " "
                            + mark + " " + color + ""+ status +""+ comment);

                    if (_dataBase.EditOrder(order_id, master_id, user_id, mark, color, status, comment)) {
                        answer.put("answer", "your profile was edited");
                        result = answer.toJSONString();
                    } else {
                        answer.put("answer", "editorder error!");
                        result = answer.toJSONString();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}
