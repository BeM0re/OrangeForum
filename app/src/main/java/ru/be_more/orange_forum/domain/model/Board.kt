package ru.be_more.orange_forum.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

data class Board(
    val name:String,
    val id: String,
    val category: String,
    val threads: List<BoardThread> = listOf(),
    val isFavorite: Boolean = false
): Parcelable, ExpandableGroup<BoardThread>(name, threads) {
    constructor(parcel: Parcel) : this(
        name = parcel.readString() ?: "",
        id = parcel.readString() ?: "",
        category = parcel.readString() ?: "",
        threads = listOf(),
        isFavorite = parcel.readByte() != 0.toByte()
    )//todo зочем

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(id)
        parcel.writeByte(if (isFavorite) 1 else 0)
    } //todo зочем

    override fun describeContents(): Int {
        return 0
    }//todo зочем

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }
}