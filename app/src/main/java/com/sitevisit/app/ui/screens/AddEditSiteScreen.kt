package com.sitevisit.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sitevisit.app.data.entity.Site
import com.sitevisit.app.util.LocationHelper
import com.sitevisit.app.viewmodel.AppViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSiteScreen(
    viewModel: AppViewModel,
    siteId: Long?,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isEdit = siteId != null

    val siteFlow = remember(siteId) { siteId?.let { viewModel.siteFlow(it) } ?: kotlinx.coroutines.flow.flowOf<Site?>(null) }
    val existing by siteFlow.collectAsStateWithLifecycle(initialValue = null)

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var contactName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf<Double?>(null) }
    var lng by remember { mutableStateOf<Double?>(null) }
    var locating by remember { mutableStateOf(false) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(existing) {
        val e = existing
        if (e != null && !loaded) {
            name = e.name; address = e.address; contactName = e.contactName
            contactPhone = e.contactPhone; notes = e.notes; lat = e.latitude; lng = e.longitude
            loaded = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Edit Site" else "Add Site") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Site name *") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = contactName, onValueChange = { contactName = it }, label = { Text("Contact name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = contactPhone, onValueChange = { contactPhone = it }, label = { Text("Contact phone") }, modifier = Modifier.fillMaxWidth())

            Card {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Location", style = MaterialTheme.typography.titleSmall)
                    Text(
                        if (lat != null && lng != null) "Lat: ${"%.6f".format(lat)}, Lng: ${"%.6f".format(lng)}"
                        else "No location set",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = {
                            locating = true
                            scope.launch {
                                val loc = LocationHelper.getCurrentLocation(context)
                                if (loc != null) {
                                    lat = loc.first
                                    lng = loc.second
                                }
                                locating = false
                            }
                        },
                        enabled = !locating
                    ) {
                        Icon(Icons.Filled.MyLocation, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (locating) "Getting location..." else "Use current GPS location")
                    }
                }
            }

            OutlinedTextField(
                value = notes, onValueChange = { notes = it }, label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(), minLines = 3
            )

            Button(
                onClick = {
                    val finalLat = lat ?: 0.0
                    val finalLng = lng ?: 0.0
                    if (isEdit && existing != null) {
                        viewModel.updateSite(
                            existing!!.copy(
                                name = name, address = address, contactName = contactName,
                                contactPhone = contactPhone, notes = notes,
                                latitude = finalLat, longitude = finalLng
                            )
                        )
                    } else {
                        viewModel.addSite(name, address, contactName, contactPhone, finalLat, finalLng, notes)
                    }
                    onDone()
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEdit) "Save Changes" else "Add Site")
            }
        }
    }
}
