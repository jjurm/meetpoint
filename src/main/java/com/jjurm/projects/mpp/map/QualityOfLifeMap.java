package com.jjurm.projects.mpp.map;

import java.util.Date;

import com.jjurm.projects.mpp.db.SingleQueryCache;
import com.jjurm.projects.mpp.model.Attendant;
import com.jjurm.projects.mpp.model.Parameters.ParametersList;
import com.jjurm.projects.mpp.model.Place;

public class QualityOfLifeMap extends ProductivityMap {

  public static final String PARAM_P_MIN = "P_MIN";

  public QualityOfLifeMap(ParametersList parameters, Date date, Attendant attendant) {
    super(parameters, date, attendant);
  }

  @Override
  public double calculateProductivity(Place destination, int day) {
    double qol = destination.getQualityOfLife();
    double max = cacheMax.getValue();

    double P = Math.pow(parameters.get(PARAM_P_MIN), (max - qol) / max);
    return P;
  }

  private static SingleQueryCache<Double> cache =
      new SingleQueryCache<>("SELECT AVG(qol) as avg FROM qol", r -> r.getDouble(1));

  private static SingleQueryCache<Double> cacheMax =
      new SingleQueryCache<>("SELECT MAX(qol) FROM qol", r -> r.getDouble(1));

  public static double getMeanQOL() {
    return cache.getValue();
  }

}
