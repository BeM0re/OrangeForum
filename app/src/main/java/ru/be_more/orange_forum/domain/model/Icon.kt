package ru.be_more.orange_forum.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Icon(
    val id: Int,
    val name: String,
    val url: String,
) : Parcelable