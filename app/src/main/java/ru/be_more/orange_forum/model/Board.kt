package ru.be_more.orange_forum.model

import android.os.Parcel
import android.os.Parcelable
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

data class Board (
    var name:String,
    var id: String,
    var threads: List<BoardThread> = listOf()
):Parcelable, ExpandableGroup<BoardThread>(name, threads) {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }

}