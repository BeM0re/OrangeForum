package ru.be_more.orange_forum.data

import com.google.gson.annotations.SerializedName

data class DvachCategories(
    @SerializedName(value = "Взрослым")
    val adult : List<DvachBoardName> = listOf(),
    @SerializedName(value = "Игры")
    val games : List<DvachBoardName> = listOf(),
    @SerializedName(value = "Политика")
    val politics : List<DvachBoardName> = listOf(),
    @SerializedName(value = "Пользовательские")
    val custom : List<DvachBoardName> = listOf(),
    @SerializedName(value = "Разное")
    val other : List<DvachBoardName> = listOf(),
    @SerializedName(value = "Творчество")
    val art : List<DvachBoardName> = listOf(),
    @SerializedName(value = "Тематика")
    val thematics : List<DvachBoardName> = listOf(),
    @SerializedName(value = "Техника и софт")
    val tech : List<DvachBoardName> = listOf(),
    @SerializedName(value = "Японская культура" )
    val japan : List<DvachBoardName> = listOf()
)

data class DvachBoardName(
    val default_name : String,
    val category : String,
    val id : String,
    val name : String
)

data class DvachBoard(
    var BoardName : String = "",
    var threads: List<DvachThread> = listOf()
)

data class DvachThread(
    var num: Int,
    var name: String,
    var comment: String,
    var date: String,
    var email: String,
    var files: List<DvachFile>,
    var files_count: Int,
    var op: Int,
    var posts_count: Int,
    var subject: String,
    var timestamp: Int
)

data class DvachFile(
    var displayname: String,
    var height: Int,
    var width: Int,
    var tn_height: Int,
    var tn_width: Int,
    var path: String,
    var thumbnail: String
)