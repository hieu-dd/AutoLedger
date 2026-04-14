package org.bakarot.autoledger.shared.domain.usecase.transaction

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.bakarot.autoledger.shared.domain.model.Transaction
import org.bakarot.autoledger.shared.domain.model.TransactionType
import org.bakarot.autoledger.shared.domain.repository.TransactionRepository

data class GetTransactionsParams(
    val from: Instant? = null,
    val to: Instant? = null,
    val type: TransactionType? = null,
    val categoryId: String? = null,
    val accountId: String? = null,
    val isAutoCapture: Boolean? = null,
    val searchQuery: String? = null,
)

/**
 * Returns a live [Flow] of transactions matching the given [params].
 * When params is null, all transactions are returned (equivalent to [GetTransactionsParams] defaults).
 */
class GetTransactionsUseCase(private val repository: TransactionRepository) {

    operator fun invoke(params: GetTransactionsParams? = null): Flow<List<Transaction>> {
        return if (params == null) {
            repository.observeAll()
        } else {
            repository.observeFiltered(
                from = params.from,
                to = params.to,
                type = params.type,
                categoryId = params.categoryId,
                accountId = params.accountId,
                isAutoCapture = params.isAutoCapture,
                searchQuery = params.searchQuery,
            )
        }
    }
}
