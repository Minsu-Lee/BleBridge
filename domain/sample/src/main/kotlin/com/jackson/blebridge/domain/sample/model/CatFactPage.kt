package com.jackson.blebridge.domain.sample.model

data class CatFactPage(
    val items: List<CatFact>,
    val currentPage: Int,
    val lastPage: Int,
)
