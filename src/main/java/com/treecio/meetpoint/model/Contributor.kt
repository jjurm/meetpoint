package com.treecio.meetpoint.model

/**
 * Class to judge destinations and therefore contribute to the result.
 */
interface Contributor {

    fun process(cr: ContributorInput): ContributorResult

}