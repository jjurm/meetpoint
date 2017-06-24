package com.jjurm.projects.mpp.algorithm;

import java.util.Date;
import java.util.TreeSet;

import com.jjurm.projects.mpp.map.ProductivityMapsFactory;
import com.jjurm.projects.mpp.model.Attendant;
import com.jjurm.projects.mpp.model.Place;

public abstract class Algorithm {

  protected int resultCount;

  public Algorithm(int resultCount) {
    this.resultCount = resultCount;
  }

  public abstract TreeSet<Result> find(Date date, Attendant[] attendants,
      ProductivityMapsFactory mapsFactory);

  public static class Result {

    public static Object[] tableColumns = {"City", "Productivity"};

    private double productivitySum;
    private Place destination;

    public Result(double productivitySum, Place destination) {
      super();
      this.productivitySum = productivitySum;
      this.destination = destination;
    }

    public Object[] getTableRow() {
      return new Object[] {destination, productivitySum};
    }

    public double getProductivitySum() {
      return productivitySum;
    }

    public Place getDestination() {
      return destination;
    }

    @Override
    public String toString() {
      return destination + " (" + productivitySum + ")";
    }

  }

}
