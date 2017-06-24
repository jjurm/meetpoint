package com.treecio.meetpoint.model

import com.treecio.meetpoint.model.db.Meeting

/**
 * A file of input parameters to one evaluation of a Contributor
 */
data class MeetingPossibility(
        val meeting : Meeting,
        val destination: Place
)