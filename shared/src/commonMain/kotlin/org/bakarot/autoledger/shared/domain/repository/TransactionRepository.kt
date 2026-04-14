package org.bakarot.autoledger.shared.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.bakarot.autoledger.shared.domain.model.Transaction
import org.bakarot.autoledger.shared.domain.model.TransactionType
import org.bakarot.autoledger.shared.domain.result.Result

/**
 * Contract for local-first transaction persistence.
 * All operations are offline-capable. Sync is handled separately (post-MVP).
 */
interface TransactionRepository {

    /**
     * Observe all non-deleted transactions, ordered by date descending.
     * Emits a new list whenever the underlying data changes.
     */
    fun observeAll(): Flow<List<Transaction>>

    /**
     * Observe transactions filtered by optional criteria.
     *
     * @param from   inclusive start of date range (null = no lower bound)
     * @param to     inclusive end of date range (null = no upper bound)
     * @param type   filter by income/expense (null = both)
     * @param categoryId filter by a specific category (null = all)
     * @param accountId  filter by a specific account (null = all)
     * @param isAutoCapture when true returns only auto-captured; when false only manual;
     *                      null returns both
     * @param searchQuery free-text search against description (null = no filter)
     */
    fun observeFiltered(
        from: Instant? = null,
        to: Instant? = null,
        type: TransactionType? = null,
        categoryId: String? = null,
        accountId: String? = null,
        isAutoCapture: Boolean? = null,
        searchQuery: String? = null,
    ): Flow<List<Transaction>>

    /**
     * Returns a single transaction by [id], or [Result.Failure] with [AppError.NotFound].
     */
    suspend fun getById(id: String): Result<Transaction>

    /**
     * Persists a new transaction locally.
     *
     * @return [Result.Success] with the saved transaction, or [Result.Failure] on validation / storage error.
     */
    suspend fun add(transaction: Transaction): Result<Transaction>

    /**
     * Updates an existing transaction. The [transaction.id] must already exist.
     *
     * @return [Result.Success] with the updated transaction, or [Result.Failure] with [AppError.NotFound].
     */
    suspend fun update(transaction: Transaction): Result<Transaction>

    /**
     * Hard-deletes (MVP) the transaction identified by [id].
     *
     * @return [Result.Success] with Unit, or [Result.Failure] with [AppError.NotFound].
     */
    suspend fun delete(id: String): Result<Unit>
}
