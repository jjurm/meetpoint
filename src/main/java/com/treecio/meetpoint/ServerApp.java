package com.treecio.meetpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.treecio.meetpoint.model.fb.Message;
import com.treecio.meetpoint.model.fb.MessageRecipient;
import com.treecio.meetpoint.model.fb.MessageRequest;
import com.treecio.meetpoint.server.HttpServer;

import java.io.IOException;

public class ServerApp {

    public static void main(String[] args) {

        try {
            run();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

    }

    private static void run() throws IOException, UnirestException {

        HttpServer server = new HttpServer();

        Object o = new MessageRequest(new MessageRecipient("292658_168073583366495_1215364522_n"), new Message("Hello!"));

        ObjectMapper mapper = new ObjectMapper();
        String output = mapper.writeValueAsString(o);

        System.out.println(output);

        HttpResponse<String> response = Unirest.post(Config.URL).header("Content-Type", "application/json").body(output).asString();

        System.out.println(response.getBody());


    }

}
