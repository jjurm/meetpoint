package com.jjurm.projects.mpp.map;

import java.util.Calendar;
import java.util.Date;

import com.jjurm.projects.mpp.model.Attendant;
import com.jjurm.projects.mpp.model.Parameters.ParametersList;
import com.jjurm.projects.mpp.model.Place;

public class TemperatureMap extends ProductivityMap {

  public static final String PARAM_KT = "KT";

  public static final double c1 = -42.379;
  public static final double c2 = 2.04901523;
  public static final double c3 = 10.14333127;
  public static final double c4 = -0.22475541;
  public static final double c5 = -0.683783 * Math.pow(10, -3);
  public static final double c6 = -5.481717 * Math.pow(10, -2);
  public static final double c7 = 1.22874 * Math.pow(10, -3);
  public static final double c8 = 8.5280 * Math.pow(10, -4);
  public static final double c9 = -1.99 * Math.pow(10, -6);

  public TemperatureMap(ParametersList parameters, Date date, Attendant attendant) {
    super(parameters, date, attendant);
  }

  @Override
  public double calculateProductivity(Place destination, int day) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DATE, day);
    int month = calendar.get(Calendar.MONTH);

    // dry-bulb temperature
    double tC = destination.getTemperature(month);
    // double tF = celsiusToFahrenheit(tC);

    double P = productivityInTemperature(tC);

    return P;
  }

  public double productivityInTemperature(double t) {
    return ((-1.06907 * Math.pow(10, -7) * Math.pow(t, 4) + 0.00003 * Math.pow(t, 3)
        - 0.00344 * Math.pow(t, 2) + 0.11109 * t - 0.08269) - 1) * parameters.get(PARAM_KT) + 1;
  }

  public static double celsiusToFahrenheit(double v) {
    return v * 9 / 5 + 32;
  }

  public static double fahrenheitToCelsius(double v) {
    return (v - 32) * 5 / 9;
  }

}
