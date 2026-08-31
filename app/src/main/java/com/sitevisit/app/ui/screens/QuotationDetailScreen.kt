package com.sitevisit.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sitevisit.app.data.entity.QuotationStatus
import com.sitevisit.app.util.Formatters
import com.sitevisit.app.viewmodel.AppViewModel
import com.sitevisit.app.viewmodel.subtotal
import com.sitevisit.app.viewmodel.total

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotationDetailScreen(
    viewModel: AppViewModel,
    quotationId: Long,
    onBack: () -> Unit
) {
    val quotation by remember(quotationId) { viewModel.quotationFlow(quotationId) }.collectAsStateWithLifecycle(initialValue = null)
    val items by remember(quotationId) { viewModel.quotationItems(quotationId) }.collectAsStateWithLifecycle(initialValue = emptyList())

    val q = quotation
    val subtotal = items.subtotal()
    val afterDiscount = subtotal - (q?.discount ?: 0.0)
    val tax = afterDiscount * ((q?.taxPercent ?: 0.0) / 100.0)
    val total = q?.total(items) ?: 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(q?.title ?: "Quotation") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (q != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    QuotationStatus.values().forEach { status ->
                        FilterChip(
                            selected = q.status == status,
                            onClick = { viewModel.updateQuotationStatus(q, status) },
                            label = { Text(status.name) }
                        )
                    }
                }
            }

            Text("Items", style = MaterialTheme.typography.titleSmall)
            items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.description)
                        Text(
                            "${item.quantity} x ${Formatters.currency(item.unitPrice)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(Formatters.currency(item.quantity * item.unitPrice))
                    IconButton(onClick = { viewModel.deleteQuotationItem(item) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Remove")
                    }
                }
                Divider()
            }

            Card {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Subtotal"); Text(Formatters.currency(subtotal))
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Discount"); Text("- " + Formatters.currency(q?.discount ?: 0.0))
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Tax (${q?.taxPercent ?: 0}%)"); Text(Formatters.currency(tax))
                    }
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Total", style = MaterialTheme.typography.titleMedium)
                        Text(Formatters.currency(total), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            if (q != null) {
                OutlinedButton(
                    onClick = { viewModel.deleteQuotation(q); onBack() },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Delete quotation") }
            }
        }
    }
}
