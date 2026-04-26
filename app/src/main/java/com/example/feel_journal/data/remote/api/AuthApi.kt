package com.example.feel_journal.data.remote.api

import com.example.feel_journal.data.remote.dto.*
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

interface AuthApi {

    @POST("api/auth/register")
    fun register(@Body request: RegisterRequest): Observable<AuthResponse>

    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Observable<AuthResponse>

    @POST("api/auth/recovery")
    fun initiateRecovery(@Body request: RecoveryRequest): Observable<Unit>

    @POST("api/auth/reset-password")
    fun resetPassword(@Body request: ResetPasswordRequest): Observable<Unit>

    @GET("api/auth/me")
    fun getMe(): Observable<UserDto>
}
