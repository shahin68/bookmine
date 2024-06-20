package com.shahin.core.network.books.services

import com.shahin.core.network.books.model.BookItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface BooksMockyIoApi {

    @GET("v3/{id}")
    suspend fun getBooks(
        @Path("id") id: String
    ): Response<List<BookItem?>>

}