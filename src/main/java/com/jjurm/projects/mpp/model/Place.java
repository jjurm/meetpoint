package com.jjurm.projects.mpp.model;

import java.util.TimeZone;

import com.peertopark.java.geocalc.Point;

public class Place {

  int id;
  private String name;
  private String country;
  private Point point;
  private int population;
  private TimeZone timeZone;
  private int altitude;
  private double[] temperature;
  private double[] precipitation;
  private double pollution;
  private double qol;

  public Place(int id, String name, String country, Point point, int population, TimeZone timeZone,
      int altitude, double[] temperature, double[] precipitation, double pollution, double qol) {
    this.id = id;
    this.name = name;
    this.country = country;
    this.point = point;
    this.population = population;
    this.timeZone = timeZone;
    this.altitude = altitude;
    this.temperature = temperature;
    this.precipitation = precipitation;
    this.pollution = pollution;
    this.qol = qol;
  }

  public int getId() {
    return id;
  }

  public Point getPoint() {
    return point;
  }

  public int getPopulation() {
    return population;
  }

  public TimeZone getTimeZone() {
    return timeZone;
  }

  public int getAltitude() {
    return altitude;
  }

  public double getTemperature(int month) {
    return temperature[month];
  }

  public double getPrecipitation(int month) {
    return precipitation[month];
  }

  public double getPollution() {
    return pollution;
  }

  public double getQualityOfLife() {
    return qol;
  }

  @Override
  public String toString() {
    return name + ", " + country;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof Place))
      return false;
    Place p = (Place) obj;
    return id == p.id;
  }

  @Override
  public int hashCode() {
    return id;
  }

}
