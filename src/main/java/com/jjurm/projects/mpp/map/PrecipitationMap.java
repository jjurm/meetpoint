package com.jjurm.projects.mpp.map;

import java.util.Calendar;
import java.util.Date;

import com.jjurm.projects.mpp.model.Attendant;
import com.jjurm.projects.mpp.model.Parameters.ParametersList;
import com.jjurm.projects.mpp.model.Place;

public class PrecipitationMap extends ProductivityMap {

  public static final String PARAM_K = "K";

  public static final double P_DECREASE = 0.01363;

  public PrecipitationMap(ParametersList parameters, Date date, Attendant attendant) {
    super(parameters, date, attendant);
  }

  @Override
  public double calculateProductivity(Place destination, int day) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DATE, day);
    int month = calendar.get(Calendar.MONTH);

    double x = destination.getPrecipitation(month);
    double K = parameters.get(PARAM_K);

    double P = Math.pow(1 - P_DECREASE, -K * Math.sqrt(x) / 25.4);
    return P;
  }

}
