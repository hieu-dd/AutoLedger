package org.bakarot.autoledger.shared.domain.usecase.transaction

import org.bakarot.autoledger.shared.domain.error.AppError
import org.bakarot.autoledger.shared.domain.model.Transaction
import org.bakarot.autoledger.shared.domain.repository.TransactionRepository
import org.bakarot.autoledger.shared.domain.result.Result

/**
 * Validates and persists a new [transaction] to local storage.
 *
 * Validation rules:
 * - [Transaction.amount] must be > 0
 * - [Transaction.description] must not be blank
 * - [Transaction.accountId] must not be blank
 * - [Transaction.categoryId] must not be blank
 */
class AddTransactionUseCase(private val repository: TransactionRepository) {

    suspend operator fun invoke(transaction: Transaction): Result<Transaction> {
        val validationError = validate(transaction)
        if (validationError != null) return Result.Failure(validationError)
        return repository.add(transaction)
    }

    private fun validate(transaction: Transaction): AppError.Validation? {
        return when {
            transaction.amount <= 0 -> AppError.Validation("Amount must be greater than zero")
            transaction.description.isBlank() -> AppError.Validation("Description must not be blank")
            transaction.accountId.isBlank() -> AppError.Validation("Account must be specified")
            transaction.categoryId.isBlank() -> AppError.Validation("Category must be specified")
            else -> null
        }
    }
}
