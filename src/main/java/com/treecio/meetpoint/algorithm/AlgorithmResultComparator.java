package com.treecio.meetpoint.algorithm;

import com.treecio.meetpoint.model.AlgorithmResult;
import com.treecio.meetpoint.model.ContributorResult;
import com.treecio.meetpoint.model.MeetingPossibility;

public class AlgorithmResultComparator {

    private static double costIndex = 1.5;
    private static double productivityIndex = 1.3;
    private static double happinessIndex = 1;

    public static int compare(AlgorithmResult r1, AlgorithmResult r2) {
        costIndex *= r1.getMeetingPossibility().getMeeting().getPrioBudget()/100;
        productivityIndex *= r1.getMeetingPossibility().getMeeting().getPrioProductivity()/100;
        happinessIndex *= r1.getMeetingPossibility().getMeeting().getPrioHappiness()/100;

        ContributorResult c1 =  r1.getStats();
        ContributorResult c2 = r2.getStats();
        double cost1 = c1.getCost();
        double cost2 = c2.getCost();

        double p1 = c1.getProductivity();
        double p2 = c2.getProductivity();

        double h1 = c1.getHappiness();
        double h2 = c2.getHappiness();

        if(cost1 == 0 || p1 == 0 || h1 == 0) return 1;
        double costDiff = ((-cost1+cost2)/Math.max(cost1, cost2))*costIndex;
        double pDiff = ((p1-p2)/Math.max(p1, p2))*productivityIndex;
        double hDiff = ((h1-h2)/Math.max(h1, h2))*happinessIndex;

        return costDiff + pDiff + hDiff > 0 ? -1 : +1;
    }
}
