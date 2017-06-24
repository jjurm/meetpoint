package com.jjurm.projects.mpp.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.jjurm.projects.mpp.map.AltitudeMap;
import com.jjurm.projects.mpp.map.DaylightMap;
import com.jjurm.projects.mpp.map.DistanceMap;
import com.jjurm.projects.mpp.map.JetLagMap;
import com.jjurm.projects.mpp.map.PollutionMap;
import com.jjurm.projects.mpp.map.PrecipitationMap;
import com.jjurm.projects.mpp.map.ProductivityMap;
import com.jjurm.projects.mpp.map.QualityOfLifeMap;
import com.jjurm.projects.mpp.map.TemperatureMap;
import com.jjurm.projects.mpp.util.Holder;

public class Parameters {

  LinkedHashMap<Class<? extends ProductivityMap>, ParametersList> lists = new LinkedHashMap<>();

  public Parameters() {
    lists.put(JetLagMap.class, new ParametersList(true, p -> {
      p.put(JetLagMap.PARAM_PM, h(0.7));
    }));
    lists.put(DistanceMap.class, new ParametersList(true, p -> {
      p.put(DistanceMap.PARAM_PY, h(0.9));
      p.put(DistanceMap.PARAM_PE, h(0.7));
      p.put(DistanceMap.PARAM_PP, h(0.3));
    }));
    lists.put(AltitudeMap.class, new ParametersList(true, p -> {
    }));
    lists.put(DaylightMap.class, new ParametersList(true, p -> {
      p.put(DaylightMap.PARAM_WH, h(8));
    }));
    lists.put(TemperatureMap.class, new ParametersList(true, p -> {
      p.put(TemperatureMap.PARAM_KT, h(0.05));
    }));
    lists.put(PollutionMap.class, new ParametersList(true, p -> {
      p.put(PollutionMap.PARAM_P_MIN, h(0.96));
    }));
    lists.put(QualityOfLifeMap.class, new ParametersList(true, p -> {
      p.put(QualityOfLifeMap.PARAM_P_MIN, h(0.92));
    }));
    lists.put(PrecipitationMap.class, new ParametersList(true, p -> {
      p.put(PrecipitationMap.PARAM_K, h(0.5));
    }));
  }

  private static Holder<Double> h(double value) {
    return new Holder<Double>(value);
  }

  public Set<Map.Entry<Class<? extends ProductivityMap>, ParametersList>> entrySet() {
    return lists.entrySet();
  }

  public ParametersList getParametersList(Class<? extends ProductivityMap> clazz) {
    return lists.get(clazz);
  }

  public static class ParametersList {

    private boolean useThisMap;
    private LinkedHashMap<String, Holder<Double>> parameters;

    public ParametersList(boolean useThisMap,
        Consumer<LinkedHashMap<String, Holder<Double>>> paramSetter) {
      this.useThisMap = useThisMap;
      this.parameters = new LinkedHashMap<String, Holder<Double>>();
      paramSetter.accept(this.parameters);
    }

    public void setUseThisMap(boolean value) {
      this.useThisMap = value;
    }

    public boolean getUseThisMap() {
      return useThisMap;
    }

    public Set<Map.Entry<String, Holder<Double>>> entrySet() {
      return parameters.entrySet();
    }

    public Double get(String key) {
      return parameters.get(key).get();
    }

  }

}
