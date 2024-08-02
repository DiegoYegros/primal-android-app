package net.primal.android.articles.feed.ui

import java.time.Instant
import net.primal.android.articles.db.Article
import net.primal.android.attachments.domain.CdnImage
import net.primal.android.core.compose.feed.model.EventStatsUi
import net.primal.android.core.utils.asEllipsizedNpub
import net.primal.android.core.utils.authorNameUiFriendly
import net.primal.android.note.ui.EventZapUiModel
import net.primal.android.note.ui.asEventZapUiModel

data class FeedArticleUi(
    val eventId: String,
    val articleId: String,
    val title: String,
    val content: String,
    val publishedAt: Instant,
    val authorId: String,
    val authorName: String,
    val rawNostrEventJson: String?,
    val isBookmarked: Boolean,
    val stats: EventStatsUi,
    val authorAvatarCdnImage: CdnImage? = null,
    val imageCdnImage: CdnImage? = null,
    val readingTimeInMinutes: Int? = null,
    val eventZaps: List<EventZapUiModel> = emptyList(),
)

fun Article.mapAsFeedArticleUi(): FeedArticleUi {
    return FeedArticleUi(
        eventId = this.data.eventId,
        articleId = this.data.articleId,
        publishedAt = Instant.ofEpochSecond(this.data.publishedAt),
        authorId = this.data.authorId,
        title = this.data.title,
        content = this.data.content,
        rawNostrEventJson = this.data.raw,
        imageCdnImage = this.data.imageCdnImage ?: this.author?.avatarCdnImage,
        authorName = this.author?.authorNameUiFriendly() ?: this.data.authorId.asEllipsizedNpub(),
        authorAvatarCdnImage = this.author?.avatarCdnImage,
        isBookmarked = false,
        stats = EventStatsUi.from(eventStats = this.eventStats, userStats = null),
        readingTimeInMinutes = ((this.data.wordsCount ?: 1) / 200) + 1,
        eventZaps = this.eventZaps.map { it.asEventZapUiModel() },
    )
}