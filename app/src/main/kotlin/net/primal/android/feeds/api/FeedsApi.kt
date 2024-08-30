package net.primal.android.feeds.api

import net.primal.android.feeds.api.model.FeedsResponse
import net.primal.android.nostr.model.primal.content.ContentArticleFeedData

interface FeedsApi {

    suspend fun getFeaturedReadsFeeds(): DvmFeedsResponse

    suspend fun getFeaturedHomeFeeds(): DvmFeedsResponse

    suspend fun getReadsUserFeeds(userId: String): FeedsResponse

    suspend fun setReadsUserFeeds(userId: String, feeds: List<ContentArticleFeedData>)

    suspend fun getHomeUserFeeds(userId: String): FeedsResponse

    suspend fun setHomeUserFeeds(userId: String, feeds: List<ContentArticleFeedData>)
}
