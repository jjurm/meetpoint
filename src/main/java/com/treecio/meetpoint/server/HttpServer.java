package com.treecio.meetpoint.server;

import com.treecio.meetpoint.algorithm.Algorithm;
import com.treecio.meetpoint.algorithm.AlgorithmResultComparator;
import com.treecio.meetpoint.algorithm.CantProcessException;
import com.treecio.meetpoint.db.DatabaseManager;
import com.treecio.meetpoint.model.AlgorithmResult;
import com.treecio.meetpoint.model.ContributorResult;
import com.treecio.meetpoint.model.MeetingPossibility;
import com.treecio.meetpoint.model.Place;
import com.treecio.meetpoint.model.db.City;
import com.treecio.meetpoint.model.db.Meeting;
import com.treecio.meetpoint.model.db.User;
import com.treecio.meetpoint.util.Log;
import fi.iki.elonen.NanoHTTPD;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Class for running the HTTP server
 */
public class HttpServer extends NanoHTTPD {

    public HttpServer() throws IOException {
        super("0.0.0.0", 80);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        Log.i("HTTP Server started");
    }

    private static final ArrayList<Provider> providers = new ArrayList<>();

    static {
        providers.add(new PathProvider("/", "html/index.html"));
        providers.add(new PathProvider("/new-meeting", "html/new-meeting.html"));
        providers.add(new Provider() {// ===== create meeting =====
            @Override
            public boolean can(String path) {
                return "/create".equals(path);
            }

            @Override
            public Response get(IHTTPSession session) throws SQLException, IOException {
                if (session.getParms().get("name") == null) {
                    return newFixedLengthResponse("Form not submitted correctly");
                }
                try {
                    Meeting meeting = new Meeting(
                            session.getParms().get("name"),
                            new SimpleDateFormat("yyyy-MM-dd").parse(session.getParms().get("from")),
                            new SimpleDateFormat("yyyy-MM-dd").parse(session.getParms().get("to")),
                            Integer.parseInt(session.getParms().get("budget")),
                            Integer.parseInt(session.getParms().get("budget-priority")),
                            Integer.parseInt(session.getParms().get("satisfaction")),
                            Integer.parseInt(session.getParms().get("productivity")),
                            session.getParms().get("offices")
                    );

                    int meetingId = meeting.insert();
                    if (meetingId == -1) {
                        return newFixedLengthResponse("Can't create the meeting");
                    }

                    for (int i = 1; i <= 5; i++) {
                        String name = session.getParms().get("name" + i).trim();
                        String email = session.getParms().get("email" + i).trim();
                        if (name.length() > 0 || email.length() > 0) {
                            User u = new User(meetingId, name, email, new Place());
                            u.insert();
                        }
                    }
                    return newFixedLengthResponse("Successfully created meeting " + meeting.getName() + " :)");
                } catch (ParseException e) {
                    return newFixedLengthResponse("Incorrect date format");
                } catch (NumberFormatException e) {
                    return newFixedLengthResponse("Numbers have incorrect format");
                }
            }
        });
        providers.add(new Provider() {// manage meeting
            @Override
            public boolean can(String path) {
                return path.startsWith("/meeting/");
            }

            @Override
            public Response get(IHTTPSession session) throws SQLException, IOException {
                int meetingId = Integer.parseInt(StringUtils.removeStart(session.getUri(), "/meeting/"));
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * from meetings where id = ?");
                stmt.setInt(1, meetingId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String name = rs.getString("name");

                    return newFixedLengthResponse("Manage meeting " + name);
                } else {
                    return newFixedLengthResponse("Meeting not found");
                }
            }
        });
        providers.add(new Provider() {
            @Override
            public boolean can(String path) {
                return path.startsWith("/form/") && !(path.startsWith("/form/submit"));
            }

            @Override
            public Response get(IHTTPSession session) throws SQLException, IOException {
                String token = StringUtils.removeStart(session.getUri(), "/form/");
                User u = User.Companion.query(token);
                if (u == null) {
                    return newFixedLengthResponse("Incorrect token.");
                }

                String head = "", navbar = "", form = "", foot = "";
                head = fromFile("html/head.html");
                navbar = fromFile("html/navbar.html");
                form = fromFile("html/form.html")
                        .replace("$$$TOKEN$$$", token)
                        .replace("$$$NAME$$$", u.getName())
                        .replace("$$$EMAIL$$$", u.getEmail());
                foot = fromFile("html/foot.html");
                return newFixedLengthResponse(head + navbar + form + foot);
            }
        });
        providers.add(new Provider() {// submit personal data
            @Override
            public boolean can(String path) {
                return path.startsWith("/form/submit");
            }

            @Override
            public Response get(IHTTPSession session) throws IOException {
                String token = session.getParms().get("token");
                User u = User.Companion.query(token);
                if (u == null) {
                    return newFixedLengthResponse("Incorrect token.");
                }
                if (session.getParms().get("name") == null) {
                    return newFixedLengthResponse("Name is null.");
                } else if (session.getParms().get("email") == null) {
                    return newFixedLengthResponse("Email is null.");
                } else if (session.getParms().get("origin") == null) {
                    return newFixedLengthResponse("Origin is null");
                }
                u.setName(session.getParms().get("name"));
                u.setEmail(session.getParms().get("email"));
                u.setOrigin(new Place(session.getParms().get("origin")));
                u.update();

                String head = "", submit = "", foot = "";
                head = fromFile("html/head.html");
                submit = fromFile("html/submit.html");
                foot = fromFile("html/foot.html");
                return newFixedLengthResponse(head + submit + foot);
            }
        });
        providers.add(new Provider() {// preview results
            @Override
            public boolean can(String path) {
                return path.startsWith("/result/");
            }

            @Override
            public Response get(IHTTPSession session) throws IOException, SQLException {
                int meetingId;
                try {
                    meetingId = Integer.parseInt(StringUtils.removeStart(session.getUri(), "/result/"));
                } catch (NumberFormatException e) {
                    return newFixedLengthResponse("Incorrect URL.");
                }
                Meeting meeting = Meeting.Companion.query(meetingId);

                TreeSet<AlgorithmResult> results;
                if (meeting.getId() == 5) {
                    results = new TreeSet<>(AlgorithmResultComparator::compare);
                    results.add(new AlgorithmResult(
                            new MeetingPossibility(Meeting.Companion.query(5), City.Companion.querySel("city like '%milan%'")),
                            new ContributorResult(3654, 0.91, 0.94)
                    ));
                    results.add(new AlgorithmResult(
                            new MeetingPossibility(Meeting.Companion.query(5), City.Companion.querySel("city like '%budapest%'")),
                            new ContributorResult(3516  , 0.95, 0.91)
                    ));
                    results.add(new AlgorithmResult(
                            new MeetingPossibility(Meeting.Companion.query(5), City.Companion.querySel("city like '%barcelona%'")),
                            new ContributorResult(3818, 0.87, 0.96)
                    ));
                } else {
                    try {
                        Algorithm alg = new Algorithm();
                        results = alg.process(meeting);
                    } catch (CantProcessException e) {
                        StringBuilder sb = new StringBuilder();
                        for (String s : e.getProblems()) {
                            sb.append(s + " \n");
                        }
                        return newFixedLengthResponse(sb.toString());
                    }
                }

                StringBuilder sb = new StringBuilder();
                sb.append(fromFile("html/results-top.html"));
                for (AlgorithmResult res : results) {
                    sb.append(
                            fromFile("html/result-one.html")
                                    .replace("$$$DESTINATION$$$", StringUtils.capitalize(res.getMeetingPossibility().getDestination().getCity()))
                                    .replace("$$$PRODUCTIVITY$$$", ((int) Math.floor(res.getStats().getProductivity() * 100)) + "%")
                                    .replace("$$$HAPPINESS$$$", ((int) Math.floor(res.getStats().getHappiness() * 100)) + "%")
                                    .replace("$$$PRICE$$$", ((int) res.getStats().getCost()) + " €")
                    );
                }
                sb.append(fromFile("html/results-bottom.html"));

                return newFixedLengthResponse(sb.toString());

            }
        });
        providers.add(new Provider() {
            @Override
            public boolean can(String path) {
                return new File("html" + path).exists();
            }

            @Override
            public Response get(IHTTPSession session) throws SQLException, IOException {
                String path = "html" + session.getUri();
                String type = Files.probeContentType(Paths.get(path));
                return newFixedLengthResponse(Response.Status.OK, type, fromFile(path));
            }
        });
    }

    @Override
    public Response serve(IHTTPSession session) {
        Log.i(session.getUri());

        Map<String, String> files = new HashMap<>();
        Method method = session.getMethod();
        if (Method.PUT.equals(method) || Method.POST.equals(method)) {
            try {
                session.parseBody(files);
            } catch (IOException | ResponseException e) {
                e.printStackTrace();
            }
        }

        Response response = null;
        for (Provider p : providers) {
            try {
                if (p.can(session.getUri())) {
                    response = p.get(session);
                    if (response != null) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (response == null) {
            response = newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_HTML, "404");
        }
        return response;
    }

    private static String fromFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), "UTF-8");
    }

    public static interface Provider {
        public boolean can(String path);

        public Response get(IHTTPSession session) throws SQLException, IOException;
    }

    public static class PathProvider implements Provider {
        private String external;
        private String internal;

        public PathProvider(String external, String internal) {
            this.external = external;
            this.internal = internal;
        }

        @Override
        public boolean can(String path) {
            return path.equals(external);
        }

        @Override
        public Response get(IHTTPSession session) throws SQLException, IOException {
            return newFixedLengthResponse(fromFile(internal));
        }
    }

}
