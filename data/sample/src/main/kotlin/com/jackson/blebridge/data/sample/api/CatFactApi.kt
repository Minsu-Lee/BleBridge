package com.jackson.blebridge.data.sample.api

import com.jackson.blebridge.data.sample.model.CatFactPageResponse
import com.jackson.blebridge.data.sample.model.CatFactResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface CatFactApi {
    @GET("facts")
    suspend fun getFacts(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): CatFactPageResponse

    @GET("fact")
    suspend fun getRandomFact(): Response<CatFactResponse>
}
