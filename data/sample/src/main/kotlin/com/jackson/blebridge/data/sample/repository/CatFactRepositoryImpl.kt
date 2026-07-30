package com.jackson.blebridge.data.sample.repository

import com.jackson.blebridge.core.common.NeveraResult
import com.jackson.blebridge.core.common.map
import com.jackson.blebridge.core.network.ApiCallExecutor
import com.jackson.blebridge.data.sample.api.CatFactApi
import com.jackson.blebridge.data.sample.mapper.toDomain
import com.jackson.blebridge.data.sample.mapper.toRandomCatFactFailure
import com.jackson.blebridge.domain.sample.model.CatFact
import com.jackson.blebridge.domain.sample.model.CatFactPage
import com.jackson.blebridge.domain.sample.model.RandomCatFactFailure
import com.jackson.blebridge.domain.sample.repository.CatFactRepository
import javax.inject.Inject

internal class CatFactRepositoryImpl @Inject constructor(
    private val api: CatFactApi,
    private val apiCall: ApiCallExecutor,
) : CatFactRepository {
    override suspend fun getRandomFact(): NeveraResult<CatFact, RandomCatFactFailure> =
        apiCall {
            api.getRandomFact()
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toRandomCatFactFailure() },
        )

    override suspend fun getFacts(page: Int, limit: Int): CatFactPage =
        api.getFacts(page = page, limit = limit).toDomain()
}
