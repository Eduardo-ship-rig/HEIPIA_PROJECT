package com.heipia;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class SupabaseClient {

    private static final String SUPABASE_URL = "https://lxrwpuzxeywjjklspxox.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imx4cndwdXp4ZXl3amprbHNweG94Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzEyOTM3MzIsImV4cCI6MjA4Njg2OTczMn0.AlnGwqftgZzgq1JTmKwqZsmTVc4wlNiZ1Vs-P7JfJ8Y";

    // Códigos de error personalizados
    public static final int ERROR_RED = -1;
    public static final int ERROR_TIMEOUT = -2;
    public static final int ERROR_SERVIDOR = -3;
    public static final int ERROR_DATOS = -4;

    public RespuestaGet get(String endpoint) {
        RespuestaGet respuesta = new RespuestaGet();
        HttpURLConnection conn = null;

        try {
            URL url = new URI(SUPABASE_URL + endpoint).toURL();
            conn = (HttpURLConnection) url.openConnection();

            // Configurar timeouts
            conn.setConnectTimeout(5000); // 5 segundos para conectar
            conn.setReadTimeout(5000); // 5 segundos para leer

            conn.setRequestMethod("GET");
            conn.setRequestProperty("apikey", API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");

            int responseCode = conn.getResponseCode();
            String responseBody = obtenerRespuesta(conn, responseCode);

            respuesta.exitosa = true;
            respuesta.codigoHttp = responseCode;
            respuesta.cuerpo = responseBody;

        } catch (SocketTimeoutException e) {
            respuesta.exitosa = false;
            respuesta.codigoError = ERROR_TIMEOUT;
            respuesta.mensajeError = "Tiempo de espera agotado. Verifica tu conexión.";
        } catch (UnknownHostException e) {
            respuesta.exitosa = false;
            respuesta.codigoError = ERROR_RED;
            respuesta.mensajeError = "Sin conexión a internet. No se pudo conectar al servidor.";
        } catch (Exception e) {
            respuesta.exitosa = false;
            respuesta.codigoError = ERROR_SERVIDOR;
            respuesta.mensajeError = "Error inesperado: " + e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return respuesta;
    }

    public RespuestaPost post(String endpoint, String json) {
        RespuestaPost respuesta = new RespuestaPost();
        HttpURLConnection conn = null;

        try {
            URL url = new URI(SUPABASE_URL + endpoint).toURL();
            conn = (HttpURLConnection) url.openConnection();

            // Configurar timeouts
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("apikey", API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Prefer", "return=minimal");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes("utf-8"));
                os.flush();
            }

            int responseCode = conn.getResponseCode();

            respuesta.exitosa = true;
            respuesta.codigoHttp = responseCode;

        } catch (SocketTimeoutException e) {
            respuesta.exitosa = false;
            respuesta.codigoError = ERROR_TIMEOUT;
            respuesta.mensajeError = "Tiempo de espera agotado. Verifica tu conexión.";
        } catch (UnknownHostException e) {
            respuesta.exitosa = false;
            respuesta.codigoError = ERROR_RED;
            respuesta.mensajeError = "Sin conexión a internet. No se pudo conectar al servidor.";
        } catch (Exception e) {
            respuesta.exitosa = false;
            respuesta.codigoError = ERROR_SERVIDOR;
            respuesta.mensajeError = "Error inesperado: " + e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return respuesta;
    }

    private String obtenerRespuesta(HttpURLConnection conn, int responseCode) throws IOException {
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

    // Clases para respuestas
    public static class RespuestaGet {
        public boolean exitosa;
        public int codigoHttp;
        public int codigoError;
        public String cuerpo;
        public String mensajeError;

        public RespuestaGet() {
            this.exitosa = false;
            this.codigoHttp = 0;
            this.codigoError = 0;
            this.cuerpo = "";
            this.mensajeError = "";
        }

        public boolean esErrorRed() {
            return codigoError == ERROR_RED || codigoError == ERROR_TIMEOUT;
        }
    }

    public static class RespuestaPost {
        public boolean exitosa;
        public int codigoHttp;
        public int codigoError;
        public String mensajeError;

        public RespuestaPost() {
            this.exitosa = false;
            this.codigoHttp = 0;
            this.codigoError = 0;
            this.mensajeError = "";
        }

        public boolean esErrorRed() {
            return codigoError == ERROR_RED || codigoError == ERROR_TIMEOUT;
        }
    }
}