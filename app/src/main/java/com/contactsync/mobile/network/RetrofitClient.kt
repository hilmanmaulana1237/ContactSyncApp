package com.contactsync.mobile.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    // Base URL untuk emulator Android (10.0.2.2 = localhost host machine)
    // Ganti dengan URL Vercel production untuk rilis
    private const val BASE_URL = "https://backend-sync-contact.vercel.app/"
    
    // API Key - MUST match backend
    private const val API_KEY = "CSApp2024SecretKey!@#\$"
    
    /**
     * Interceptor to add API key header to all requests
     */
    private class ApiKeyInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .addHeader("X-Api-Key", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build()
            return chain.proceed(newRequest)
        }
    }
    
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
