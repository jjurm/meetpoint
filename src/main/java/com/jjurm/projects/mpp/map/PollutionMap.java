package com.jjurm.projects.mpp.map;

import java.util.Date;

import com.jjurm.projects.mpp.db.SingleQueryCache;
import com.jjurm.projects.mpp.model.Attendant;
import com.jjurm.projects.mpp.model.Parameters.ParametersList;
import com.jjurm.projects.mpp.model.Place;

public class PollutionMap extends ProductivityMap {

  public static final String PARAM_P_MIN = "P_MIN";

  public PollutionMap(ParametersList parameters, Date date, Attendant attendant) {
    super(parameters, date, attendant);
  }

  @Override
  public double calculateProductivity(Place destination, int day) {
    double x = destination.getPollution();

    double P = Math.pow(parameters.get(PARAM_P_MIN), (100 - x) / 100);
    return P;
  }

  private static SingleQueryCache<Double> cache =
      new SingleQueryCache<>("SELECT AVG(pollution) as avg FROM pollution", r -> r.getDouble(1));

  public static double getMeanPollution() {
    return cache.getValue();
  }

}
