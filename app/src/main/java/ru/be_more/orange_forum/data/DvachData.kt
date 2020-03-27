package ru.be_more.orange_forum.data

import com.google.gson.annotations.SerializedName

data class DvachBoard(
    val bump_limit : Int,
    val category : String,
    val default_name : String,
    val enable_dices : Int,
    val enable_flags : Int,
    val enable_icons : Int,
    val enable_likes : Int,
    val enable_names : Int,
    val enable_oekaki : Int,
    val enable_posting : Int,
    val enable_sage : Int,
    val enable_shield : Int,
    val enable_subject : Int,
    val enable_thread_tags : Int,
    val enable_trips : Int,
    val icons : Int,
    val id : String,
    val name : String,
    val pages : Int,
    val sage : Int,
    val tripcodes : Int
)

data class DvachCategories(
    val Взрослым : List<DvachBoard> = listOf(),
    val Игры : List<DvachBoard> = listOf() ,
    val Политика : List<DvachBoard> = listOf() ,
    val Пользовательские : List<DvachBoard> = listOf() ,
    val Разное : List<DvachBoard> = listOf() ,
    val Творчество : List<DvachBoard> = listOf() ,
    val Тематика : List<DvachBoard> = listOf() ,
    @SerializedName(value = "Техника и софт")
    val tech : List<DvachBoard> = listOf() ,
    @SerializedName(value = "Японская культура" )
    val japan : List<DvachBoard> = listOf()

)