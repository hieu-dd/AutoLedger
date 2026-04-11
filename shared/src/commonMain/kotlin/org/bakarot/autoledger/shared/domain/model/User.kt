package org.bakarot.autoledger.shared.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val createdAt: Instant,
    val updatedAt: Instant
)
