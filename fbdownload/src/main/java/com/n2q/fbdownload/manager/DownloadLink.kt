package com.n2q.fbdownload.manager

import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.n2q.fbdownload.entities.Media

class DownloadLink(private val externalDir: String, private val filename: String, private val media: Media, private val callback: Callback) {

    fun start(): DownloadLink {
        callback.onPre(filename, media.url)

        PRDownloader.download(media.url, externalDir, filename)
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