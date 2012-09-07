package se.liu.tdp024.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public abstract class HTTPHelper {

    public static String get(String requestedPath, String... parameters) {

        String path = requestedPath;
        try {
            if (parameters != null) {
                path += "?";
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < parameters.length; i += 2) {
                    builder.append(parameters[i]);
                    builder.append("=");
                    builder.append(URLEncoder.encode(parameters[i + 1], "UTF-8"));
                    builder.append("&");
                }
                path += builder.toString();
            }
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        try {
            URL url = new URL(path);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(60000);
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream is = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "utf-8"));

                StringBuilder result = new StringBuilder();
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
                bufferedReader.close();

                return result.toString();

            } else {
                // Server returned HTTP error code.
                return null;
            }
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}
