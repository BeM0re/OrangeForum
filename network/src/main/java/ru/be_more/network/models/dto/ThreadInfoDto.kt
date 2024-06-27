package ru.be_more.network.models.dto

data class ThreadInfoDto(
    val result: Int = 0,
    val thread: ThreadInfoInnerDto? = null,
    val error: ThreadInfoErrorDto? = null,
) {

    data class ThreadInfoInnerDto(
        val num: Int,
        val posts: Int,
        val timestamp: Long,
    )

    //todo unify with other error dto?
    data class ThreadInfoErrorDto(
        val code: Int?,
        val message: String?,
    )
}