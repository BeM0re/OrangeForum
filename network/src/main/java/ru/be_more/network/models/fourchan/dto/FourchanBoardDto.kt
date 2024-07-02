package ru.be_more.network.models.fourchan.dto

data class FourchanBoardPageDto(
    val page: String,
    val threads: List<FourchanPostDto>
)