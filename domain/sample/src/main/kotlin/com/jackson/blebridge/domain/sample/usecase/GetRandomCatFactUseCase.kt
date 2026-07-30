package com.jackson.blebridge.domain.sample.usecase

import com.jackson.blebridge.core.common.NeveraResult
import com.jackson.blebridge.domain.sample.model.CatFact
import com.jackson.blebridge.domain.sample.model.RandomCatFactFailure
import com.jackson.blebridge.domain.sample.repository.CatFactRepository
import javax.inject.Inject

class GetRandomCatFactUseCase @Inject constructor(
    private val repository: CatFactRepository,
) {
    suspend operator fun invoke(): NeveraResult<CatFact, RandomCatFactFailure> =
        repository.getRandomFact()
}
