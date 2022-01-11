package com.n2q.fbdownload.util

import android.content.Context
import java.io.File

object FileUtil {

    const val EXTERNAL_NAME = "FDownload"

    fun externalFileChild(context: Context, nameChild: String) = File(externalPathDir(context), nameChild)

    fun externalFileDir(context: Context) = File(context.getExternalFilesDir(null), EXTERNAL_NAME)

    fun externalPathDir(context: Context) = externalFileDir(context).toString()

    fun isExistExternalDir(context: Context): Boolean {
        val dir = externalFileDir(context)
        if (!dir.exists()) {
            return dir.mkdirs()
        }
        return true
    }


}