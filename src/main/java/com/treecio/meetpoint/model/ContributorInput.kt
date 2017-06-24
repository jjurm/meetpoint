package com.treecio.meetpoint.model

import com.treecio.meetpoint.model.db.Meeting
import com.treecio.meetpoint.model.db.User

/**
 * A file of input parameters to one evaluation of a Contributor
 */
data class ContributorInput(
        val meeting : Meeting,
        val user: User,
        val destination: Place
)