package com.treecio.meetpoint.algorithm;

import com.treecio.meetpoint.Config;
import com.treecio.meetpoint.db.DatabaseManager;
import com.treecio.meetpoint.kiwi.DestinationImpossible;
import com.treecio.meetpoint.kiwi.FlightContributor;
import com.treecio.meetpoint.model.*;
import com.treecio.meetpoint.model.db.City;
import com.treecio.meetpoint.model.db.Meeting;
import com.treecio.meetpoint.model.db.User;
import com.treecio.meetpoint.model.db.UserPreference;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Algorithm {

    static final String CITIES_SELECTION = "population > 300000";

    private static final List<Contributor> contributors = new ArrayList<>();

    static {
        contributors.add(new FlightContributor());
    }

    public TreeSet<AlgorithmResult> process(Meeting meeting) throws IOException, SQLException, CantProcessException {
        Connection conn = DatabaseManager.getConnection();

        List<String> problems = new ArrayList<>();

        // find users of the meeting
        List<User> users = new ArrayList<>();
        try (ResultSet rs = DatabaseManager.getFromDatabase("users", "meeting = " + meeting.getId())) {
            while (rs.next()) {
                User u = User.Companion.fromResultSet(rs);
                if (isComplete(u)) {
                    users.add(u);
                } else {
                    problems.add("User " + u.getEmail() + " still hasn't submitted the form.");
                }
            }
        }

        if (problems.size() > 0) {
            throw new CantProcessException(problems);
        }

        // create result-holding object
        TreeSet<AlgorithmResult> results = new TreeSet<AlgorithmResult>(AlgorithmResultComparator::compare);

        // iterate through all cities
        try (ResultSet rs = conn.prepareStatement("SELECT * FROM cities WHERE " + CITIES_SELECTION).executeQuery()) {
            while (rs.next()) {

                City city = City.Companion.fromResultSet(rs);

                MeetingPossibility meetingPossibility = new MeetingPossibility(meeting, city);

                try {
                    ContributorResult usersStats = ContributorResult.Companion.createDefault();
                    for (User user : users) {
                        ContributorResult contributorsStats = ContributorResult.Companion.createDefault();
                        for (Contributor contributor : contributors) {

                            ContributorResult result = contributor.process(meetingPossibility, user);

                            contributorsStats.aggregateOnContributors(result);
                        }
                        usersStats.aggregateOnUsers(contributorsStats);
                    }

                    AlgorithmResult algRes = new AlgorithmResult(meetingPossibility, usersStats);

                    results.add(algRes);
                    while (results.size() > Config.RESULT_COUNT) {
                        results.pollLast();
                    }

                } catch (DestinationImpossible e) {
                    // just proceed to the next one
                }

            }
        }

        return results;

    }

    private boolean isComplete(User u) {
        for (Preference p : Preference.values()) {
            UserPreference found = UserPreference.Companion.query(u, p);
            if (found == null) {
                return false;
            }
        }
        return true;
    }

}
