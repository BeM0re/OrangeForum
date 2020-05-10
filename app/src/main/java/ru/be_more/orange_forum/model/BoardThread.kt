package ru.be_more.orange_forum.model

import android.os.Parcel
import android.os.Parcelable

data class BoardThread(
    var num : Int,
    var posts: List<Post> = listOf(),
    var title: String = "",
    var isHidden: Boolean = false,
    var isDownloaded: Boolean = false,
    var isFavorite: Boolean = false
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        listOf(),
        parcel.readString().orEmpty(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(num)
        parcel.writeString(title)
        parcel.writeByte(if (isHidden) 1 else 0)
        parcel.writeByte(if (isDownloaded) 1 else 0)
        parcel.writeByte(if (isFavorite) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BoardThread> {
        override fun createFromParcel(parcel: Parcel): BoardThread {
            return BoardThread(parcel)
        }

        override fun newArray(size: Int): Array<BoardThread?> {
            return arrayOfNulls(size)
        }
    }
}