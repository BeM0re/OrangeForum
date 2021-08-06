package ru.be_more.orange_forum.domain.model

import android.os.Parcel
import android.os.Parcelable


data class BoardThread(
    val num : Int,
    val posts: List<Post> = listOf(),
    val title: String = "",
    val lastPostNumber: Int = 0,
    val newMessageAmount: Int = 0,
    val isHidden: Boolean = false,
    val isDownloaded: Boolean = false,
    val isFavorite: Boolean = false,
    val isQueued: Boolean = false
): Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        listOf(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )

    fun isEmpty(): Boolean =
        this.num == -1

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(num)
        parcel.writeString(title)
        parcel.writeInt(lastPostNumber)
        parcel.writeInt(newMessageAmount)
        parcel.writeByte(if (isHidden) 1 else 0)
        parcel.writeByte(if (isDownloaded) 1 else 0)
        parcel.writeByte(if (isFavorite) 1 else 0)
        parcel.writeByte(if (isQueued) 1 else 0)
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

        fun empty(): BoardThread =
            BoardThread(-1)
    }
}