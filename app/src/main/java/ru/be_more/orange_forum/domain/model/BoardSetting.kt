package ru.be_more.orange_forum.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BoardSetting(
    val isNameEnabled: Boolean,
    val isTripEnabled: Boolean,
    val isSubjectEnabled: Boolean,
    val isSageEnabled: Boolean,
    val isIconEnabled: Boolean,
    val isFlagEnabled: Boolean,
    val isPostingEnabled: Boolean,
    val isLikeEnabled: Boolean,
    val isTagEnabled: Boolean,
    val fileTypes: List<String>?,
    val maxCommentSize: Int?,
    val maxFileSize: Int?,
    val tags: List<String>?,
    val icons: List<Icon>?,
) : Parcelable