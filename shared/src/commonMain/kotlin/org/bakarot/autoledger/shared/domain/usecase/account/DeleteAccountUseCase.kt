package org.bakarot.autoledger.shared.domain.usecase.account

import org.bakarot.autoledger.shared.domain.repository.AccountRepository
import org.bakarot.autoledger.shared.domain.result.Result

/**
 * Hard-deletes (MVP) the account identified by [id].
 * Callers must verify no transactions reference this account before invoking.
 */
class DeleteAccountUseCase(private val repository: AccountRepository) {

    suspend operator fun invoke(id: String): Result<Unit> = repository.delete(id)
}
