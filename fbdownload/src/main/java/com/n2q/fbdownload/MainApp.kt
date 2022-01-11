package com.n2q.fbdownload

import android.app.Application
import com.downloader.PRDownloader
import com.orhanobut.hawk.Hawk

class MainApp :  Application() {

    override fun onCreate() {
        super.onCreate()

        /**
         * Hawk init
         */
        Hawk.init(applicationContext).build()


        /**
         * PRDownloader init
         */
        PRDownloader.initialize(applicationContext)

    }

}