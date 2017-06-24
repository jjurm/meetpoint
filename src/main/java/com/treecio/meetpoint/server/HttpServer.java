package com.treecio.meetpoint.server;

import com.treecio.meetpoint.algorithm.Algorithm;
import com.treecio.meetpoint.db.DatabaseManager;
import com.treecio.meetpoint.model.Place;
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
            public Response get(IHTTPSession session) {
                return newFixedLengthResponse("Welcome, let's create a meeting");
            }
        });
        providers.add(new Provider() {// ===== create meeting =====
            @Override
            public boolean can(String path) {
                return "/create".equals(path);
            }
            @Override
            public Response get(IHTTPSession session) throws SQLException, IOException {
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO meetings (name) VALUES (?)");
                String name = session.getParms().get("name");
                stmt.setString(1, name);
                stmt.execute();

                return newFixedLengthResponse("Created meeting " + name + "!");
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

                    return newFixedLengthResponse("Manage meeting "+name);
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
                String token = StringUtils.removeStart("/form/", session.getUri());
                User u = User.Companion.query(token);

                String head = "", navbar = "", form = "", foot = "";
                head = fromFile("html/head.html");
                navbar = fromFile("html/navbar.html");
                form = fromFile("html/form.html");
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
                String token = StringUtils.removeStart(session.getUri(), "/form/submit/");
                User u = User.Companion.query(token);
                if(session.getParms().get("name") == null) {
                    return newFixedLengthResponse("Name is null.");
                } else if (session.getParms().get("email") == null){
                    return newFixedLengthResponse("Email is null.");
                } else if (session.getParms().get("origin") == null) {
                    return newFixedLengthResponse("Origin is null");
                }
                u.setName(session.getParms().get("name"));
                u.setEmail(session.getParms().get("email"));
                u.setOrigin(new Place(session.getParms().get("origin")));
                u.insert();

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
            public Response get(IHTTPSession session) {
                int meetingId = Integer.parseInt(StringUtils.removeStart(session.getUri(), "/result/"));
                Algorithm alg = new Algorithm();
                /*try {
                    alg.process(meetingId);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }*/
                return null;
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
                Response r = newFixedLengthResponse(fromFile(path));
                r.addHeader("Content-Type", type);
                return r;
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
            response = newFixedLengthResponse("404");
        }
        return response;
    }

    private static String fromFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), "UTF-8");
    }

    public interface Provider {
        public boolean can(String path);

        public Response get(IHTTPSession session) throws SQLException, IOException;
    }

}
