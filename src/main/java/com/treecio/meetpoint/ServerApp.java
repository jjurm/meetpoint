package com.treecio.meetpoint;

import com.treecio.meetpoint.db.DatabaseManager;
import com.treecio.meetpoint.server.HttpServer;

public class ServerApp {

    public static void main(String[] args) {

        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void run() throws Exception {


        HttpServer server = new HttpServer();

    }

}
