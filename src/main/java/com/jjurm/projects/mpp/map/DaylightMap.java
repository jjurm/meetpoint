package com.jjurm.projects.mpp.map;

import java.util.Calendar;
import java.util.Date;

import com.jjurm.projects.mpp.db.QueryCache;
import com.jjurm.projects.mpp.model.Attendant;
import com.jjurm.projects.mpp.model.Parameters.ParametersList;
import com.jjurm.projects.mpp.model.Place;

public class DaylightMap extends ProductivityMap {

  public static final double D = 100.0 / 121.0;

  public static final String PARAM_WH = "WH";

  public static QueryCache<Integer, Double> cache = new QueryCache<Integer, Double>(
      day -> "SELECT declination FROM sundeclination WHERE `day` = " + day, rs -> rs.getDouble(1));

  public DaylightMap(ParametersList parameters, Date date, Attendant attendant) {
    super(parameters, date, attendant);
  }

  @Override
  public double calculateProductivity(Place destination, int day) {
    double phi = destination.getPoint().getLatitude() * Math.PI / 180;

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int dayOfYear = (calendar.get(Calendar.DAY_OF_YEAR) + day) % 366;
    double declination = cache.getValue(dayOfYear) * Math.PI / 180;

    double h = Math.acos(-Math.tan(phi) * Math.tan(declination)) / (2 * Math.PI);
    double sunrise = 24 * -h + 12;
    double sunset = 24 * h + 12;

    double wh = parameters.get(PARAM_WH);
    double RL = Math.max(0, -sunset + sunrise + wh) / wh;

    double P = RL * D + (1 - RL);
    return P;
  }

}
