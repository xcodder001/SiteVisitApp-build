package com.sitevisit.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sitevisit.app.util.Formatters
import com.sitevisit.app.viewmodel.AppViewModel

private data class DraftItem(val description: String, val quantity: Double, val unitPrice: Double)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuotationScreen(
    viewModel: AppViewModel,
    siteId: Long,
    onBack: () -> Unit,
    onDone: (Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var taxPercent by remember { mutableStateOf("0") }
    var discount by remember { mutableStateOf("0") }
    val items = remember { mutableStateListOf<DraftItem>() }

    var itemDesc by remember { mutableStateOf("") }
    var itemQty by remember { mutableStateOf("1") }
    var itemPrice by remember { mutableStateOf("") }

    val subtotal = items.sumOf { it.quantity * it.unitPrice }
    val afterDiscount = subtotal - (discount.toDoubleOrNull() ?: 0.0)
    val tax = afterDiscount * ((taxPercent.toDoubleOrNull() ?: 0.0) / 100.0)
    val total = afterDiscount + tax

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Quotation") },
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
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Quotation title *") }, modifier = Modifier.fillMaxWidth())

            Card {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Add line item", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(value = itemDesc, onValueChange = { itemDesc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = itemQty, onValueChange = { itemQty = it }, label = { Text("Qty") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = itemPrice, onValueChange = { itemPrice = it }, label = { Text("Unit price") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Button(
                        onClick = {
                            val qty = itemQty.toDoubleOrNull() ?: 1.0
                            val price = itemPrice.toDoubleOrNull() ?: 0.0
                            if (itemDesc.isNotBlank()) {
                                items.add(DraftItem(itemDesc, qty, price))
                                itemDesc = ""; itemQty = "1"; itemPrice = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Add item") }
                }
            }

            if (items.isNotEmpty()) {
                Text("Items", style = MaterialTheme.typography.titleSmall)
                items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.description)
                            Text(
                                "${item.quantity} x ${Formatters.currency(item.unitPrice)} = ${Formatters.currency(item.quantity * item.unitPrice)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        IconButton(onClick = { items.removeAt(index) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Remove item")
                        }
                    }
                    Divider()
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = discount, onValueChange = { discount = it }, label = { Text("Discount") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = taxPercent, onValueChange = { taxPercent = it }, label = { Text("Tax %") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }

            Card {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Subtotal"); Text(Formatters.currency(subtotal))
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("After discount"); Text(Formatters.currency(afterDiscount))
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Tax"); Text(Formatters.currency(tax))
                    }
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Total", style = MaterialTheme.typography.titleMedium)
                        Text(Formatters.currency(total), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.addQuotation(
                        siteId, title,
                        taxPercent.toDoubleOrNull() ?: 0.0,
                        discount.toDoubleOrNull() ?: 0.0,
                        items.map { Triple(it.description, it.quantity, it.unitPrice) }
                    ) { newId -> onDone(newId) }
                },
                enabled = title.isNotBlank() && items.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save Quotation") }
        }
    }
}
