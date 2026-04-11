package org.bakarot.autoledger.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class TransactionType {
    INCOME,
    EXPENSE
}
