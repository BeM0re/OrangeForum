package ru.be_more.database.db.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoredIcon(
    val id: Int,
    val name: String,
    val url: String,
) : Parcelable