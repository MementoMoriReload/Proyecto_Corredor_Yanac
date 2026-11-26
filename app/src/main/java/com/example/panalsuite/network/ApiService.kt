package com.example.panalsuite.network

import com.example.panalsuite.model.User
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.TicketCategory
import com.example.panalsuite.model.TicketComment
import com.example.panalsuite.model.TicketPriority
import com.example.panalsuite.model.TicketStatus
import com.example.panalsuite.model.UserRole
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import kotlin.jvm.Throws
import java.util.concurrent.TimeUnit
import retrofit2.http.DELETE



data class LoginRequest(
    val email: String,
    val password: String
)

data class UserRequestDTO(
    val name: String,
    val email: String,
    val password: String,
    val role: UserRole
)

data class RoleRequest(
    val role: UserRole
)

data class TicketRequestDTO(
    val title: String,
    val description: String,
    val location: String,
    val category: TicketCategory,
    val priority: TicketPriority,
    val requesterId: Long
)

data class CommentRequestDTO(
    val author: String,
    val text: String
)

data class StatusRequest(
    val status: TicketStatus
)

data class AssignRequest(
    val assignedId: Long?
)

data class UserDTO(
    @SerializedName("userId") val id: Long,
    val name: String,
    val email: String,
    val department: String?,
    val role: UserRole
)

data class CommentDTO(
    @SerializedName("commentId") val id: Long,
    val author: String?,
    val text: String?,
    val timestamp: String?
)

data class TicketDTO(
    @SerializedName("ticketId") val id: Long,
    val title: String,
    val description: String,
    val category: TicketCategory,
    val priority: TicketPriority,
    val status: TicketStatus,
    val location: String,
    val requesterId: Long,
    val assignedId: Long?,
    val comments: List<CommentDTO>,
    val createdAt: String?,
    val updatedAt: String?
)

fun UserDTO.toUser() = User (
    id = id.toString(),
    name = name,
    email = email,
    department = department,
    role = role
)

private fun String?.toShortDate(): String
= this?.replace('T', ' ')?.take(16) ?: ""

fun TicketDTO.toTicket() = Ticket (
    id = id.toInt(),
    title = title,
    description = description?:"",
    category = category,
    priority = priority,
    status = status,
    location = location?: "",
    requesterId = requesterId.toString(),
    assignedId = assignedId?.toString(),
    comments = (comments ?: emptyList()).map {
    TicketComment(
        id = it.id.toString(),
        author = it.author ?: "",
        text = it.text ?: "",
        timestamp = it.timestamp.toShortDate()
    )
    },
    createdAt = createdAt.toShortDate(),
    updatedAt = updatedAt.toShortDate()
)

class ApiException(message: String) : Exception(message)

fun <T> Response<T>.bodyOrThrow(): T {
    if (isSuccessful) {
        return body() ?: throw ApiException("El servidor devolvio una respuesta vacia")
    }
    val errorBody = errorBody()?.string()
    val serverMessage = try {
        (Gson().fromJson(errorBody, Map::class.java)?.get("error")) as? String
    } catch (e: Exception) {
        null
    }
    throw ApiException(serverMessage ?: "Error del servidor (${code()})")
}
fun Response<Unit>.requireSuccess() {
    if (isSuccessful) return
    val errorBody = errorBody()?.string()
    val serverMessage = try {
        (Gson().fromJson(errorBody, Map::class.java)?.get("error")) as? String
    } catch (e: Exception) {
        null
    }
    throw ApiException(serverMessage ?: "Error del servidor (${code()})")
}

fun Throwable.userMessage(): String  = when (this) {
    is ApiException -> message ?: "Error del servidor"
    is HttpException -> "Error del servidor (${code()})"
    is IOException -> "Error de red"
    else -> message ?: "Error inesperado"
}


interface ApiService {
    //Login
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<UserDTO>

    //User
    @POST("api/v1/user")
    suspend fun createUser(@Body request: UserRequestDTO): Response<UserDTO>

    @GET("api/v1/user")
    suspend fun getUsers(): List<UserDTO>

    @PATCH("api/v1/user/{id}/role")
    suspend fun updateUserRole(@Path("id") id: Long, @Body request: RoleRequest): Response<UserDTO>

    //Ticket
    @POST("api/v1/ticket")
    suspend fun createTicket(@Body request: TicketRequestDTO): Response<TicketDTO>

    @GET("api/v1/ticket")
    suspend fun getTickets(): List<TicketDTO>

    @POST("api/v1/ticket/{id}/comments")
    suspend fun createComment(@Path("id") id: Long, @Body request: CommentRequestDTO): Response<TicketDTO>

    @PATCH("api/v1/ticket/{id}/status")
    suspend fun updateTicketStatus(@Path("id") id: Long, @Body request: StatusRequest): Response<TicketDTO>

    @PATCH("api/v1/ticket/{id}/assign")
    suspend fun assignTicket(@Path("id") id: Long, @Body request: AssignRequest): Response<TicketDTO>

    @DELETE("api/v1/ticket/{id}")
    suspend fun deleteTicket(@Path("id") id: Long): Response<Unit>
}

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

   private val okHttp = OkHttpClient.Builder()
       .addInterceptor(HttpLoggingInterceptor().apply { level =
           HttpLoggingInterceptor.Level.BODY })
       .connectTimeout(10, TimeUnit.SECONDS)
       .readTimeout(10, TimeUnit.SECONDS)
       .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: ApiService = retrofit.create(ApiService::class.java)
}