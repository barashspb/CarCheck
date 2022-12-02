import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

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
}
