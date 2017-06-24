package com.treecio.meetpoint.model

import com.treecio.meetpoint.kiwi.DestinationImpossible

/**
 * Class to judge destinations and therefore contribute to the result.
 */
interface Contributor {

    @Throws(DestinationImpossible::class)
    fun process(cr: ContributorInput): ContributorResult

}