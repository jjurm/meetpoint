package com.treecio.meetpoint.kiwi;

import java.io.IOException;
import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.treecio.meetpoint.model.Contributor;
import com.treecio.meetpoint.model.ContributorInput;
import com.treecio.meetpoint.model.ContributorResult;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.treecio.meetpoint.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class FlightRequest implements Contributor {

  private  String flyFrom;
  private  String flyTo;
  private  Date dateFrom;
  private  Date dateTo;

  public static String requestUrl = "https://api.skypicker.com/flights?";

  public FlightRequest(String flyFrom, String flyTo, Date dateFrom, Date dateTo) {
    this.flyFrom = flyFrom;
    this.flyTo = flyTo;
    this.dateFrom = dateFrom;
    this.dateTo = dateTo;
  }
  
  public static void main(String[] args) {
    FlightRequest r = new FlightRequest("Prague", "Budapest", new Date(0), new Date(0));
    FlightRequestResult result = r.request();
    System.out.println("price: " + result.getPrice());
    System.out.println("duration: " + result.getDuration());
  }

  public FlightRequestResult request() {
    Client client = ClientBuilder.newClient();
    StringBuilder r = new StringBuilder();
    r.append(requestUrl);
    r.append("flyFrom=");
    r.append(flyFrom);
    r.append("&to=");
    r.append(flyTo);
    r.append("&dateFrom");
    r.append(Utils.formattedDate(dateFrom));
    r.append("&dateTo");
    r.append(Utils.formattedDate(dateTo));

    String requestString = r.toString();

    Response response = client.target(requestString).request(MediaType.TEXT_PLAIN_TYPE).get();
    
    ObjectMapper mapper = new ObjectMapper();
    JsonNode actualObj = null;
    try {
      actualObj = mapper.readTree(response.readEntity(String.class));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    if(Integer.parseInt(actualObj.get("_results").toString()) == 0)
      try {
        throw new NoFlightException();
      } catch (NoFlightException e) {
        e.printStackTrace();
      }
    
    double price = Double.parseDouble(actualObj.get("data").get(0).findValue("conversion").findValue("EUR").toString());
    
    double duration = Utils.timeToSeconds(actualObj.get("data").get(0).findValue("fly_duration").toString());
    
    return new FlightRequestResult(price, duration);
  }

  @NotNull
  public ContributorResult process(@NotNull ContributorInput cr) {
    FlightRequest t = new FlightRequest(cr.getUser().getOrigin().getName(), cr.getMeeting().getDestination().getName(), cr.getMeeting().getStartDate(), cr.getMeeting().getEndDate());
    FlightRequestResult result = t.request();

    return new ContributorResult(result.getPrice(), 1, 1);
  }
}
