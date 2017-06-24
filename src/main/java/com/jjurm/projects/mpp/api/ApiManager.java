package com.jjurm.projects.mpp.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.google.maps.GeoApiContext;

public class ApiManager {

  static GeoApiContext context = null;

  public static GeoApiContext getContext() {
    if (context == null) {
      try (BufferedReader br = new BufferedReader(new FileReader("ApiKey.txt"))) {
        String line = br.readLine();
        context = new GeoApiContext().setApiKey(line);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return context;
  }

  static String bingApiKey = null;

  public static String getBingApiKey() {
    if (bingApiKey == null) {
      try {
        bingApiKey = FileUtils.readFileToString(new File("BingApiKey.txt"), "UTF-8");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return bingApiKey;
  }

}
