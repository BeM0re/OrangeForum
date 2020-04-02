package ru.be_more.orange_forum.model

import android.os.Parcel
import android.os.Parcelable

data class BoardShort (
    var name:String?,
    var id: String?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BoardShort> {
        override fun createFromParcel(parcel: Parcel): BoardShort {
            return BoardShort(parcel)
        }

        override fun newArray(size: Int): Array<BoardShort?> {
            return arrayOfNulls(size)
        }
    }

}