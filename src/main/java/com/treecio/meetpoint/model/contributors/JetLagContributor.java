package com.treecio.meetpoint.model.contributors;

import com.treecio.meetpoint.kiwi.DestinationImpossible;
import com.treecio.meetpoint.model.Contributor;
import com.treecio.meetpoint.model.ContributorResult;
import com.treecio.meetpoint.model.MeetingPossibility;
import com.treecio.meetpoint.model.db.User;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Pali on 24.06.2017.
 */
public class JetLagContributor implements Contributor{


    @NotNull
    @Override
    public ContributorResult process(@NotNull MeetingPossibility cr, @NotNull User user) throws DestinationImpossible {

        return null;
    }
}
