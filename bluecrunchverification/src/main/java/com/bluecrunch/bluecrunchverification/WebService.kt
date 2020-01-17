package com.bluecrunch.bluecrunchverification

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface WebService {

    companion object {
        private val client: OkHttpClient
            get() = OkHttpClient()
                .newBuilder()
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(HttpLoggingInterceptor { message ->
                    Log.e("API = > ", message)
                }
                    .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

        val retrofit: Retrofit
            get() = Retrofit.Builder()
                .baseUrl("http://www.google.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @GET
    fun sendSMSGET(@Url url: String): Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST
    fun sendSMSPOST(@Url url: String, @Body request: SendSMSRequest): Call<ResponseBody>


    @GET
    fun verifySMSGET(@Url url: String): Call<ResponseBody>

    @POST
    fun verifySMSPOST(@Url url: String, @Body request: VerifySMSRequest): Call<ResponseBody>

}