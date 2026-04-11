package org.bakarot.autoledger.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class AccountType {
    CASH,
    BANK,
    CREDIT,
    INVESTMENT
}
