package com.n2q.fbdownload.manager

import android.content.Context
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.n2q.fbdownload.entities.TypeMedia
import com.n2q.fbdownload.util.FileUtil

object DownloadLink {

    fun start(context: Context, externalName: String, mediaUrl: String, mediaName: String, mediaType: TypeMedia, callback: Callback): DownloadLink {

        if (!FileUtil.isExistExternalDir(context, externalName)) {
            return this
        }

        val externalDir = FileUtil.externalFileDir(context, externalName).toString()
        val filename = FileUtil.generateFilename(context, externalName, mediaName, mediaType)

        callback.onPre(filename, mediaUrl)

        PRDownloader.download(mediaUrl, externalDir, filename)
            .build()
            .setOnProgressListener { progress ->
                val percent = (progress.currentBytes * 100L) / progress.totalBytes
                callback.onDownload(percent)
            }
            .start(object : OnDownloadListener {

                override fun onDownloadComplete() {
                    callback.onComplete(filename)
                }

                override fun onError(error: Error?) {
                    callback.onError()
                }
            })

        return this
    }

    interface Callback {
        fun onPre(filename: String, url: String)
        fun onDownload(percent: Long)
        fun onComplete(filename: String)
        fun onError()
    }

}