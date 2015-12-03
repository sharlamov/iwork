package com.reporting.util;

import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharlamov on 04.11.2015.
 */
public class MapUtil {

    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static double[] getGeoCode(String country, String district, String place) {
        double[] coordinates = null;
        try {
            String path = (country + " " + district + " " + place).replace(" ", "%20");
            JSONObject json = readJsonFromUrl("http://maps.google.com/maps/api/geocode/json?address=" + path + "&sensor=false");
            /*if(json.get("status").equals("ZERO_RESULTS")){
                path = (country + " " + place).replace(" ", "%20");
                json = readJsonFromUrl("http://maps.google.com/maps/api/geocode/json?address=" + path + "&sensor=false");
            }*/
            JSONArray jarr = json.getJSONArray("results");

            for (int i = 0; i < jarr.length(); i++) {
                json = jarr.getJSONObject(i);
                JSONObject locations = json.getJSONObject("geometry").getJSONObject("location");
                coordinates = new double[2];
                coordinates[0] = locations.getDouble("lat");
                coordinates[1] = locations.getDouble("lng");
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return coordinates;
    }
}
