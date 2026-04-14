package org.bakarot.autoledger.shared.domain.usecase.transaction

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.bakarot.autoledger.shared.domain.error.AppError
import org.bakarot.autoledger.shared.domain.model.SyncStatus
import org.bakarot.autoledger.shared.domain.model.Transaction
import org.bakarot.autoledger.shared.domain.model.TransactionType
import org.bakarot.autoledger.shared.domain.repository.TransactionRepository
import org.bakarot.autoledger.shared.domain.result.Result
import kotlin.test.Test
import kotlin.test.assertIs

class DeleteTransactionUseCaseTest {

    private val now = Clock.System.now()

    private fun buildRepo(vararg transactions: Transaction): TransactionRepository {
        val store = transactions.associateBy { it.id }.toMutableMap()
        return object : TransactionRepository {
            override fun observeAll(): Flow<List<Transaction>> = flowOf(store.values.toList())
            override fun observeFiltered(
                from: Instant?, to: Instant?, type: TransactionType?,
                categoryId: String?, accountId: String?,
                isAutoCapture: Boolean?, searchQuery: String?,
            ): Flow<List<Transaction>> = flowOf(store.values.toList())
            override suspend fun getById(id: String): Result<Transaction> =
                store[id]?.let { Result.Success(it) } ?: Result.Failure(AppError.NotFound(id))
            override suspend fun add(transaction: Transaction): Result<Transaction> {
                store[transaction.id] = transaction; return Result.Success(transaction)
            }
            override suspend fun update(transaction: Transaction): Result<Transaction> {
                if (!store.containsKey(transaction.id)) return Result.Failure(AppError.NotFound(transaction.id))
                store[transaction.id] = transaction; return Result.Success(transaction)
            }
            override suspend fun delete(id: String): Result<Unit> {
                if (!store.containsKey(id)) return Result.Failure(AppError.NotFound(id))
                store.remove(id); return Result.Success(Unit)
            }
        }
    }

    private fun tx(id: String) = Transaction(
        id = id, userId = "u", accountId = "a", categoryId = "c",
        amount = 1.0, type = TransactionType.EXPENSE, description = "d",
        date = now, createdAt = now, updatedAt = now,
        syncStatus = SyncStatus.PENDING, isAutoCapture = false, isDeleted = false,
    )

    @Test
    fun `returns success when transaction exists`() = runTest {
        val repo = buildRepo(tx("tx-1"))
        val result = DeleteTransactionUseCase(repo)("tx-1")
        assertIs<Result.Success<Unit>>(result)
    }

    @Test
    fun `returns NotFound when transaction does not exist`() = runTest {
        val repo = buildRepo()
        val result = DeleteTransactionUseCase(repo)("tx-missing")
        assertIs<Result.Failure>(result)
        assertIs<AppError.NotFound>(result.error)
    }
}
