package net.primal.android.feed.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.json.JsonArray

@Entity
data class RepostData(
    @PrimaryKey
    val repostId: String,
    val authorId: String,
    val createdAt: Long,
    val tags: List<JsonArray>,
    val postId: String,
    val postAuthorId: String,
    val sig: String,
)