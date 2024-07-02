package ru.be_more.model.model

import kotlinx.serialization.Serializable

@Serializable
data class Imageboard(
    val baseUrl: String,
    val attachmentUrl: String, //pics/vids
    val staticDataUrl: String, //icons
    val type: ImageboardType,
)