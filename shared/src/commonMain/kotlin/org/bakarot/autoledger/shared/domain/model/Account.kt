package org.bakarot.autoledger.shared.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val id: String,
    val userId: String,
    val name: String,
    val type: AccountType,
    val currency: String,
    val initialBalance: Double,
    val currentBalance: Double,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isDeleted: Boolean = false
)
