package com.jjurm.projects.mpp.db;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.TimeZone;

import org.apache.ibatis.jdbc.ScriptRunner;

import com.google.maps.ElevationApi;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.TimeZoneApi;
import com.google.maps.errors.ApiException;
import com.google.maps.errors.ZeroResultsException;
import com.google.maps.model.ElevationResult;
import com.google.maps.model.LatLng;
import com.jjurm.projects.mpp.api.ApiManager;
import com.jjurm.projects.mpp.api.BingApi;

public class CitiesImporter {

  static String filename = "cities.csv";
  static int ignoreLines = 1;

  public static void run() {
    DatabaseManager.init();
    // importCities();
    // fetchTimezones();
    fetchElevations();
    DatabaseManager.release();
  }

  public static void importCities() {

    String insert2Statement =
        "INSERT INTO `bigcities`(`country`, `city`, `accent`, `region`, `population`, `lat`, `lon`) VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (BufferedReader brCsv = new BufferedReader(new FileReader(filename));
        BufferedReader brCitiesSql = new BufferedReader(new FileReader("sql/cities.sql"));
        Connection conn = DatabaseManager.getConnection();) {

      ScriptRunner sr = new ScriptRunner(conn);
      sr.runScript(brCitiesSql);

      conn.setAutoCommit(false);

      try (PreparedStatement stmt2 = conn.prepareStatement(insert2Statement)) {

        for (int i = 0; i < ignoreLines; i++) {
          brCsv.readLine();
        }
        String line;
        String[] parts;
        Integer population;
        while ((line = brCsv.readLine()) != null) {
          parts = line.split(",");

          if (parts[4].length() > 0) {
            population = Integer.parseInt(parts[4]);
          } else {
            population = null;
          }

          if (population != null && population > 50000) {
            stmt2.setString(1, parts[0]);
            stmt2.setString(2, parts[1]);
            stmt2.setString(3, parts[2]);
            stmt2.setString(4, parts[3]);
            stmt2.setInt(5, population);
            stmt2.setDouble(6, Double.parseDouble(parts[5]));
            stmt2.setDouble(7, Double.parseDouble(parts[6]));
            stmt2.executeUpdate();
          }
        }
        conn.commit();

      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void fetchTimezones() {
    try (Connection conn = DatabaseManager.getConnection()) {
      conn.setAutoCommit(false);

      try (
          PreparedStatement stmtSelect =
              conn.prepareStatement("SELECT id, lat, lon FROM bigcities");
          PreparedStatement stmtUpdate =
              conn.prepareStatement("UPDATE bigcities SET tz_id = ?, alt = ? WHERE id = ?");
          ResultSet result = stmtSelect.executeQuery()) {

        ApiFunctionProcessor<TimeZone> pTimeZone = new ApiFunctionProcessor<>(
            TimeZoneApi::getTimeZone, tz -> stmtUpdate.setString(1, tz.getID()),
            () -> stmtUpdate.setNull(1, Types.VARCHAR));
        ApiFunctionProcessor<ElevationResult> pElevation =
            new ApiFunctionProcessor<ElevationResult>(ElevationApi::getByPoint,
                er -> stmtUpdate.setDouble(2, er.elevation),
                () -> stmtUpdate.setNull(1, Types.DOUBLE));

        int i = 0;
        while (result.next()) {
          if (i % 100 == 0) {
            conn.commit();
            System.out.println(i);
          }
          LatLng point = new LatLng(result.getDouble("lat"), result.getDouble("lon"));
          stmtUpdate.setInt(3, result.getInt("id"));
          pTimeZone.process(point);
          pElevation.process(point);
          stmtUpdate.executeUpdate();
          i++;
        }

        conn.commit();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (ApiException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public static void fetchElevations() {
    try (Connection conn = DatabaseManager.getConnection()) {
      conn.setAutoCommit(false);

      try (
          PreparedStatement stmtSelect =
              conn.prepareStatement("SELECT id, lat, lon FROM bigcities");
          PreparedStatement stmtUpdate =
              conn.prepareStatement("UPDATE bigcities SET alt = ? WHERE id = ?");
          ResultSet result = stmtSelect.executeQuery()) {

        int i = 0;
        int count = 0;
        int[] ids = new int[100];
        double[][] points = new double[100][2];
        while (result.next() && i < 150) {
          if (count == 40) {
            int[] elevations = BingApi.fetchElevation(points, count);
            for (int j = 0; j < count; j++) {
              stmtUpdate.setInt(2, ids[j]);
              stmtUpdate.setInt(1, elevations[j]);
              stmtUpdate.executeUpdate();
            }
            count = 0;

            conn.commit();
            System.out.println(i);
          }
          ids[count] = result.getInt(1);
          points[count][0] = result.getDouble(2);
          points[count][1] = result.getDouble(3);
          i++;
          count++;
        }

        int[] elevations = BingApi.fetchElevation(points, count);
        for (int j = 0; j < count; j++) {
          stmtUpdate.setInt(2, ids[0]);
          stmtUpdate.setInt(1, elevations[j]);
          stmtUpdate.executeUpdate();
        }

        conn.commit();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  private static class ApiFunctionProcessor<T> {

    private ApiFunction<T> function;
    private ResultConsumer<T> onSuccess;
    private NoResultAction action;

    public ApiFunctionProcessor(ApiFunction<T> function, ResultConsumer<T> onSuccess,
        NoResultAction action) {
      this.function = function;
      this.onSuccess = onSuccess;
      this.action = action;
    }

    public void process(LatLng point)
        throws ApiException, InterruptedException, IOException, SQLException {
      PendingResult<T> result = function.query(ApiManager.getContext(), point);
      try {
        T r = result.await();
        onSuccess.accept(r);
      } catch (ZeroResultsException e) {
        action.run();
      }
    }

    private static interface ApiFunction<T> {
      public PendingResult<T> query(GeoApiContext context, LatLng point);
    }

    private static interface ResultConsumer<T> {
      public void accept(T r) throws SQLException;
    }

    private static interface NoResultAction {
      public void run() throws SQLException;
    }

  }

}
