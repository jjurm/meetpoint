package com.treecio.meetpoint.server;

import com.treecio.meetpoint.algorithm.Algorithm;
import com.treecio.meetpoint.db.DatabaseManager;
import com.treecio.meetpoint.model.db.User;
import com.treecio.meetpoint.util.Log;
import fi.iki.elonen.NanoHTTPD;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for running the HTTP server
 */
public class HttpServer extends NanoHTTPD {

    public HttpServer() throws IOException {
        super(8080);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        Log.i("HTTP Server started");
    }

    private static final ArrayList<Provider> providers = new ArrayList<>();

    static {
        providers.add(new Provider() {
            @Override
            public boolean can(String path) {// ===== create meeting =====
                return "/".equals(path);
            }

            @Override
            public String get(IHTTPSession session) {
                return "Welcome, let's create a meeting";
            }
        });
        providers.add(new Provider() {// ===== create meeting =====
            @Override
            public boolean can(String path) {
                return "/create".equals(path);
            }
            @Override
            public String get(IHTTPSession session) throws SQLException, IOException {
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO meetings (name) VALUES (?)");
                String name = session.getParms().get("name");
                stmt.setString(1, name);
                stmt.execute();

                return "Created meeting " + name + "!";
            }
        });
        providers.add(new Provider() {// manage meeting
            @Override
            public boolean can(String path) {
                return path.startsWith("/meeting/");
            }

            @Override
            public String get(IHTTPSession session) throws SQLException, IOException {
                int meetingId = Integer.parseInt(StringUtils.removeStart(session.getUri(), "/meeting/"));
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * from meetings where id = ?");
                stmt.setInt(1, meetingId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String name = rs.getString("name");

                    return "Manage meeting "+name;
                } else {
                    return "Meeting not found";
                }
            }
        });
        providers.add(new Provider() {
            @Override
            public boolean can(String path) {
                return path.startsWith("/form/");
            }
            @Override
            public String get(IHTTPSession session) throws SQLException {
                String token = StringUtils.removeStart("/form/", session.getUri());
                User u = User.Companion.query(token);

                return null;
            }
        });
        providers.add(new Provider() {// submit personal data
            @Override
            public boolean can(String path) {
                return path.startsWith("/submit/");
            }
            @Override
            public String get(IHTTPSession session) {
                String token = StringUtils.removeStart("/form/", session.getUri());
                User u = User.Companion.query(token);
                return null;
            }
        });
        providers.add(new Provider() {// preview results
            @Override
            public boolean can(String path) {
                return path.startsWith("/result/");
            }
            @Override
            public String get(IHTTPSession session) {
                int meetingId = Integer.parseInt(StringUtils.removeStart(session.getUri(), "/result/"));
                Algorithm alg = new Algorithm();
                try {
                    alg.process(meetingId);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
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

        String response = null;
        for (Provider p : providers) {
            try {
                if (p.can(session.getUri())) {
                    response = p.get(session);
                    if (response != null) {
                        break;
                    }
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
        if (response == null) {
            response = "404";
        }
        return newFixedLengthResponse(response);
    }

    public interface Provider {
        public boolean can(String path);

        public String get(IHTTPSession session) throws SQLException, IOException;
    }

}
