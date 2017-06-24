package com.jjurm.projects.mpp.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BingApi {

  private static final String ALPHABET =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-";

  private static final String BASE_URL =
      "http://dev.virtualearth.net/REST/v1/Elevation/List?key=" + ApiManager.getBingApiKey();

  public static int[] fetchElevation(double[][] points, int count) {
    // String url = BASE_URL + "&points=" + compressPoints(points, count);
    String url = BASE_URL + "&points=" + concatenatePoints(points, count);
    int[] result = new int[count];
    try {
      JSONObject json = readJsonFromUrl(url);
      JSONArray elevations = json.getJSONArray("resourceSets").getJSONObject(0)
          .getJSONArray("resources").getJSONObject(0).getJSONArray("elevations");
      for (int i = 0; i < count; i++) {
        result[i] = elevations.getInt(i);
      }
      return result;
    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
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

  public static String concatenatePoints(double[][] points, int count) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < count; i++) {
      sb.append(points[i][0] + "," + points[i][1]);
      if (i + 1 < count) {
        sb.append(",");
      }
    }
    return sb.toString();
  }

  public static String compressPoints(double[][] points, int count) {
    int latitude = 0, longitude = 0, dx, dy, newLatitude, newLongitude, index, rem;
    StringBuilder sb = new StringBuilder();
    // double l;

    for (int point = 0; point < count; point++) {

      // step 2
      newLatitude = (int) Math.round(points[point][0] * 100000);
      newLongitude = (int) Math.round(points[point][1] * 100000);

      // step 3
      dy = newLatitude - latitude;
      dx = newLongitude - longitude;
      latitude = newLatitude;
      longitude = newLongitude;

      // step 4 and 5
      dy = (dy << 1) ^ (dy >> 31);
      dx = (dx << 1) ^ (dx >> 31);

      // step 6
      index = ((dy + dx) * (dy + dx + 1) / 2) + dy;

      while (index > 0) {

        // step 7
        rem = index & 31;
        index = (index - rem) / 32;

        // step 8
        if (index > 0)
          rem += 32;

        // step 9
        sb.append(ALPHABET.charAt(rem));
      }
    }

    // step 10
    return sb.toString();
  }

}
