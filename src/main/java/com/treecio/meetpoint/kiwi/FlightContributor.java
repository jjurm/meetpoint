package com.treecio.meetpoint.kiwi;

import com.jjurm.projects.mpp.map.JetLagMap;
import com.treecio.meetpoint.model.Contributor;
import com.treecio.meetpoint.model.MeetingPossibility;
import com.treecio.meetpoint.model.ContributorResult;
import com.treecio.meetpoint.model.db.User;
import com.treecio.meetpoint.utils.Utils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URLEncoder;

public class FlightContributor implements Contributor {

    private static final String requestUrl = "https://api.skypicker.com/flights?";

    @NotNull
    public ContributorResult process(@NotNull MeetingPossibility cr, @NotNull User user) throws DestinationImpossible {
        Client client = ClientBuilder.newClient();

        try {
            String requestString = requestUrl +
                    "flyFrom=" +
                    URLEncoder.encode(user.getOrigin().getName(), "UTF-8") +
                    "&to=" +
                    URLEncoder.encode(cr.getDestination().getCity(), "UTF-8") +
                    "&dateFrom=" +
                    Utils.formattedDate(cr.getMeeting().getStartDate()) +
                    "&dateTo=" +
                    Utils.formattedDate(cr.getMeeting().getEndDate());

            Response response = client.target(requestString).request(MediaType.TEXT_PLAIN_TYPE).get();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = null;

            actualObj = mapper.readTree(response.readEntity(String.class));
            if (Integer.parseInt(actualObj.get("_results").toString()) == 0) {
                throw new DestinationImpossible();
            }

            double price = Double.parseDouble(actualObj.get("data").get(0).findValue("conversion").findValue("EUR").toString());
            double duration = Utils.timeToSeconds(actualObj.get("data").get(0).findValue("fly_duration").toString());

            double lngFrom = Double.parseDouble(actualObj.get("data").get(0).findValue("route").findValue("lngFrom").toString());
            double lngTo = Double.parseDouble(actualObj.get("data").get(0).findValue("route").findValue("lngTo").toString());
            double diffhours = Math.abs(lngTo-lngFrom)*4/60;
            double productivity = JetLagMap.calculateProductivity(diffhours);

            return new ContributorResult(price, productivity, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ContributorResult.Companion.createDefault();

    }
}
