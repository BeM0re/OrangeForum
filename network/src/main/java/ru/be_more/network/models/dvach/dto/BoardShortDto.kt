package ru.be_more.network.models.dvach.dto

import com.google.gson.annotations.SerializedName

data class BoardShortDto(
    @SerializedName("default_name") val defaultName: String,
    val category: String,
    val id: String,
    val name: String
    //todo icons
)

