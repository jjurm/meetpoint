package com.treecio.meetpoint;

import com.treecio.meetpoint.server.HttpServer;

import java.io.IOException;

public class ServerApp {

    public static void main(String[] args) {

        try {
            HttpServer server = new HttpServer();



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
