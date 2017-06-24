package com.treecio.meetpoint.kiwi;

import com.treecio.meetpoint.model.Contributor;
import com.treecio.meetpoint.model.ContributorInput;
import com.treecio.meetpoint.model.ContributorResult;
import com.treecio.meetpoint.utils.Utils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class FlightContributor implements Contributor {

    private static final String requestUrl = "https://api.skypicker.com/flights?";

    @NotNull
    public ContributorResult process(@NotNull ContributorInput cr) throws DestinationImpossible {
        Client client = ClientBuilder.newClient();

        String requestString = requestUrl +
                "flyFrom=" +
                cr.getUser().getOrigin().getName() +
                "&to=" +
                cr.getDestination().getName() +
                "&dateFrom" +
                Utils.formattedDate(cr.getMeeting().getStartDate()) +
                "&dateTo" +
                Utils.formattedDate(cr.getMeeting().getEndDate());

        Response response = client.target(requestString).request(MediaType.TEXT_PLAIN_TYPE).get();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = null;
        try {
            actualObj = mapper.readTree(response.readEntity(String.class));
            if (Integer.parseInt(actualObj.get("_results").toString()) == 0) {
                throw new DestinationImpossible();
            }

            double price = Double.parseDouble(actualObj.get("data").get(0).findValue("conversion").findValue("EUR").toString());
            double duration = Utils.timeToSeconds(actualObj.get("data").get(0).findValue("fly_duration").toString());

            return new ContributorResult(price, 1, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ContributorResult.Companion.getDefault();

    }
}