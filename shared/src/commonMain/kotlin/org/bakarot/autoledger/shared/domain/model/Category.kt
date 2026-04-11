package org.bakarot.autoledger.shared.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val userId: String,
    val name: String,
    val icon: String,
    val colorHex: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isDeleted: Boolean = false
)
