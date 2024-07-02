package ru.be_more.network.models.dvach.dto

import com.google.gson.annotations.SerializedName

data class ResponseDto(
    val error : String,
    @SerializedName("Status") val status :String,
    @SerializedName("Num") val num: Int,
    @SerializedName("Reason") val reason: String
)