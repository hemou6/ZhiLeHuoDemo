package com.example.zhilehuodemo.Model

import android.os.Parcel
import android.os.Parcelable

data class StoryContentDetail(
    val pageNum:Int=0,
    val imgUrl:String="",
    val audioUrl:String="",
    val sentence:String="",
    val sentenceByXFList: List<SentenceByXF> = emptyList()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.createTypedArrayList(SentenceByXF.CREATOR)?: emptyList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(pageNum)
        parcel.writeString(imgUrl)
        parcel.writeString(audioUrl)
        parcel.writeString(sentence)
        parcel.writeTypedList(sentenceByXFList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StoryContentDetail> {
        override fun createFromParcel(parcel: Parcel): StoryContentDetail {
            return StoryContentDetail(parcel)
        }

        override fun newArray(size: Int): Array<StoryContentDetail?> {
            return arrayOfNulls(size)
        }
    }
}
