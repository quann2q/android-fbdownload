package com.n2q.fbdownload.manager

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import com.n2q.fbdownload.constant.Constant
import com.n2q.fbdownload.entities.Media
import com.n2q.fbdownload.entities.TypeMedia
import com.n2q.fbdownload.util.FileUtil
import com.orhanobut.hawk.Hawk
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList


class CrawMeta(private val context: Context, private val externalName: String, private val callback: Callback) {

    fun start() {
        callback.onPre()
        val directory = FileUtil.externalFileDir(context, externalName)
        if (!directory.exists()) {
            callback.onComplete(ArrayList())
            return
        }

        val files = directory.listFiles()
        if (files == null || files.isEmpty()) {
            callback.onComplete(ArrayList())
            return
        }

        Observable.just(directory)
            .flatMap { Observable.fromArray(*files) }
            .filter { obj: File -> obj.isFile }
            .flatMap { file -> Observable.just(file).map(this::crawMedia).subscribeOn(Schedulers.io()) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {

                override fun onSubscribe(d: Disposable) {}
                override fun onNext(stateFile: String) {}
                override fun onError(e: Throwable) {}

                override fun onComplete() {
                    val arrMedia = ArrayList<Media>()

                    files.forEach { file ->
                        Hawk.get<Media>(Constant.PREFIX_MEDIA + file.name)?.let {
                            arrMedia.add(it)
                        }
                    }

                    arrMedia.sort()
                    callback.onComplete(arrMedia)
                }

            })
    }

    private fun crawMedia(file: File): String {
        // Delete .temp file after 12 hours of failed download
        if (file.name.endsWith(".temp")) {
            if (Calendar.getInstance().timeInMillis - file.lastModified() > 43_200_000L) {
                file.delete()
            }
            return "FILE_TEMP"
        }

        var media = Hawk.get<Media>(Constant.PREFIX_MEDIA + file.name, null)
        if (media == null) {

            val typeMedia = when (true) {
                file.name.contains(Constant.PREFIX_SD) -> TypeMedia.SD_VIDEO
                file.name.contains(Constant.PREFIX_HD) -> TypeMedia.HD_VIDEO
                file.name.contains(Constant.PREFIX_AUDIO) -> TypeMedia.AUDIO
                else -> TypeMedia.SD_VIDEO
            }

            media = crawMeta(file, typeMedia)
            Hawk.put(Constant.PREFIX_MEDIA + media.name, media)
        }

        return "FILE_MEDIA"
    }


    /**
     * Get detail media from external storage
     */
    private fun crawMeta(file: File, typeMedia: TypeMedia): Media {
        val url = FileUtil.externalFileChild(context, externalName, file.name).toString()

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(url)
        val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toInt() ?: 0
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        val mimeType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) ?: String()
        val size = file.length()
        val date = file.lastModified()

        var width = 0L
        var height = 0L
        var urlThumb = ""

        if (typeMedia != TypeMedia.AUDIO) {
            width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toLong() ?: 0L
            height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toLong() ?: 0L
            urlThumb = saveThumbInternalDir(context, retriever, file.name)
        }

        return Media(file.name, url, size.toFloat(), typeMedia, bitrate, duration, mimeType, width, height, date, urlThumb)
    }

    /**
     * Save cache thumb to internal storage
     * Return directory internal storage
     */
    private fun saveThumbInternalDir(context: Context, retriever: MediaMetadataRetriever, name: String): String {
        val icon = retriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
        val cw = ContextWrapper(context)
        val dirThumb = cw.getDir(externalName + "_thumbnail", Context.MODE_PRIVATE)
        if (!dirThumb.exists()) {
            dirThumb.mkdirs()
        }
        val fileName = name.substring(0, name.lastIndexOf(".")) + Constant.EXT_IMAGE
        val fileThumb = File(dirThumb, fileName)
        val fos = FileOutputStream(fileThumb)
        icon?.compress(Bitmap.CompressFormat.JPEG, 85, fos)
        fos.close()

        return dirThumb.path + File.separator + fileName
    }

    interface Callback {
        fun onPre()
        fun onComplete(arrMedia: ArrayList<Media>)
    }

}