package com.example.zhilehuodemo.Model

import android.os.Parcel
import android.os.Parcelable

data class SentenceByXF(
    val word:String,
    val wb:Int,
    val we:Int
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun describeContents(): Int =0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(word)
        dest.writeInt(wb)
        dest.writeInt(we)
    }

    companion object CREATOR : Parcelable.Creator<SentenceByXF> {
        override fun createFromParcel(parcel: Parcel): SentenceByXF {
            return SentenceByXF(parcel)
        }

        override fun newArray(size: Int): Array<SentenceByXF?> {
            return arrayOfNulls(size)
        }
    }
}
