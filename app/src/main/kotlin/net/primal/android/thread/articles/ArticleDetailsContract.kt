package net.primal.android.thread.articles

import net.primal.android.attachments.domain.CdnImage
import net.primal.android.core.compose.feed.model.FeedPostUi
import net.primal.android.nostr.utils.Naddr
import net.primal.android.note.ui.EventZapUiModel

interface ArticleDetailsContract {
    data class UiState(
        val naddr: Naddr? = null,
        val eventId: String? = null,
        val title: String = "",
        val coverCdnImage: CdnImage? = null,
        val summary: String? = null,
        val timestamp: Long? = null,
        val markdownContent: String = "",
        val authorId: String? = null,
        val authorCdnImage: CdnImage? = null,
        val authorDisplayName: String? = null,
        val authorInternetIdentifier: String? = null,
        val referencedNotes: List<FeedPostUi> = emptyList(),
        val npubToDisplayNameMap: Map<String, String> = emptyMap(),
        val topZap: EventZapUiModel? = null,
        val otherZaps: List<EventZapUiModel> = emptyList(),
        val comments: List<FeedPostUi> = emptyList(),
        val error: ArticleDetailsError? = null,
    )

    sealed class ArticleDetailsError {
        data object InvalidNaddr : ArticleDetailsError()
        data class MissingLightningAddress(val cause: Throwable) : ArticleDetailsError()
        data class InvalidZapRequest(val cause: Throwable) : ArticleDetailsError()
        data class FailedToPublishZapEvent(val cause: Throwable) : ArticleDetailsError()
        data class FailedToPublishRepostEvent(val cause: Throwable) : ArticleDetailsError()
        data class FailedToPublishLikeEvent(val cause: Throwable) : ArticleDetailsError()
        data class MissingRelaysConfiguration(val cause: Throwable) : ArticleDetailsError()
    }

    sealed class ArticlePartRender {
        data class MarkdownRender(val markdown: String) : ArticlePartRender()
        data class HtmlRender(val html: String) : ArticlePartRender()
        data class NoteRender(val note: FeedPostUi) : ArticlePartRender()
    }

    sealed class UiEvent {
        data object UpdateContent : UiEvent()
        data object DismissErrors : UiEvent()
    }
}