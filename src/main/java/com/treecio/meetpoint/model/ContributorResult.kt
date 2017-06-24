package com.treecio.meetpoint.model

/**
 * Result of a {@link com.treecio.meetpoint.model.Contributor}
 */
data class ContributorResult (
        val cost: Double,
        val productivity: Double,
        val happiness: Double
) {

    companion object {
        val default = ContributorResult(0.0, 1.0, 1.0)
    }

}
