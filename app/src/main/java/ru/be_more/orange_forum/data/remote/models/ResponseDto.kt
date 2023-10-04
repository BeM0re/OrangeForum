package ru.be_more.orange_forum.data.remote.models

import com.google.gson.annotations.SerializedName
import ru.be_more.orange_forum.domain.model.PostResponse

data class ResponseDto(
    val error : String,
    @SerializedName("Status") val status :String,
    @SerializedName("Num") val num: Int,
    @SerializedName("Reason") val reason: String
){
    fun toModel() =
        PostResponse(
            error = error,
            status = status,
            num = num,
            reason = reason
        )
}