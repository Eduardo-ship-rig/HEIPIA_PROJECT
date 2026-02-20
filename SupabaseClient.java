package HeIPIA;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SupabaseClient {

    private static final String SUPABASE_URL = "https://lxrwpuzxeywjjklspxox.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imx4cndwdXp4ZXl3amprbHNweG94Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzEyOTM3MzIsImV4cCI6MjA4Njg2OTczMn0.AlnGwqftgZzgq1JTmKwqZsmTVc4wlNiZ1Vs-P7JfJ8Y";

    public String get(String endpoint) throws Exception {

        URL url = new URI(SUPABASE_URL + endpoint).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("apikey", API_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");

        int responseCode = conn.getResponseCode();
        String response = obtenerRespuesta(conn, responseCode);

        conn.disconnect();
        return response;
    }

    public int post(String endpoint, String json) throws Exception {

        URL url = new URI(SUPABASE_URL + endpoint).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("apikey", API_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Prefer", "return=minimal");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes("utf-8"));
        }

        int responseCode = conn.getResponseCode();
        conn.disconnect();

        return responseCode;
    }

    private String obtenerRespuesta(HttpURLConnection conn, int responseCode) throws Exception {

        BufferedReader reader;

        if (responseCode >= 200 && responseCode < 300) {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        return response.toString();
    }
}
