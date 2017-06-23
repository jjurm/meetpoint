package com.treecio.meetpoint.server;

import com.treecio.meetpoint.util.Log;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;

/**
 * Class for running the HTTP server
 */
public class HttpServer extends NanoHTTPD {

    public HttpServer() throws IOException {
        super(80);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        Log.i("HTTP Server started");
    }

    @Override
    public Response serve(IHTTPSession session) {

        Log.i(session.getUri());

        String response = "ok!";

        return newFixedLengthResponse(response);

    }
}
