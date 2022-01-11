package com.n2q.fbdownload.manager

import android.content.Context
import com.coder.link.preview.library.LinkPreviewCallback
import com.coder.link.preview.library.SourceContent
import com.coder.link.preview.library.TextCrawler
import com.n2q.fbdownload.util.NetworkUtil

object PreviewLink {

    enum class State {
        NO_INTERNET, NOT_VIDEO
    }

    fun crawler(context: Context, url: String, callback: Callback){

        if (!NetworkUtil.isNetworkConnected(context)) {
            callback.onError(State.NO_INTERNET)

            return
        }

        TextCrawler().makePreview(object : LinkPreviewCallback {

            override fun onPre() {
                callback.onPre(url)
            }

            override fun onPos(sourceContent: SourceContent?, isNull: Boolean) {


                if (sourceContent == null) {
                    callback.onError(State.NOT_VIDEO)
                    return
                }

                if (isNull) {
                    callback.onError(State.NOT_VIDEO)
                    return
                }

                callback.onFoundLink(sourceContent.title, sourceContent.url, sourceContent.images[0])

            }

        }, url)

    }

    interface Callback {
        fun onPre(url: String)
        fun onFoundLink(title: String, url: String, urlImage: String?)
        fun onError(state: State)
    }

}