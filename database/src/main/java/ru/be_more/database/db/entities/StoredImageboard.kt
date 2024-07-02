package ru.be_more.database.db.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoredImageboard(
    val baseUrl: String,
    val attachmentUrl: String,
    val staticDataUrl: String,
    val type: String,
) : Parcelable