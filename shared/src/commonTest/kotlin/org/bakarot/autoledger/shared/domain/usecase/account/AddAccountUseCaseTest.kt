package org.bakarot.autoledger.shared.domain.usecase.account

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.bakarot.autoledger.shared.domain.error.AppError
import org.bakarot.autoledger.shared.domain.model.Account
import org.bakarot.autoledger.shared.domain.model.AccountType
import org.bakarot.autoledger.shared.domain.repository.AccountRepository
import org.bakarot.autoledger.shared.domain.result.Result
import kotlin.test.Test
import kotlin.test.assertIs

class AddAccountUseCaseTest {

    private val now = Clock.System.now()

    private fun makeAccount(
        name: String = "Vietcombank",
        currency: String = "VND",
    ) = Account(
        id = "acc-1",
        userId = "user-1",
        name = name,
        type = AccountType.BANK,
        currency = currency,
        initialBalance = 0.0,
        currentBalance = 0.0,
        createdAt = now,
        updatedAt = now,
    )

    private val fakeRepo = object : AccountRepository {
        private val store = mutableMapOf<String, Account>()
        override fun observeAll(): Flow<List<Account>> = flowOf(store.values.toList())
        override suspend fun getById(id: String): Result<Account> =
            store[id]?.let { Result.Success(it) } ?: Result.Failure(AppError.NotFound(id))
        override suspend fun add(account: Account): Result<Account> {
            store[account.id] = account; return Result.Success(account)
        }
        override suspend fun update(account: Account): Result<Account> {
            if (!store.containsKey(account.id)) return Result.Failure(AppError.NotFound(account.id))
            store[account.id] = account; return Result.Success(account)
        }
        override suspend fun delete(id: String): Result<Unit> {
            if (!store.containsKey(id)) return Result.Failure(AppError.NotFound(id))
            store.remove(id); return Result.Success(Unit)
        }
    }

    @Test
    fun `returns success for valid account`() = runTest {
        val result = AddAccountUseCase(fakeRepo)(makeAccount())
        assertIs<Result.Success<Account>>(result)
    }

    @Test
    fun `returns validation error when name is blank`() = runTest {
        val result = AddAccountUseCase(fakeRepo)(makeAccount(name = ""))
        assertIs<Result.Failure>(result)
        assertIs<AppError.Validation>(result.error)
    }

    @Test
    fun `returns validation error when currency is blank`() = runTest {
        val result = AddAccountUseCase(fakeRepo)(makeAccount(currency = ""))
        assertIs<Result.Failure>(result)
        assertIs<AppError.Validation>(result.error)
    }
}
