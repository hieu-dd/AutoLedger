package org.bakarot.autoledger.shared.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: String,
    val userId: String,
    val accountId: String,
    val categoryId: String,
    val amount: Double,
    val type: TransactionType,
    val description: String,
    val date: Instant,
    val createdAt: Instant,
    val updatedAt: Instant,
    val serverTimestamp: Instant? = null,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val isAutoCapture: Boolean = false,
    val isDeleted: Boolean = false
)
