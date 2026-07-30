package com.jackson.blebridge.data.sample.mapper

import com.jackson.blebridge.data.sample.model.CatFactPageResponse
import com.jackson.blebridge.data.sample.model.CatFactResponse
import com.jackson.blebridge.domain.sample.model.CatFact
import com.jackson.blebridge.domain.sample.model.CatFactPage

internal fun CatFactPageResponse.toDomain(): CatFactPage =
    CatFactPage(
        items = data.map(CatFactResponse::toDomain),
        currentPage = currentPage,
        lastPage = lastPage,
    )

internal fun CatFactResponse.toDomain(): CatFact =
    CatFact(
        fact = fact,
        length = length,
    )
