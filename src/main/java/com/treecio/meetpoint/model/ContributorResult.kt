package com.treecio.meetpoint.model

/**
 * Result of a {@link com.treecio.meetpoint.model.Contributor}
 */
data class ContributorResult (
        var cost: Double,
        var productivity: Double,
        var happiness: Double
) {

    companion object {
        fun createDefault() = ContributorResult(0.0, 1.0, 1.0)
    }

    fun aggregateOnContributors(result: ContributorResult) {
        cost += result.cost
        productivity *= productivity
        happiness *= happiness
    }

    fun aggregateOnUsers(result: ContributorResult) {
        cost += result.cost
        productivity += result.productivity
        happiness += result.happiness
    }

}
