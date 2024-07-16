package javachatapplication;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpCookie;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChatHandler implements HttpHandler {
    private static List<String> messages = new ArrayList<>();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!isLoggedIn(exchange)) {
            exchange.getResponseHeaders().set("Location", "/login");
            exchange.sendResponseHeaders(302, 0);
            exchange.close();
            return;
        }

        if ("POST".equals(exchange.getRequestMethod())) {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String message = sb.toString().split("=")[1];
            messages.add(message);
        }

        InputStream is = getClass().getResourceAsStream("/resources/chat.html");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        String chatMessages = "";
        for (String message : messages) {
            chatMessages += "<p>" + message + "</p>";
        }

        String fullResponse = response.toString().replace("${messages}", chatMessages);

        exchange.sendResponseHeaders(200, fullResponse.length());
        OutputStream os = exchange.getResponseBody();
        os.write(fullResponse.getBytes(StandardCharsets.UTF_8));
        os.close();
    }

    private boolean isLoggedIn(HttpExchange exchange) {
        List<String> cookies = exchange.getRequestHeaders().get("Cookie");
        if (cookies != null) {
            for (String cookie : cookies) {
                HttpCookie httpCookie = HttpCookie.parse(cookie).get(0);
                if ("sessionId".equals(httpCookie.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
