package com.sitevisit.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sitevisit.app.data.entity.SiteVisit
import com.sitevisit.app.data.entity.VisitStatus
import com.sitevisit.app.util.Formatters
import com.sitevisit.app.viewmodel.AppViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVisitScreen(
    viewModel: AppViewModel,
    siteId: Long?,
    visitId: Long?,
    onDone: () -> Unit
) {
    val isEdit = visitId != null
    var existingVisit by remember { mutableStateOf<SiteVisit?>(null) }
    var resolvedSiteId by remember { mutableStateOf(siteId) }

    val allVisits by viewModel.allVisits.collectAsState(initial = emptyList())
    LaunchedEffect(allVisits, visitId) {
        if (visitId != null) {
            val v = allVisits.find { it.id == visitId }
            if (v != null) {
                existingVisit = v
                resolvedSiteId = v.siteId
            }
        }
    }

    val sites by viewModel.sites.collectAsState()
    val siteName = sites.find { it.id == resolvedSiteId }?.name ?: ""

    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var reminderEnabled by remember { mutableStateOf(true) }
    var reminderMinutes by remember { mutableIntStateOf(60) }
    var loaded by remember { mutableStateOf(false) }

    val cal = remember { Calendar.getInstance() }
    var dateTimeMillis by remember { mutableStateOf(cal.timeInMillis + 60 * 60 * 1000) }

    LaunchedEffect(existingVisit) {
        val v = existingVisit
        if (v != null && !loaded) {
            title = v.title
            notes = v.notes
            reminderEnabled = v.reminderEnabled
            reminderMinutes = v.reminderMinutesBefore
            dateTimeMillis = v.visitDateTime
            loaded = true
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Edit Visit" else "Schedule Visit") },
                navigationIcon = {
                    IconButton(onClick = onDone) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    if (isEdit) {
                        IconButton(onClick = {
                            existingVisit?.let { viewModel.deleteVisit(it) }
                            onDone()
                        }) { Icon(Icons.Filled.Delete, contentDescription = "Delete visit") }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (siteName.isNotBlank()) {
                Text("Site: $siteName", style = MaterialTheme.typography.titleSmall)
            }
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Visit title *") }, modifier = Modifier.fillMaxWidth())

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { showDatePicker = true }) {
                    Text(Formatters.date(dateTimeMillis))
                }
                OutlinedButton(onClick = { showTimePicker = true }) {
                    Text(Formatters.time(dateTimeMillis))
                }
            }

            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

            Card {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = reminderEnabled, onCheckedChange = { reminderEnabled = it })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Remind me before the visit")
                    }
                    if (reminderEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val options = listOf(15 to "15 min before", 30 to "30 min before", 60 to "1 hour before", 1440 to "1 day before")
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            options.forEach { (mins, label) ->
                                FilterChip(
                                    selected = reminderMinutes == mins,
                                    onClick = { reminderMinutes = mins },
                                    label = { Text(label) }
                                )
                            }
                        }
                    }
                }
            }

            if (isEdit && existingVisit != null) {
                Card {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Status", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            VisitStatus.values().forEach { status ->
                                FilterChip(
                                    selected = existingVisit?.status == status,
                                    onClick = { viewModel.setVisitStatus(existingVisit!!, status) },
                                    label = { Text(status.name) }
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val sId = resolvedSiteId ?: return@Button
                    if (isEdit && existingVisit != null) {
                        viewModel.updateVisit(
                            existingVisit!!.copy(
                                title = title, notes = notes, visitDateTime = dateTimeMillis,
                                reminderEnabled = reminderEnabled, reminderMinutesBefore = reminderMinutes
                            ),
                            siteName
                        )
                    } else {
                        viewModel.addVisit(sId, siteName, title, dateTimeMillis, notes, reminderEnabled, reminderMinutes)
                    }
                    onDone()
                },
                enabled = title.isNotBlank() && resolvedSiteId != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEdit) "Save Changes" else "Schedule Visit")
            }
        }
    }

    if (showDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = dateTimeMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { newDate ->
                        val existingCal = Calendar.getInstance().apply { timeInMillis = dateTimeMillis }
                        val newCal = Calendar.getInstance().apply {
                            timeInMillis = newDate
                            set(Calendar.HOUR_OF_DAY, existingCal.get(Calendar.HOUR_OF_DAY))
                            set(Calendar.MINUTE, existingCal.get(Calendar.MINUTE))
                        }
                        dateTimeMillis = newCal.timeInMillis
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = state) }
    }

    if (showTimePicker) {
        val existingCal = Calendar.getInstance().apply { timeInMillis = dateTimeMillis }
        val state = rememberTimePickerState(
            initialHour = existingCal.get(Calendar.HOUR_OF_DAY),
            initialMinute = existingCal.get(Calendar.MINUTE)
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val newCal = Calendar.getInstance().apply {
                        timeInMillis = dateTimeMillis
                        set(Calendar.HOUR_OF_DAY, state.hour)
                        set(Calendar.MINUTE, state.minute)
                    }
                    dateTimeMillis = newCal.timeInMillis
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancel") } },
            text = { TimePicker(state = state) }
        )
    }
}
