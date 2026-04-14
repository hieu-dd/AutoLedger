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
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AddTransactionUseCaseTest {

    private val now: Instant = Clock.System.now()

    private fun makeTransaction(
        amount: Double = 100_000.0,
        description: String = "Lunch",
        accountId: String = "acc-1",
        categoryId: String = "cat-1",
    ) = Transaction(
        id = "tx-1",
        userId = "user-1",
        accountId = accountId,
        categoryId = categoryId,
        amount = amount,
        type = TransactionType.EXPENSE,
        description = description,
        date = now,
        createdAt = now,
        updatedAt = now,
        syncStatus = SyncStatus.PENDING,
        isAutoCapture = false,
        isDeleted = false,
    )

    @Test
    fun `returns success when transaction is valid`() = runTest {
        val repository = FakeTransactionRepository()
        val useCase = AddTransactionUseCase(repository)
        val tx = makeTransaction()

        val result = useCase(tx)

        assertIs<Result.Success<Transaction>>(result)
        assertEquals(tx, result.data)
    }

    @Test
    fun `returns validation error when amount is zero`() = runTest {
        val repository = FakeTransactionRepository()
        val useCase = AddTransactionUseCase(repository)

        val result = useCase(makeTransaction(amount = 0.0))

        assertIs<Result.Failure>(result)
        assertIs<AppError.Validation>(result.error)
    }

    @Test
    fun `returns validation error when amount is negative`() = runTest {
        val repository = FakeTransactionRepository()
        val useCase = AddTransactionUseCase(repository)

        val result = useCase(makeTransaction(amount = -1.0))

        assertIs<Result.Failure>(result)
        assertIs<AppError.Validation>(result.error)
    }

    @Test
    fun `returns validation error when description is blank`() = runTest {
        val repository = FakeTransactionRepository()
        val useCase = AddTransactionUseCase(repository)

        val result = useCase(makeTransaction(description = "  "))

        assertIs<Result.Failure>(result)
        assertIs<AppError.Validation>(result.error)
    }

    @Test
    fun `returns validation error when accountId is blank`() = runTest {
        val repository = FakeTransactionRepository()
        val useCase = AddTransactionUseCase(repository)

        val result = useCase(makeTransaction(accountId = ""))

        assertIs<Result.Failure>(result)
        assertIs<AppError.Validation>(result.error)
    }

    @Test
    fun `returns validation error when categoryId is blank`() = runTest {
        val repository = FakeTransactionRepository()
        val useCase = AddTransactionUseCase(repository)

        val result = useCase(makeTransaction(categoryId = ""))

        assertIs<Result.Failure>(result)
        assertIs<AppError.Validation>(result.error)
    }
}

/** Minimal in-memory fake — no DB, no Android dependencies. */
private class FakeTransactionRepository : TransactionRepository {
    private val store = mutableMapOf<String, Transaction>()

    override fun observeAll(): Flow<List<Transaction>> = flowOf(store.values.toList())

    override fun observeFiltered(
        from: Instant?,
        to: Instant?,
        type: TransactionType?,
        categoryId: String?,
        accountId: String?,
        isAutoCapture: Boolean?,
        searchQuery: String?,
    ): Flow<List<Transaction>> = flowOf(store.values.toList())

    override suspend fun getById(id: String): Result<Transaction> {
        return store[id]?.let { Result.Success(it) }
            ?: Result.Failure(AppError.NotFound(id))
    }

    override suspend fun add(transaction: Transaction): Result<Transaction> {
        store[transaction.id] = transaction
        return Result.Success(transaction)
    }

    override suspend fun update(transaction: Transaction): Result<Transaction> {
        if (!store.containsKey(transaction.id)) return Result.Failure(AppError.NotFound(transaction.id))
        store[transaction.id] = transaction
        return Result.Success(transaction)
    }

    override suspend fun delete(id: String): Result<Unit> {
        if (!store.containsKey(id)) return Result.Failure(AppError.NotFound(id))
        store.remove(id)
        return Result.Success(Unit)
    }
}
