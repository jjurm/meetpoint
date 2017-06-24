package com.jjurm.projects.mpp.model;

/**
 * A class representing a person going to participate in the meeting.
 * 
 * @author JJurM
 */
public class Attendant {

  private Place origin;
  private double age;

  public Attendant(Place origin, double age) {
    this.origin = origin;
    this.age = age;
  }

  public Place getOrigin() {
    return origin;
  }

  public double getAge() {
    return age;
  }

  @Override
  public String toString() {
    return origin + " (age " + ((int) age) + ")";
  }

}
