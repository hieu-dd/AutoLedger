package org.bakarot.autoledger.shared.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class DomainModelSerializationTest {

    @Test
    fun testTransactionSerializationRoundTrip() {
        val now = Clock.System.now()
        val transaction = Transaction(
            id = "tx-123",
            userId = "user-456",
            accountId = "acc-789",
            categoryId = "cat-000",
            amount = 1250.50,
            type = TransactionType.EXPENSE,
            description = "Grocery Shopping",
            date = now,
            createdAt = now,
            updatedAt = now,
            serverTimestamp = now,
            syncStatus = SyncStatus.SYNCED,
            isAutoCapture = true,
            isDeleted = false
        )

        val json = Json.encodeToString(Transaction.serializer(), transaction)
        val deserialized = Json.decodeFromString(Transaction.serializer(), json)

        assertEquals(transaction, deserialized)
    }

    @Test
    fun testAccountSerializationRoundTrip() {
        val now = Clock.System.now()
        val account = Account(
            id = "acc-1",
            userId = "user-1",
            name = "Main Bank",
            type = AccountType.BANK,
            currency = "USD",
            initialBalance = 1000.0,
            currentBalance = 1500.0,
            createdAt = now,
            updatedAt = now,
            isDeleted = false
        )

        val json = Json.encodeToString(Account.serializer(), account)
        val deserialized = Json.decodeFromString(Account.serializer(), json)

        assertEquals(account, deserialized)
    }
}
