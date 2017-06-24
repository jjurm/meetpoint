package com.treecio.meetpoint.kiwi;

public class FlightRequestResult {

  /**
   * price in €.
   */
  private double price;
  /**
   * length in seconds
   */
  private double duration;
  
  public FlightRequestResult(double price, double length) {
     this.price = price;
     this.duration = length;
  }
  
  public double getPrice() {
    return price;
  }
  
  public double getDuration() {
    return duration;
  }
}
