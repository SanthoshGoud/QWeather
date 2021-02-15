package com.android.test.network

import android.util.Log
import com.android.test.BuildConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor


/**
 * Factory class for convenient creation of the Api Service interface
 */
object MQHTTPClient : IHTTPClient {

    private val TAG: String? = MQHTTPClient::class.java.canonicalName
    private var mRetroClient: Retrofit? = null
    private var mBaseURL = URLBuilder.baseUrl +"/"


    override fun getAppJsonRequestBody(payload: String): RequestBody {
        return payload.toRequestBody("application/json".toMediaTypeOrNull())
    }

    override fun getTextRequestBody(payload: String): RequestBody {
        return payload.toRequestBody("application/json".toMediaTypeOrNull())
    }

    override fun getHttpClient(): Retrofit? {
        if (mRetroClient == null) {
            createHTTPClient()
        }
        return mRetroClient
    }

    override fun bindService(service: Any): Any {
        if (mRetroClient == null) {
            createHTTPClient()
        }
        return mRetroClient!!.create(service::class.java)
    }

    fun initialize() {
            if (mRetroClient == null) createHTTPClient()
           else
            Log.i(TAG, "initialize() :: Base URL cannot be null")
    }



    private fun createHTTPClient(): Retrofit? {

        val interceptor = HttpLoggingInterceptor()
        if(BuildConfig.DEBUG)
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        else
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        mRetroClient = Retrofit.Builder()
                .baseUrl(mBaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        return mRetroClient
    }
}