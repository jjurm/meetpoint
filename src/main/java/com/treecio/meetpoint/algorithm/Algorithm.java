package com.treecio.meetpoint.algorithm;

import com.treecio.meetpoint.db.DatabaseManager;
import com.treecio.meetpoint.kiwi.FlightContributor;
import com.treecio.meetpoint.model.Contributor;
import com.treecio.meetpoint.model.db.City;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Algorithm {

    static final String CITIES_SELECTION = "population > 300000";

    private static final List<Contributor> contributors = new ArrayList<>();

    static {
        contributors.add(new FlightContributor());
    }

    public void process(int meetingId) throws IOException, SQLException {
        Connection conn = DatabaseManager.getConnection();

        // iterate through all cities
        ResultSet rs = conn.prepareStatement("SELECT * FROM cities").executeQuery();

        while (rs.next()) {

            City city = City.Companion.fromResultSet(rs);



        }

    }

}
