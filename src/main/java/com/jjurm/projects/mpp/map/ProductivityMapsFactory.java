package com.jjurm.projects.mpp.map;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.jjurm.projects.mpp.model.Attendant;
import com.jjurm.projects.mpp.model.Parameters;
import com.jjurm.projects.mpp.model.Parameters.ParametersList;

public class ProductivityMapsFactory {

  private static HashMap<Class<? extends ProductivityMap>, Factory> allFactories =
      new HashMap<Class<? extends ProductivityMap>, Factory>() {
        private static final long serialVersionUID = 1L;
        {
          put(AltitudeMap.class, AltitudeMap::new);
          put(DaylightMap.class, DaylightMap::new);
          put(DistanceMap.class, DistanceMap::new);
          put(JetLagMap.class, JetLagMap::new);
          put(PollutionMap.class, PollutionMap::new);
          put(PrecipitationMap.class, PrecipitationMap::new);
          put(QualityOfLifeMap.class, QualityOfLifeMap::new);
          put(TemperatureMap.class, TemperatureMap::new);
        }
      };


  List<Class<? extends ProductivityMap>> classes =
      new ArrayList<Class<? extends ProductivityMap>>();
  Parameters parameters;

  public ProductivityMapsFactory(Parameters parameters) {
    this.parameters = parameters;
  }

  public void addFactory(Class<? extends ProductivityMap> clazz) {
    classes.add(clazz);
  }

  public ProductivityMap[] produce(Date date, Attendant attendant) {
    ProductivityMap[] maps = new ProductivityMap[classes.size()];
    int i = 0;
    for (Class<? extends ProductivityMap> clazz : classes) {
      ParametersList list = parameters.getParametersList(clazz);
      maps[i] = allFactories.get(clazz).construct(list, date, attendant);
      i++;
    }
    return maps;
  }

  static interface Factory {

    public ProductivityMap construct(ParametersList parameters, Date date, Attendant attendant);

  }

}
