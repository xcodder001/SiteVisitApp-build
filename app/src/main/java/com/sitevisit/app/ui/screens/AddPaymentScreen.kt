package com.sitevisit.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sitevisit.app.data.entity.PaymentType
import com.sitevisit.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentScreen(
    viewModel: AppViewModel,
    siteId: Long,
    onDone: () -> Unit
) {
    var type by remember { mutableStateOf(PaymentType.CHARGE) }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Payment / Charge") },
                navigationIcon = {
                    IconButton(onClick = onDone) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = type == PaymentType.CHARGE,
                    onClick = { type = PaymentType.CHARGE },
                    label = { Text("Charge (billed to client)") }
                )
                FilterChip(
                    selected = type == PaymentType.PAYMENT,
                    onClick = { type = PaymentType.PAYMENT },
                    label = { Text("Payment received") }
                )
            }
            OutlinedTextField(
                value = amount, onValueChange = { amount = it }, label = { Text("Amount *") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description, onValueChange = { description = it }, label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        viewModel.addPayment(siteId, type, amt, description)
                        onDone()
                    }
                },
                enabled = (amount.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }
        }
    }
}
