package com.example.ime

import WhisperApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WhisperClient {
    private val okHttpClient = OkHttpClient.Builder().build()

    val api: WhisperApi = Retrofit.Builder()
        .baseUrl("https://api.groq.com")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WhisperApi::class.java)
}
