package com.treecio.meetpoint.utils;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Utils {

  public static String formattedDate(Date date) {
    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    return formatter.format(date);
  }
  
  public static double timeToSeconds(String time) {
    time = time.replaceAll("[^ \\d]", "");
    String[] parts = time.split(" ");

    return (double) (Integer.parseInt(parts[0])*60*60 + Integer.parseInt(parts[1])*60);
  }
}
