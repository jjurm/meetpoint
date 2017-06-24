package com.jjurm.projects.mpp.algorithm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeSet;
import java.util.function.Consumer;

import com.jjurm.projects.mpp.db.DatabaseManager;
import com.jjurm.projects.mpp.db.PlaceFinder;
import com.jjurm.projects.mpp.map.ProductivityMap;
import com.jjurm.projects.mpp.map.ProductivityMapsFactory;
import com.jjurm.projects.mpp.model.Attendant;
import com.jjurm.projects.mpp.model.Place;

public class DiscreteAlgorithm extends Algorithm {

  protected Consumer<Double> progressUpdater;

  public DiscreteAlgorithm(int resultCount, Consumer<Double> progressUpdater) {
    super(resultCount);
    this.progressUpdater = progressUpdater;
  }

  protected void updateProgress(double progress) {
    if (progressUpdater != null) {
      progressUpdater.accept(progress);
    }
  }

  @Override
  public TreeSet<Algorithm.Result> find(Date date, Attendant[] attendants,
      ProductivityMapsFactory mapsFactory) {

    updateProgress(0);

    ProductivityMap[][] maps = new ProductivityMap[attendants.length][];

    for (int i = 0; i < attendants.length; i++) {
      maps[i] = mapsFactory.produce(date, attendants[i]);
    }

    TreeSet<Algorithm.Result> results =
        new TreeSet<Algorithm.Result>(new Comparator<Algorithm.Result>() {
          @Override
          public int compare(Result o1, Result o2) {
            if (o2.getProductivitySum() == o1.getProductivitySum())
              return o1.getDestination().getId() - o2.getDestination().getId();
            else
              return o1.getProductivitySum() < o2.getProductivitySum() ? 1 : -1;
          }
        });

    String query = PlaceFinder.QUERY_BASE;
    int rowCount, row = 0;
    try (Connection conn = DatabaseManager.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery(query);) {

      result.last();
      rowCount = result.getRow();
      result.beforeFirst();

      double sum, productivity;

      while (result.next()) {
        Place destination = PlaceFinder.fromResultSet(result);

        sum = 0;
        for (int day = 1; day <= 3; day++) {
          for (int attendant = 0; attendant < attendants.length; attendant++) {
            productivity = 1;
            for (int map = 0; map < maps[attendant].length; map++) {
              productivity *= maps[attendant][map].calculateProductivity(destination, day);
            }
            sum += productivity;
          }
        }

        // System.out.println(destination + " " + sum);
        Algorithm.Result r = new Algorithm.Result(sum, destination);
        if (results.size() < resultCount) {
          results.add(r);
        } else if (sum > results.last().getProductivitySum()) {
          results.pollLast();
          results.add(r);
        }

        row++;
        updateProgress(((double) row) / rowCount);

      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return results;
  }

}
