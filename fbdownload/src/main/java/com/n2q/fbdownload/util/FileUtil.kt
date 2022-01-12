package com.n2q.fbdownload.util

import android.content.Context
import com.n2q.fbdownload.constant.Constant
import com.n2q.fbdownload.entities.TypeMedia
import java.io.File

object FileUtil {

    fun externalFileChild(context: Context, externalName: String, nameChild: String) = File(externalFileDir(context, externalName).toString(), nameChild)

    fun externalFileDir(context: Context, externalName: String) = File(context.getExternalFilesDir(null), externalName)

    fun isExistExternalDir(context: Context, externalName: String): Boolean {
        val dir = externalFileDir(context, externalName)
        if (!dir.exists()) {
            return dir.mkdirs()
        }
        return true
    }

    private fun generateFilename(context: Context, externalName: String, filename: String, typeMedia: TypeMedia): String {
        var name = String()
        var ext = String()

        if (typeMedia == TypeMedia.SD_VIDEO) {
            name = "${Constant.PREFIX_SD}$filename"
            ext = Constant.EXT_VIDEO
        }

        if (typeMedia == TypeMedia.HD_VIDEO) {
            name = "${Constant.PREFIX_HD}$filename"
            ext = Constant.EXT_VIDEO
        }

        if (typeMedia == TypeMedia.AUDIO) {
            name = "${Constant.PREFIX_AUDIO}$filename"
            ext = Constant.EXT_AUDIO
        }

        var count = 1
        var file = externalFileChild(context, externalName, name + ext)
        while (file.exists()) {
            file = externalFileChild(context, externalName, "$name-$count$ext")
            count++
        }
        return file.name
    }


}