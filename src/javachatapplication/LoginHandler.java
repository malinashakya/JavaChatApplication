package javachatapplication;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpCookie;
import java.nio.charset.StandardCharsets;

public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            sendLoginForm(exchange, "");
        } else if ("POST".equals(exchange.getRequestMethod())) {
            handleLogin(exchange);
        }
    }

    private void sendLoginForm(HttpExchange exchange, String message) throws IOException {
        InputStream is = getClass().getResourceAsStream("/resources/login.html");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        String fullResponse = response.toString().replace("${message}", message);

        exchange.sendResponseHeaders(200, fullResponse.length());
        OutputStream os = exchange.getResponseBody();
        os.write(fullResponse.getBytes(StandardCharsets.UTF_8));
        os.close();
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String[] pairs = sb.toString().split("&");
        String username = pairs[0].split("=")[1];
        String password = pairs[1].split("=")[1];

        if ((username.equals(User.USERNAME1) && password.equals(User.PASSWORD1)) ||
            (username.equals(User.USERNAME2) && password.equals(User.PASSWORD2))) {
            String sessionId = "123456";
            HttpCookie cookie = new HttpCookie("sessionId", sessionId);
            exchange.getResponseHeaders().add("Set-Cookie", cookie.toString());
            exchange.getResponseHeaders().set("Location", "/chat");
            exchange.sendResponseHeaders(302, 0);
        } else {
            sendLoginForm(exchange, "Incorrect credentials, please try again.");
        }
        exchange.close();
    }
}
