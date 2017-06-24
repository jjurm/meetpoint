package com.treecio.meetpoint.model

import com.treecio.meetpoint.kiwi.DestinationImpossible
import com.treecio.meetpoint.model.db.User

/**
 * Class to judge destinations and therefore contribute to the result.
 */
interface Contributor {

    @Throws(DestinationImpossible::class)
    fun process(cr: MeetingPossibility, user: User): ContributorResult

}