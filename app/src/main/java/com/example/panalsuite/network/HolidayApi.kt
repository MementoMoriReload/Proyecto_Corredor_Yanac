package com.example.panalsuite.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

data class HolidayDTO(
    val date: String,
    val localName: String,
    val name: String,
    val global: Boolean
)

interface HolidayApiService {
    @GET("api/v3/NextPublicHolidays/{countryCode}")
    suspend fun getNextHolidays(@Path("countryCode") countryCode: String): List<HolidayDTO>
}

// Retrofit APARTE del que usa el gateway: este apunta a internet, no a nuestro backend
object HolidayApiClient {
    private const val BASE_URL = "https://nagerholidays.com/"

    private val okHttp = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: HolidayApiService = retrofit.create(HolidayApiService::class.java)
}