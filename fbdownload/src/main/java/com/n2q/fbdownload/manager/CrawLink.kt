package com.n2q.fbdownload.manager

import com.n2q.fbdownload.constant.Constant
import com.n2q.fbdownload.entities.Media
import com.n2q.fbdownload.entities.TypeMedia
import com.n2q.fbdownload.entities.VideoInfo
import com.n2q.fbdownload.util.CrawlerUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.URL
import java.net.URLEncoder
import java.util.regex.Matcher
import java.util.regex.Pattern


class CrawLink(private val videoInfo: VideoInfo, private val isEncodeHD: Boolean, private val callback: Callback) {

    companion object {
        private const val EMBEDDED_LINK = "https://www.facebook.com/plugins/video.php?href="
        private val PATTERN_HD = Pattern.compile("\"hd_src\":\"(.*?)\",\"sd_src\"")
        private val PATTERN_AUDIO = Pattern.compile("\"audio\":\\[\\{\"url\":\"(.*?)\",\"start\"")
    }

    /**
     * Craw link with RxAndroid
     */
    fun crawLinkRx() {
        val arrMedia = ArrayList<Media>()
        Observable.just(videoInfo)
            .flatMap { Observable.just(it).map(this::encodeUrl).subscribeOn(Schedulers.io()) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ArrayList<Media>> {
                override fun onSubscribe(d: Disposable) {
                    callback.onPre()
                }

                override fun onNext(mediaCrawed: ArrayList<Media>) {
                    arrMedia.addAll(mediaCrawed)
                }

                override fun onError(e: Throwable) {
                    // TODO: 25/11/2021
                }

                override fun onComplete() {
                    callback.onComplete(arrMedia)
                }
            })
    }

    private fun encodeUrl(videoInfo: VideoInfo): ArrayList<Media> {
        val arrMedia = ArrayList<Media>()

        // Add SD Video
        arrMedia.add(infoMedia(videoInfo.vidId, videoInfo.vidData, TypeMedia.SD_VIDEO))

        if (!isEncodeHD) {
            return arrMedia
        }

        // Query HD Video & Audio
        val query = URLEncoder.encode(Constant.FB_PREFIX + videoInfo.vidId, "utf-8")
        val strUrl = "$EMBEDDED_LINK$query&show_text=true&width=1024&locate=en_US"
        val html = CrawlerUtil.requestHtml(strUrl, "GET")

        val hdMatcher = PATTERN_HD.matcher(html)
        val audioMatcher = PATTERN_AUDIO.matcher(html)

        if (hdMatcher.find()) {
            val mediaHD = infoMedia(videoInfo.vidId, urlMedia(hdMatcher), TypeMedia.HD_VIDEO)
            if (mediaHD.size != 22.0F) {
                arrMedia.add(infoMedia(videoInfo.vidId, urlMedia(hdMatcher), TypeMedia.HD_VIDEO))
            }
        }

        if (audioMatcher.find()) {
            arrMedia.add(infoMedia(videoInfo.vidId, urlMedia(audioMatcher), TypeMedia.AUDIO))
        }

        return arrMedia
    }

    private fun urlMedia(matcher: Matcher) = matcher.group(1)?.replace("\\/", "/") ?: ""

    private fun infoMedia(name: String, urlMedia: String, typeMedia: TypeMedia) =
        URL(urlMedia).openConnection().run {
            connect()
            Media(name = name, url = urlMedia, size = contentLength.toFloat(), type = typeMedia)
        }

    interface Callback {
        fun onPre()
        fun onComplete(arrMedia: ArrayList<Media>)
    }
}
