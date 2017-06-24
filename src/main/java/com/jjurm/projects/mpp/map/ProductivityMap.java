package com.jjurm.projects.mpp.map;

import java.util.Date;

import com.jjurm.projects.mpp.model.Attendant;
import com.jjurm.projects.mpp.model.Parameters.ParametersList;
import com.jjurm.projects.mpp.model.Place;

/**
 * This abstract class represents a function that basically takes coordinates as an argument and
 * returns the adjusted productivity of an attendant after travelling to the specified location,
 * considering the factor mapped by a particular implementation.
 * 
 * @author JJurM
 */
public abstract class ProductivityMap {

  protected ParametersList parameters;
  protected Date date;
  protected Attendant attendant;

  public ProductivityMap(ParametersList parameters, Date date, Attendant attendant) {
    this.parameters = parameters;
    this.date = date;
    this.attendant = attendant;
  }

  /**
   * Returns productivity given a specified destination and a day. The 0-th day is the day of
   * arrival and the meeting days are from 1 to 3 (inclusive).
   */
  public abstract double calculateProductivity(Place destination, int day);

}
