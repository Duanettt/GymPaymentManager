package com.duanett.gymmanager.presentation.customers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.duanett.gymmanager.domain.model.Customer
import com.duanett.gymmanager.domain.model.MembershipType
import com.duanett.gymmanager.presentation.customers.CustomerListContract.Effect
import com.duanett.gymmanager.presentation.customers.CustomerListContract.Intent
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.absoluteValue

// ─── Avatar helpers ───────────────────────────────────────────────────────────

private val AvatarPalette = listOf(
    Color(0xFF7986CB), Color(0xFF4DB6AC), Color(0xFFFF8A65),
    Color(0xFFA1887F), Color(0xFF4FC3F7), Color(0xFFAED581),
    Color(0xFFFFB74D), Color(0xFFBA68C8)
)

private fun avatarColor(name: String): Color =
    AvatarPalette[name.hashCode().absoluteValue % AvatarPalette.size]

private fun initials(name: String): String =
    name.trim().split("\\s+".toRegex())
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    onNavigateToDetail: (Int) -> Unit = {},
    onNavigateToAdd: () -> Unit = {},
    viewModel: CustomerListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var searchVisible by remember { mutableStateOf(false) }

    // Derived stats — computed here so the ViewModel stays unchanged
    val monthlyCount = state.customers.count { it.membershipType == MembershipType.MONTHLY }
    val dropInCount  = state.customers.count { it.membershipType == MembershipType.DROP_IN }
    val activeCount  = state.customers.count { it.isActive }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is Effect.NavigateToCustomerDetail -> onNavigateToDetail(effect.customerId)
                is Effect.NavigateToAddCustomer    -> onNavigateToAdd()
                is Effect.ShowSnackbar             -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Customers",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = { searchVisible = !searchVisible }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (searchVisible)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { /* notifications — future */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.handleIntent(Intent.AddCustomerClicked) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add customer",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search bar slides in below the app bar when the search icon is tapped.
            // Pinned outside the scroll so it stays visible while browsing the list.
            AnimatedVisibility(visible = searchVisible) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.handleIntent(Intent.SearchCustomers(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search by name...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                viewModel.handleIntent(Intent.SearchCustomers(""))
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.large
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                // Hero banner
                item {
                    HeroBannerCard(
                        totalCount = state.customers.size,
                        onAddClick = { viewModel.handleIntent(Intent.AddCustomerClicked) }
                    )
                }

                // Stats row
                item {
                    StatsRow(
                        monthly = monthlyCount,
                        dropIns = dropInCount,
                        active  = activeCount
                    )
                }

                // Body — loading / empty / list
                when {
                    state.isLoading -> item { LoadingContent() }
                    state.isEmpty   -> item { EmptyContent() }
                    else -> {
                        item { SectionHeader(count = state.customers.size) }

                        itemsIndexed(
                            items = state.customers,
                            key   = { _, customer -> customer.id }
                        ) { index, customer ->
                            CustomerRow(
                                customer = customer,
                                onClick  = { viewModel.handleIntent(Intent.SelectCustomer(customer)) }
                            )
                            if (index < state.customers.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 72.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Hero banner ──────────────────────────────────────────────────────────────

@Composable
private fun HeroBannerCard(totalCount: Int, onAddClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Track and manage",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
                Text(
                    text = "your members",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor   = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Member", fontWeight = FontWeight.SemiBold)
                }
            }

            // Decorative illustration circle
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

// ─── Stats row ────────────────────────────────────────────────────────────────

@Composable
private fun StatsRow(monthly: Int, dropIns: Int, active: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(label = "Monthly",  value = monthly, modifier = Modifier.weight(1f),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor   = MaterialTheme.colorScheme.onPrimaryContainer)

        StatCard(label = "Drop-Ins", value = dropIns,  modifier = Modifier.weight(1f),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor   = MaterialTheme.colorScheme.onSecondaryContainer)

        StatCard(label = "Active",   value = active,   modifier = Modifier.weight(1f),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor   = MaterialTheme.colorScheme.onTertiaryContainer)
    }
}

@Composable
private fun StatCard(
    label: String,
    value: Int,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.7f)
            )
        }
    }
}

// ─── Section header ───────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "All Members",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "$count total",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─── Customer row ─────────────────────────────────────────────────────────────

@Composable
private fun CustomerRow(customer: Customer, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InitialsAvatar(name = customer.name)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = customer.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = customer.phone,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = when (customer.membershipType) {
                    MembershipType.MONTHLY -> "Monthly"
                    MembershipType.DROP_IN -> "Drop-In"
                },
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            StatusBadge(isActive = customer.isActive)
        }
    }
}

// ─── Initials avatar ──────────────────────────────────────────────────────────

@Composable
private fun InitialsAvatar(name: String) {
    val color   = avatarColor(name)
    val letters = initials(name)

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letters,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

// ─── Status badge (pill) ──────────────────────────────────────────────────────

@Composable
private fun StatusBadge(isActive: Boolean) {
    val bgColor   = if (isActive) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
    val textColor = if (isActive) Color(0xFF2E7D32) else Color(0xFFE65100)
    val dotColor  = if (isActive) Color(0xFF4CAF50) else Color(0xFFFF9800)
    val label     = if (isActive) "Active" else "Inactive"

    Surface(
        shape = CircleShape,
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Canvas(modifier = Modifier.size(6.dp)) {
                drawCircle(color = dotColor)
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

// ─── Loading / empty states ───────────────────────────────────────────────────

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No customers yet.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Tap + to add one",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
