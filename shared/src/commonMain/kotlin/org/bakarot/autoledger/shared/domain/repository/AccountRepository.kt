package org.bakarot.autoledger.shared.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bakarot.autoledger.shared.domain.model.Account
import org.bakarot.autoledger.shared.domain.result.Result

/**
 * Contract for local-first account (wallet/bank) persistence.
 */
interface AccountRepository {

    /**
     * Observe all non-deleted accounts, ordered by name ascending.
     * Emits a new list whenever the underlying data changes.
     */
    fun observeAll(): Flow<List<Account>>

    /**
     * Returns a single account by [id], or [Result.Failure] with [AppError.NotFound].
     */
    suspend fun getById(id: String): Result<Account>

    /**
     * Persists a new account locally.
     */
    suspend fun add(account: Account): Result<Account>

    /**
     * Updates an existing account. The [account.id] must already exist.
     */
    suspend fun update(account: Account): Result<Account>

    /**
     * Hard-deletes (MVP) the account identified by [id].
     * Callers must ensure no transactions reference this account before deleting.
     */
    suspend fun delete(id: String): Result<Unit>
}
