package com.treecio.meetpoint.utils;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Utils {

  public static String formattedDate(Date date) {
    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    String day = formatter.format(date);
    return day;
  }
  
  public static double timeToSeconds(String time) {
    time = time.replaceAll("[^ \\d]", "");
    String[] parts = time.split(" ");
    double sec = Integer.parseInt(parts[0])*60*60 + Integer.parseInt(parts[1])*60;
    
    return sec;
  }
}
