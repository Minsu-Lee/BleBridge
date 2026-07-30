package com.jackson.blebridge.domain.sample.repository

import com.jackson.blebridge.core.common.NeveraResult
import com.jackson.blebridge.domain.sample.model.CatFact
import com.jackson.blebridge.domain.sample.model.CatFactPage
import com.jackson.blebridge.domain.sample.model.RandomCatFactFailure

interface CatFactRepository {
    suspend fun getRandomFact(): NeveraResult<CatFact, RandomCatFactFailure>
    suspend fun getFacts(page: Int, limit: Int): CatFactPage
}
