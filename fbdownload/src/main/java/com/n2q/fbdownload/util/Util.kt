package com.n2q.fbdownload.util

import android.annotation.SuppressLint
import com.n2q.fbdownload.constant.Constant
import com.n2q.fbdownload.entities.Media
import com.orhanobut.hawk.Hawk
import java.io.File
import java.text.SimpleDateFormat

object Util {

    fun Float.sizeToString() =
        if (this / 1024 > 1024) {
            "%.2f".format(this / 1024 / 1024) + " MB"
        } else {
            "%.2f".format(this / 1000) + " KB"
        }

    @SuppressLint("SimpleDateFormat")
    fun simpleDateFormat() = SimpleDateFormat("yyyy/MM/dd - HH:mm")

    fun Int.secToTime(): String {
        val h = this / 3600
        val m = (this % 3600) / 60
        val s = this % 60

        return if (h > 0) "%d:%02d:%02d".format(h, m, s)
        else "%02d:%02d".format(m, s)
    }

    fun Long.msToTime() = (this / 1000).toInt().secToTime()

    fun isUrlFb(url: String) = url.contains(Constant.FB_LINK_1) ||
            url.contains(Constant.FB_LINK_2) ||
            url.contains(Constant.FB_LINK_3) ||
            url.contains(Constant.FB_LINK_4)

    fun deleteMedia(media: Media) = deleteMedia(media.url, media.urlThumb, media.name)

    fun deleteMedia(url: String, urlThumb: String, name: String): Boolean {
        val file = File(url)
        val thumb = File(urlThumb)

        if (file.delete()) {
            thumb.delete()
            Hawk.delete(Constant.PREFIX_MEDIA + name)
            return true
        }
        return false
    }

}