package net.primal.android.premium.domain

sealed class MembershipError {

    data object PlaySubscriptionPurchaseNotFound : MembershipError()

    data class FailedToCancelSubscription(val cause: Throwable) : MembershipError()

    data class FailedToProcessSubscriptionPurchase(val cause: Throwable) : MembershipError()
}
