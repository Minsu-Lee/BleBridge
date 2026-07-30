package com.jackson.blebridge.domain.sample.usecase

import com.jackson.blebridge.domain.sample.model.CatFactPage
import com.jackson.blebridge.domain.sample.repository.CatFactRepository
import javax.inject.Inject

class GetCatFactsPageUseCase @Inject constructor(
    private val repository: CatFactRepository,
) {
    suspend operator fun invoke(page: Int, limit: Int): CatFactPage =
        repository.getFacts(page = page, limit = limit)
}
