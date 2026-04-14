package org.bakarot.autoledger.shared.domain.usecase.transaction

import org.bakarot.autoledger.shared.domain.model.Transaction
import org.bakarot.autoledger.shared.domain.repository.TransactionRepository
import org.bakarot.autoledger.shared.domain.result.Result

/**
 * Returns the transaction with the given [id], or [Result.Failure] if not found.
 */
class GetTransactionByIdUseCase(private val repository: TransactionRepository) {

    suspend operator fun invoke(id: String): Result<Transaction> {
        return repository.getById(id)
    }
}
