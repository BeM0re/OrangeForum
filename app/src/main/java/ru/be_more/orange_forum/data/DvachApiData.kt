package ru.be_more.orange_forum.data

import com.google.gson.annotations.SerializedName

data class DvachBoardName(
    val default_name : String,
    val category : String,
    val id : String,
    val name : String
)

data class DvachBoard(
    val BoardName : String = "",
    val threads: List<DvachPost> = listOf()
)

data class DvachThread(
    val posts_count: Int = 0,
    val title: String = "",
    val threads: List<DvachPosts> = listOf(DvachPosts())
)

data class DvachPosts(
    val posts: List<DvachPost> = listOf()
)

data class DvachPost(
    val num: Int,
    val name: String,
    val comment: String,
    val date: String,
    val email: String,
    val files: List<DvachFile>,
    val files_count: Int,
    val op: Int,
    val posts_count: Int,
    val subject: String,
    val timestamp: Int,
    val number: Int
)

data class DvachFile(
    val displayname: String,
    val height: Int,
    val width: Int,
    val tn_height: Int,
    val tn_width: Int,
    val path: String,
    val thumbnail: String,
    val duration: String = ""
)

data class CaptchaTypes(
    val enabled: Int,
    val result : Int,
    val types: List<CaptchaType>
)

data class CaptchaType(
    val expires : Int,
    val id: String
)

data class GetCaptchaResponse(
    val id: String,
    val result: Int,
    val type: String,
    val description: String
)

data class DvachPostResponse(
    val error : String,
    val Status :String,
    val Num: Int,
    val Reason: String
)

data class GoogleCaptchaResponse(
    val success: Boolean,
    val challenge_ts: String,  // timestamp of the challenge load (ISO format yyyy-MM-dd'T'HH:mm:ssZZ)
    val apk_package_name: String, // the package name of the app where the reCAPTCHA was solved

    @SerializedName(value = "error-codes" )
    val error_codes: List<String>
)