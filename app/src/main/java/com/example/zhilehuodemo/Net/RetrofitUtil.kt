package com.example.zhilehuodemo.Net

import kotlinx.coroutines.CoroutineScope
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitUtil {
    private const val articleTypeIp="https://test.shiqu.zhilehuo.com/"
    private const val readStoryIp="https://shiqu.zhilehuo.com/"

    private val client by lazy {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {
            }

            override fun checkServerTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }


    private val instanceArticle =
        Retrofit.Builder()
            .baseUrl(articleTypeIp)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    private val instanceReadStory=
        Retrofit.Builder()
            .baseUrl(readStoryIp)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    val articleApi= instanceArticle.create(ArticleApi::class.java)

    val readStoryApi= instanceReadStory.create(ReadStoryApi::class.java)
}