package com.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Minimal HTTP server serving the Student Registration HTML form and handling submissions.
 */
public class App {

    private static final int DEFAULT_PORT = 8085;

    public static void main(String[] args) throws IOException {
        int port = resolvePort(System.getenv("PORT"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", App::handleRoot);
        server.createContext("/register", App::handleRegister);
        server.createContext("/health", App::handleHealth);
        server.setExecutor(null);
        server.start();
        System.out.println("Student Registration server running on http://localhost:" + port);
    }

    static int resolvePort(String envPort) {
        if (envPort == null || envPort.isBlank()) {
            return DEFAULT_PORT;
        }
        try {
            return Integer.parseInt(envPort.trim());
        } catch (NumberFormatException e) {
            return DEFAULT_PORT;
        }
    }

    static void handleRoot(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "text/plain; charset=UTF-8", "Method Not Allowed");
            return;
        }
        sendResponse(exchange, 200, "text/html; charset=UTF-8", loadIndexHtml());
    }

    static void handleRegister(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Location", "/");
            sendResponse(exchange, 302, "text/plain; charset=UTF-8", "Redirecting");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> fields = parseFormData(body);
        sendResponse(exchange, 200, "text/html; charset=UTF-8", renderConfirmationPage(fields));
    }

    static void handleHealth(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 200, "text/plain; charset=UTF-8", "OK");
    }

    static String loadIndexHtml() throws IOException {
        try (InputStream stream = App.class.getResourceAsStream("/index.html")) {
            if (stream != null) {
                return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        Path localFile = Path.of("index.html");
        if (Files.exists(localFile)) {
            return Files.readString(localFile, StandardCharsets.UTF_8);
        }
        throw new IOException("index.html not found on classpath or working directory");
    }

    static Map<String, String> parseFormData(String body) {
        Map<String, String> result = new LinkedHashMap<>();
        if (body == null || body.isBlank()) {
            return result;
        }
        for (String pair : body.split("&")) {
            int idx = pair.indexOf('=');
            if (idx > 0) {
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                result.put(key, value);
            }
        }
        return result;
    }

    static String renderConfirmationPage(Map<String, String> fields) {
        String name = escapeHtml(fields.getOrDefault("studentName", ""));
        String roll = escapeHtml(fields.getOrDefault("rollNumber", ""));
        String email = escapeHtml(fields.getOrDefault("email", ""));
        String course = escapeHtml(fields.getOrDefault("course", ""));

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>Registration Confirmed</title>
                    <style>
                        body { font-family: Arial, sans-serif; background: #f4f6f8; padding: 40px 16px; }
                        .card { max-width: 440px; margin: 0 auto; background: #fff; padding: 28px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.08); }
                        h1 { margin-top: 0; font-size: 22px; color: #065f46; }
                        p { margin: 8px 0; color: #374151; }
                        a { display: inline-block; margin-top: 16px; color: #2563eb; text-decoration: none; font-weight: bold; }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <h1>Registration Successful</h1>
                        <p><strong>Student Name:</strong> %s</p>
                        <p><strong>Roll Number:</strong> %s</p>
                        <p><strong>Email:</strong> %s</p>
                        <p><strong>Course:</strong> %s</p>
                        <a href="/">Register Another Student</a>
                    </div>
                </body>
                </html>
                """.formatted(name, roll, email, course);
    }

    static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static void sendResponse(HttpExchange exchange, int status, String contentType, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
