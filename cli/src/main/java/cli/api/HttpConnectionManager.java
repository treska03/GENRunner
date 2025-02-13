package cli.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnectionManager {

    public HttpURLConnection createConnection(String urlString, String requestMethod) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(requestMethod);
        conn.setRequestProperty("Content-Type", "application/json");
        return conn;
    }
}