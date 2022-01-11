package com.n2q.fbdownload.entities

import com.n2q.fbdownload.util.Util
import com.n2q.fbdownload.util.Util.msToTime
import com.n2q.fbdownload.util.Util.sizeToString
import java.io.Serializable
import java.util.*

class Media(
    var name: String = "",
    var url: String = "",
    var size: Float = 0.0F,
    var type: TypeMedia,
    var bitrate: Int = 0,
    var duration: Long = 0,
    var mimeType: String = String(),
    var width: Long = 0,
    var height: Long = 0,
    var date: Long = 0,
    var urlThumb: String = String()
) : Serializable, Comparable<Media> {

    fun sizeToString() = size.sizeToString()

    fun resolution() = width.toString() + "x" + height.toString()

    fun dateFile() = Util.simpleDateFormat().format(Date(date)).toString()

    fun bitrate() = (bitrate / 1024).toString() + " kbps"

    fun time() = duration.msToTime()

    override fun compareTo(other: Media) = other.date.compareTo(this.date)

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Media) return false
        return name == other.name
    }

}

enum class TypeMedia { SD_VIDEO, HD_VIDEO, AUDIO }