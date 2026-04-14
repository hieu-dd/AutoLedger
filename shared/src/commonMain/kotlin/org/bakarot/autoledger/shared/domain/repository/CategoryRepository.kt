package org.bakarot.autoledger.shared.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bakarot.autoledger.shared.domain.model.Category
import org.bakarot.autoledger.shared.domain.result.Result

/**
 * Contract for local-first category persistence.
 */
interface CategoryRepository {

    /**
     * Observe all non-deleted categories, ordered by name ascending.
     * Emits a new list whenever the underlying data changes.
     */
    fun observeAll(): Flow<List<Category>>

    /**
     * Observe only the built-in (seeded) default categories.
     */
    fun observeDefaults(): Flow<List<Category>>

    /**
     * Returns a single category by [id], or [Result.Failure] with [AppError.NotFound].
     */
    suspend fun getById(id: String): Result<Category>

    /**
     * Persists a new category locally.
     */
    suspend fun add(category: Category): Result<Category>

    /**
     * Updates an existing category. The [category.id] must already exist.
     */
    suspend fun update(category: Category): Result<Category>

    /**
     * Hard-deletes (MVP) the category identified by [id].
     */
    suspend fun delete(id: String): Result<Unit>
}
