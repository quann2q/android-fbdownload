package com.n2q.fbdownload.util

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object CrawlerUtil {

    fun requestHtml(strUrl: String, requestMethod: String): String {
        val response = StringBuilder()
        (URL(strUrl).openConnection() as HttpsURLConnection).apply {
            try {
                setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U;   Windows NT 5.0; en-US; rv:1.7.12) Gecko/20050915 Firefox/1.0.7")
                if (requestMethod.isNotEmpty()) {
                    this.requestMethod = requestMethod
                }
                val inputStream = BufferedReader(InputStreamReader(inputStream))
                var inputLine: String
                while (inputStream.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return response.toString()
    }

}