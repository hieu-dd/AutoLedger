package org.bakarot.autoledger.shared.domain.usecase.account

import kotlinx.coroutines.flow.Flow
import org.bakarot.autoledger.shared.domain.model.Account
import org.bakarot.autoledger.shared.domain.repository.AccountRepository

/**
 * Returns a live [Flow] of all non-deleted accounts.
 */
class GetAccountsUseCase(private val repository: AccountRepository) {

    operator fun invoke(): Flow<List<Account>> = repository.observeAll()
}
