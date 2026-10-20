package com.example.panalsuite.network

import com.example.panalsuite.model.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


data class LoginRequest(
    val email: String,
    val password: String
)


interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<User>

}

object ApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: ApiService =
        retrofit.create(ApiService::class.java)
}