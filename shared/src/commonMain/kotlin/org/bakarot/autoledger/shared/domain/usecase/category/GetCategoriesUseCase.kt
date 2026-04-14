package org.bakarot.autoledger.shared.domain.usecase.category

import kotlinx.coroutines.flow.Flow
import org.bakarot.autoledger.shared.domain.model.Category
import org.bakarot.autoledger.shared.domain.repository.CategoryRepository

/**
 * Returns a live [Flow] of all non-deleted categories.
 */
class GetCategoriesUseCase(private val repository: CategoryRepository) {

    operator fun invoke(): Flow<List<Category>> = repository.observeAll()
}
