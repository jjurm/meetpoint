package com.jjurm.projects.mpp.map;

import java.util.Date;

import com.jjurm.projects.mpp.model.Attendant;
import com.jjurm.projects.mpp.model.Parameters.ParametersList;
import com.jjurm.projects.mpp.model.Place;

public class AltitudeMap extends ProductivityMap {

  public static final double T0 = 288.15;
  public static final double L = 0.0065;
  public static final double p0 = 101325;
  public static final double g = 9.80665;
  public static final double M = 0.0289644;
  public static final double R = 8.31447;

  public static final double a = 3.995;
  public static final double b = 0.7576;
  public static final double c = 14.05537;

  public AltitudeMap(ParametersList parameters, Date date, Attendant attendant) {
    super(parameters, date, attendant);
  }

  @Override
  public double calculateProductivity(Place destination, int day) {
    int h = destination.getAltitude();

    double g0 = Math.log(M * p0 / (R * T0) - b) / Math.log(a) + c;
    double gh =
        Math.log((M * p0 * Math.pow(1 - L * h / T0, g * M / (R * L))) / (R * (T0 - L * h)) - b)
            / Math.log(a) + c;

    return gh / g0;
  }

}
