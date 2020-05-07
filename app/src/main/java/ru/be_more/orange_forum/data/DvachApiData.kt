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
    var threads: List<DvachPost> = listOf()
)

data class DvachThread(
    var posts_count: Int = 0,
    var title: String = "",
    var threads: List<DvachPosts> = listOf(DvachPosts())
)

data class DvachPosts(
    var posts: List<DvachPost> = listOf()
)

data class DvachPost(
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
    var timestamp: Int,
    var number: Int
)

data class DvachFile(
    var displayname: String,
    var height: Int,
    var width: Int,
    var tn_height: Int,
    var tn_width: Int,
    var path: String,
    var thumbnail: String,
    var duration: String = ""
)

data class CaptchaTypes(
    var enabled: Int,
    var result : Int,
    var types: List<CaptchaType>
)

data class CaptchaType(
    var expires : Int,
    var id: String
)

data class GetCaptchaResponse(
    var id: String,
    var result: Int,
    var type: String,
    var description: String
)

data class DvachPostResponse(
    var error : String,
    var Status :String,
    var Num: Int,
    var Reason: String
)

data class GoogleCaptchaResponse(
    var success: Boolean,
    var challenge_ts: String,  // timestamp of the challenge load (ISO format yyyy-MM-dd'T'HH:mm:ssZZ)
    var apk_package_name: String, // the package name of the app where the reCAPTCHA was solved

    @SerializedName(value = "error-codes" )
    var error_codes: List<String>
)