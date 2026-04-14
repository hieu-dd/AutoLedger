package org.bakarot.autoledger.shared.domain.usecase.account

import org.bakarot.autoledger.shared.domain.error.AppError
import org.bakarot.autoledger.shared.domain.model.Account
import org.bakarot.autoledger.shared.domain.repository.AccountRepository
import org.bakarot.autoledger.shared.domain.result.Result

/**
 * Validates and persists a new [account] to local storage.
 *
 * Validation rules:
 * - [Account.name] must not be blank
 * - [Account.currency] must not be blank
 */
class AddAccountUseCase(private val repository: AccountRepository) {

    suspend operator fun invoke(account: Account): Result<Account> {
        val validationError = validate(account)
        if (validationError != null) return Result.Failure(validationError)
        return repository.add(account)
    }

    private fun validate(account: Account): AppError.Validation? {
        return when {
            account.name.isBlank() -> AppError.Validation("Account name must not be blank")
            account.currency.isBlank() -> AppError.Validation("Currency must not be blank")
            else -> null
        }
    }
}
