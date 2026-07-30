package com.jackson.blebridge.data.sample.model

import com.google.gson.annotations.SerializedName

internal data class CatFactPageResponse(
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("data")
    val data: List<CatFactResponse>,
    @SerializedName("last_page")
    val lastPage: Int,
)

internal data class CatFactResponse(
    @SerializedName("fact")
    val fact: String,
    @SerializedName("length")
    val length: Int,
)
