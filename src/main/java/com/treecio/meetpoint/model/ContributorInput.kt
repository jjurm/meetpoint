package com.treecio.meetpoint.model

/**
 * A file of input parameters to one evaluation of a Contributor
 */
data class ContributorInput(
        val user: User,
        val destination: Place
)