package ru.be_more.orange_forum.data.remote.models

import ru.be_more.orange_forum.domain.model.PostResponse

data class DvachPostResponse(
    val error : String,
    val Status :String,
    val Num: Int,
    val Reason: String
){
    fun toModel() =
        PostResponse(
            error = error,
            Status = Status,
            Num = Num,
            Reason = Reason
        )
}