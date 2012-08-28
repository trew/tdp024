package se.liu.tdp024.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class Monlog {

    private static final String MONLOG_ENDPOINT = "http://www.ida.liu.se/~TDP024/monlog/api/log/";
    private static final String API_KEY = "423b0ef8aa9b0e030e785a63262c06c81d1beaa7";
    private static final String REQUEST_URL = MONLOG_ENDPOINT + "?api_key=" + API_KEY + "&format=json";

    
    public static void log(int severity, String shortDescription, String longDescription) {

        StringBuilder dataBuilder = new StringBuilder();
        dataBuilder.append("{");
        dataBuilder.append("\"").append("severity").append("\":").append(severity).append(",");
        dataBuilder.append("\"").append("timestamp").append("\":\"").append(Calendar.getInstance().getTimeInMillis()).append("\",");
        dataBuilder.append("\"").append("short_desc").append("\":\"").append(shortDescription).append("\",");
        dataBuilder.append("\"").append("long_desc").append("\":\"").append(longDescription).append("\"");
        dataBuilder.append("}");

        try {

            URL url = new URL(REQUEST_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(60000);
            connection.setRequestMethod("POST");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
            writer.write(dataBuilder.toString());
            writer.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK || connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();
                
                System.out.println(builder.toString());

            } else {

                System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage());
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();
                
                System.out.println(builder.toString());


            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
