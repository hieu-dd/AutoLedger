package org.bakarot.autoledger.shared.domain.usecase.account

import org.bakarot.autoledger.shared.domain.model.Account
import org.bakarot.autoledger.shared.domain.repository.AccountRepository
import org.bakarot.autoledger.shared.domain.result.Result

/**
 * Returns the account with the given [id], or [Result.Failure] if not found.
 */
class GetAccountByIdUseCase(private val repository: AccountRepository) {

    suspend operator fun invoke(id: String): Result<Account> = repository.getById(id)
}
