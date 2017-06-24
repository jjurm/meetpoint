package com.jjurm.projects.mpp.map;

import java.util.Date;

import com.jjurm.projects.mpp.model.Attendant;
import com.jjurm.projects.mpp.model.Parameters.ParametersList;
import com.jjurm.projects.mpp.model.Place;
import com.peertopark.java.geocalc.EarthCalc;

public class DistanceMap extends ProductivityMap {

  public static final double AGE_YOUNG = 18;
  public static final double AGE_ELDERLY = 80;
  public static final double MAX_DISTANCE = 20_000_000;

  public static final String PARAM_PY = "P_Y (" + ((int) AGE_YOUNG) + ")";
  public static final String PARAM_PE = "P_E (" + ((int) AGE_ELDERLY) + ")";
  public static final String PARAM_PP = "P_P";

  private double PM;

  public DistanceMap(ParametersList parameters, Date date, Attendant attendant) {
    super(parameters, date, attendant);
    double PY = parameters.get(PARAM_PY);
    double PE = parameters.get(PARAM_PE);
    PM = PY - (attendant.getAge() - AGE_YOUNG) / (AGE_ELDERLY - AGE_YOUNG) * (PY - PE);
  }

  @Override
  public double calculateProductivity(Place destination, int day) {
    if (day > 1)
      return 1;

    double distance =
        EarthCalc.getHarvesineDistance(attendant.getOrigin().getPoint(), destination.getPoint());

    double popAddition =
        parameters.get(PARAM_PP) * Math.sqrt(destination.getPopulation()) / 1000000;

    double P = Math.exp(distance * Math.log(PM) / MAX_DISTANCE) + popAddition;
    return P;
  }
}
