package org.bakarot.autoledger.shared.domain.usecase.transaction

import org.bakarot.autoledger.shared.domain.repository.TransactionRepository
import org.bakarot.autoledger.shared.domain.result.Result

/**
 * Hard-deletes (MVP) the transaction identified by [id].
 * Returns [Result.Failure] with [AppError.NotFound] when no matching transaction exists.
 */
class DeleteTransactionUseCase(private val repository: TransactionRepository) {

    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.delete(id)
    }
}
