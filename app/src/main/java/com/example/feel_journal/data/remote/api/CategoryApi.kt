package com.example.feel_journal.data.remote.api

import com.example.feel_journal.data.remote.dto.CategoryDto
import com.example.feel_journal.data.remote.dto.CreateCategoryRequest
import com.example.feel_journal.data.remote.dto.UpdateCategoryRequest
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

interface CategoryApi {

    @GET("api/categories")
    fun getCategories(): Observable<List<CategoryDto>>

    @GET("api/categories/{id}")
    fun getCategoryById(@Path("id") id: Int): Observable<CategoryDto>

    @POST("api/categories")
    fun createCategory(@Body request: CreateCategoryRequest): Observable<CategoryDto>

    @PUT("api/categories/{id}")
    fun updateCategory(
        @Path("id") id: Int,
        @Body request: UpdateCategoryRequest
    ): Observable<CategoryDto>

    @DELETE("api/categories/{id}")
    fun deleteCategory(@Path("id") id: Int): Observable<Unit>
}
