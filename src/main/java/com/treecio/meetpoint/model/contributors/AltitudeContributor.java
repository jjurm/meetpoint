package com.treecio.meetpoint.model.contributors;

import com.jjurm.projects.mpp.model.Attendant;
import com.jjurm.projects.mpp.model.Parameters;
import com.jjurm.projects.mpp.model.Place;
import com.treecio.meetpoint.kiwi.DestinationImpossible;
import com.treecio.meetpoint.model.Contributor;
import com.treecio.meetpoint.model.ContributorResult;
import com.treecio.meetpoint.model.MeetingPossibility;
import com.treecio.meetpoint.model.db.User;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * Created by Pali on 24.06.2017.
 */
public class AltitudeContributor implements Contributor {
    @NotNull
    @Override
    public ContributorResult process(@NotNull MeetingPossibility cr, @NotNull User user) throws DestinationImpossible {
        int alt = (int)cr.getDestination().getAltitude();
        double prod = calculateProductivity(alt);
        return new ContributorResult(0, prod, 1);
    }

    public static final double T0 = 288.15;
    public static final double L = 0.0065;
    public static final double p0 = 101325;
    public static final double g = 9.80665;
    public static final double M = 0.0289644;
    public static final double R = 8.31447;

    public static final double a = 3.995;
    public static final double b = 0.7576;
    public static final double c = 14.05537;

    public double calculateProductivity(int h) {
        int day = 2;
        double g0 = Math.log(M * p0 / (R * T0) - b) / Math.log(a) + c;
        double gh =
                Math.log((M * p0 * Math.pow(1 - L * h / T0, g * M / (R * L))) / (R * (T0 - L * h)) - b)
                        / Math.log(a) + c;

        return gh / g0;
    }
}
