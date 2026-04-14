package org.bakarot.autoledger.shared.domain.usecase.category

import org.bakarot.autoledger.shared.domain.model.Category
import org.bakarot.autoledger.shared.domain.repository.CategoryRepository
import org.bakarot.autoledger.shared.domain.result.Result

/**
 * Returns the category with the given [id], or [Result.Failure] if not found.
 */
class GetCategoryByIdUseCase(private val repository: CategoryRepository) {

    suspend operator fun invoke(id: String): Result<Category> = repository.getById(id)
}
