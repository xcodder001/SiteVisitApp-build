package com.sitevisit.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sitevisit.app.data.entity.*
import com.sitevisit.app.util.Formatters
import com.sitevisit.app.viewmodel.AppViewModel
import com.sitevisit.app.viewmodel.balanceFor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteDetailScreen(
    viewModel: AppViewModel,
    siteId: Long,
    onBack: () -> Unit,
    onEditSite: () -> Unit,
    onAddVisit: () -> Unit,
    onVisitClick: (Long) -> Unit,
    onAddQuotation: () -> Unit,
    onQuotationClick: (Long) -> Unit,
    onAddPayment: () -> Unit,
    onOpenPhotos: () -> Unit
) {
    val site by remember(siteId) { viewModel.siteFlow(siteId) }.collectAsStateWithLifecycle(initialValue = null)
    val visits by remember(siteId) { viewModel.visitsForSite(siteId) }.collectAsStateWithLifecycle(initialValue = emptyList())
    val quotations by remember(siteId) { viewModel.quotationsForSite(siteId) }.collectAsStateWithLifecycle(initialValue = emptyList())
    val payments by remember(siteId) { viewModel.paymentsForSite(siteId) }.collectAsStateWithLifecycle(initialValue = emptyList())

    var tab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Visits", "Quotes", "Payments")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(site?.name ?: "Site") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = onEditSite) { Icon(Icons.Filled.Edit, contentDescription = "Edit") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = tab) {
                tabs.forEachIndexed { i, t ->
                    Tab(selected = tab == i, onClick = { tab = i }, text = { Text(t) })
                }
            }
            when (tab) {
                0 -> OverviewTab(site, payments, onOpenPhotos, onAddPayment)
                1 -> VisitsTab(visits, onAddVisit, onVisitClick)
                2 -> QuotesTab(quotations, onAddQuotation, onQuotationClick)
                3 -> PaymentsTab(siteId, payments, onAddPayment, viewModel)
            }
        }
    }
}

@Composable
private fun OverviewTab(site: Site?, payments: List<Payment>, onOpenPhotos: () -> Unit, onAddPayment: () -> Unit) {
    val balance = payments.balanceFor(site?.id ?: -1)
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Balance owed", style = MaterialTheme.typography.labelSmall)
                Text(
                    Formatters.currency(balance),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (site != null) {
            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (site.address.isNotBlank()) Text("Address: ${site.address}")
                    if (site.contactName.isNotBlank()) Text("Contact: ${site.contactName} ${site.contactPhone}")
                    Text("Lat/Lng: ${"%.5f".format(site.latitude)}, ${"%.5f".format(site.longitude)}")
                    if (site.notes.isNotBlank()) Text("Notes: ${site.notes}")
                }
            }
        }
        OutlinedButton(onClick = onOpenPhotos, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.PhotoCamera, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Site photos")
        }
    }
}

@Composable
private fun VisitsTab(visits: List<SiteVisit>, onAdd: () -> Unit, onClick: (Long) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (visits.isEmpty()) {
            Text("No visits scheduled.", modifier = Modifier.align(Alignment.Center).padding(24.dp))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(visits, key = { it.id }) { visit ->
                    ListItem(
                        headlineContent = { Text(visit.title) },
                        supportingContent = { Text(Formatters.dateTime(visit.visitDateTime) + " • " + visit.status.name) },
                        modifier = Modifier.clickable { onClick(visit.id) }
                    )
                    Divider()
                }
            }
        }
        FloatingActionButton(
            onClick = onAdd,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Filled.Add, contentDescription = "Add visit") }
    }
}

@Composable
private fun QuotesTab(quotations: List<Quotation>, onAdd: () -> Unit, onClick: (Long) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (quotations.isEmpty()) {
            Text("No quotations yet.", modifier = Modifier.align(Alignment.Center).padding(24.dp))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(quotations, key = { it.id }) { q ->
                    ListItem(
                        headlineContent = { Text(q.title) },
                        supportingContent = { Text(Formatters.date(q.createdAt) + " • " + q.status.name) },
                        modifier = Modifier.clickable { onClick(q.id) }
                    )
                    Divider()
                }
            }
        }
        FloatingActionButton(
            onClick = onAdd,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Filled.Add, contentDescription = "Add quotation") }
    }
}

@Composable
private fun PaymentsTab(siteId: Long, payments: List<Payment>, onAdd: () -> Unit, viewModel: AppViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (payments.isEmpty()) {
            Text("No payments or charges recorded.", modifier = Modifier.align(Alignment.Center).padding(24.dp))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(payments, key = { it.id }) { p ->
                    ListItem(
                        headlineContent = {
                            Text(
                                (if (p.type == PaymentType.CHARGE) "+ " else "- ") +
                                    Formatters.currency(p.amount)
                            )
                        },
                        supportingContent = { Text(p.description.ifBlank { p.type.name } + " • " + Formatters.date(p.date)) },
                        trailingContent = {
                            IconButton(onClick = { viewModel.deletePayment(p) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete")
                            }
                        }
                    )
                    Divider()
                }
            }
        }
        FloatingActionButton(
            onClick = onAdd,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Filled.Add, contentDescription = "Add payment") }
    }
}
