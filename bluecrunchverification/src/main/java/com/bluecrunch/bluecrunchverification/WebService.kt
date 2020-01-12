package com.bluecrunch.verification

import com.bluecrunch.bluecrunchverification.SendSMSRequest
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface WebService {

    companion object {
        private val client: OkHttpClient
            get() = OkHttpClient()
                .newBuilder()
                .readTimeout(1,TimeUnit.MINUTES)
                .writeTimeout(1,TimeUnit.MINUTES)
                .connectTimeout(1,TimeUnit.MINUTES)
                .build()

        val retrofit: Retrofit
            get() = Retrofit.Builder()
                .baseUrl("http://ws.audioscrobbler.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @GET
    fun sendSMSGET(@Url url: String): Call<ResponseBody>

    @POST
    fun sendSMSPOST(@Url url: String, @Body request: SendSMSRequest): Call<ResponseBody>


    @GET
    fun verifySMSGET(@Url url: String): Call<ResponseBody>

    @POST
    fun verifySMSPOST(@Url url: String, @Body request: VerifySMSRequest): Call<ResponseBody>

}