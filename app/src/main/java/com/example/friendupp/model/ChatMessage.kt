package com.example.friendupp.model

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope

data class ChatMessage (
    var text: String,
    var sender_picture_url:String,
    var sent_time:String,
    val sender_id:String,
    var message_type:String="text",
    var id :String,
    val collectionId:String,
    var replyTo:String?
): Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
    ) {
    }

    constructor(): this("", "","", "","","","","")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
        parcel.writeString(sender_picture_url)
        parcel.writeString(sent_time)
        parcel.writeString(sender_id)
        parcel.writeString(message_type)
        parcel.writeString(id)
        parcel.writeString(collectionId)
        parcel.writeString(replyTo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatMessage> {
        override fun createFromParcel(parcel: Parcel): ChatMessage {
            return ChatMessage(parcel)
        }

        override fun newArray(size: Int): Array<ChatMessage?> {
            return arrayOfNulls(size)
        }
    }
}
class ChatMessageListSaver : Saver<MutableList<ChatMessage>, List<ChatMessage>> {
    override fun restore(value: List<ChatMessage>): MutableList<ChatMessage> {
        return value.toMutableList()
    }

    override fun SaverScope.save(value: MutableList<ChatMessage>): List<ChatMessage> {
        return value.toList()
    }
}

