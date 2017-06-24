package com.jjurm.projects.mpp.map;

import java.util.Date;

import com.jjurm.projects.mpp.model.Attendant;
import com.jjurm.projects.mpp.model.Parameters.ParametersList;
import com.jjurm.projects.mpp.model.Place;

public class JetLagMap extends ProductivityMap {

  public static final String PARAM_PM = "P_M";

  public static final double LE = 0.7;
  public static final double LW = 0.7;

  public JetLagMap(ParametersList parameters, Date date, Attendant attendant) {
    super(parameters, date, attendant);
  }

  @Override
  public double calculateProductivity(Place destination, int day) {
    double pm = parameters.get(PARAM_PM);

    double pw = pm * Math.sqrt(LE / LW);
    double pe = pm * Math.sqrt(LW / LE);

    int offset1 = attendant.getOrigin().getTimeZone().getOffset(date.getTime());
    int offset2 = destination.getTimeZone().getOffset(date.getTime());
    double hourDiff = (offset2 - offset1) / 3600000;

    boolean toEast = hourDiff >= 0;
    hourDiff = Math.abs(hourDiff);

    double dayCoefficient1 = toEast ? 2 : 2.5;
    double p1 = toEast ? pe : pw;
    double productivity1 =
        1 - Math.pow(Math.max(0, hourDiff - dayCoefficient1 * (day - 1)) / 12, 2) * (1 - p1);

    double dayCoefficient2 = !toEast ? 2 : 2.5;
    double p2 = !toEast ? pe : pw;
    double productivity2 =
        1 - Math.pow(Math.max(0, hourDiff - dayCoefficient2 * (day - 1)) / 12, 2) * (1 - p2);

    return Math.max(productivity1, productivity2);
  }

}
