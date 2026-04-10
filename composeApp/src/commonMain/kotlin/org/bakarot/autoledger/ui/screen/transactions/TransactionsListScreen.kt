package org.bakarot.autoledger.ui.screen.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bakarot.autoledger.ui.theme.AutoLedgerDesign

private val incomeGreen = Color(0xFF00897B)

private data class TransactionItem(
    val id: Int,
    val merchant: String,
    val paymentMethod: String,
    val amount: String,
    val isExpense: Boolean,
    val time: String,
    val isAuto: Boolean,
    val icon: ImageVector,
)

private data class TransactionGroup(
    val dateLabel: String,
    val transactions: List<TransactionItem>,
)

private val sampleGroups =
    listOf(
        TransactionGroup(
            dateLabel = "TODAY",
            transactions =
                listOf(
                    TransactionItem(
                        1,
                        "Whole Foods Market",
                        "Apple Pay • Amex Gold",
                        "-\$142.60",
                        true,
                        "12:45 PM",
                        true,
                        Icons.Default.ShoppingBag,
                    ),
                    TransactionItem(
                        2,
                        "Blue Bottle Coffee",
                        "Debit • Chase Checking",
                        "-\$6.50",
                        true,
                        "09:12 AM",
                        true,
                        Icons.Default.LocalCafe,
                    ),
                ),
        ),
        TransactionGroup(
            dateLabel = "YESTERDAY",
            transactions =
                listOf(
                    TransactionItem(
                        3,
                        "Payroll Deposit",
                        "Direct Deposit • Savings",
                        "+\$3,450.00",
                        false,
                        "08:00 AM",
                        false,
                        Icons.Default.Payments,
                    ),
                    TransactionItem(
                        4,
                        "Shell Oil",
                        "Apple Pay • Amex Gold",
                        "-\$54.12",
                        true,
                        "06:45 PM",
                        true,
                        Icons.Default.DirectionsCar,
                    ),
                ),
        ),
    )

private val filterLabels = listOf("This Month", "Category", "Income/Expense", "Auto-captured")

@Composable
fun TransactionsListScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
    ) {
        item {
            SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
            Spacer(Modifier.height(16.dp))
        }

        item {
            FilterChips(
                selectedIndex = selectedFilter,
                onFilterSelected = { selectedFilter = it },
            )
            Spacer(Modifier.height(8.dp))
        }

        sampleGroups.forEach { group ->
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = group.dateLabel,
                    style =
                        MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.6.sp,
                        ),
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                )
            }
            items(group.transactions, key = { it.id }) { transaction ->
                SwipeableTransactionItem(transaction = transaction)
                Spacer(Modifier.height(12.dp))
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            OfflineBanner()
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    val extra = AutoLedgerDesign.extraColors
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Search transactions...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp),
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(50),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = extra.surfaceContainerLowest,
                unfocusedContainerColor = extra.surfaceContainerLowest,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary,
            ),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun FilterChips(
    selectedIndex: Int,
    onFilterSelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        filterLabels.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            Surface(
                onClick = { onFilterSelected(index) },
                shape = CircleShape,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE6E8EB),
                modifier = Modifier.height(38.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 18.dp),
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color =
                            if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableTransactionItem(transaction: TransactionItem) {
    val dismissState = rememberSwipeToDismissBoxState()
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(end = 20.dp),
                )
            }
        },
        enableDismissFromStartToEnd = false,
        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
    ) {
        TransactionCard(transaction = transaction)
    }
}

@Composable
private fun TransactionCard(transaction: TransactionItem) {
    val extra = AutoLedgerDesign.extraColors
    Surface(
        color = extra.surfaceContainerLowest,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = transaction.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = transaction.merchant,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        if (transaction.isAuto) {
                            AutoTag()
                        }
                    }
                    Text(
                        text = transaction.paymentMethod,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = transaction.amount,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (transaction.isExpense) MaterialTheme.colorScheme.error else incomeGreen,
                )
                Text(
                    text = transaction.time,
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 0.8.sp,
                        ),
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        }
    }
}

@Composable
private fun AutoTag() {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.2f),
    ) {
        Text(
            text = "AUTO",
            style =
                MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                ),
            color = MaterialTheme.colorScheme.inversePrimary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun OfflineBanner() {
    val extra = AutoLedgerDesign.extraColors
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = extra.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f)),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(extra.tertiaryFixedDim.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = "Working Offline",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "3 transactions are waiting to be synced with the server.",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
