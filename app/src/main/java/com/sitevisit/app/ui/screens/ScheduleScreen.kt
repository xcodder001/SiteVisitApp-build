package com.sitevisit.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sitevisit.app.data.entity.VisitStatus
import com.sitevisit.app.util.Formatters
import com.sitevisit.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: AppViewModel,
    onVisitClick: (Long) -> Unit
) {
    val visits by viewModel.upcomingVisits.collectAsState()
    val sites by viewModel.sites.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Schedule") }) }
    ) { padding ->
        val activeVisits = visits.filter { it.status == VisitStatus.SCHEDULED }
        if (activeVisits.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No upcoming visits scheduled.")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(activeVisits, key = { it.id }) { visit ->
                    val siteName = sites.find { it.id == visit.siteId }?.name ?: "Unknown site"
                    ListItem(
                        headlineContent = { Text(visit.title) },
                        supportingContent = { Text("$siteName • ${Formatters.dateTime(visit.visitDateTime)}") },
                        trailingContent = {
                            if (visit.reminderEnabled) Text("🔔", style = MaterialTheme.typography.titleMedium)
                        },
                        modifier = Modifier.clickable { onVisitClick(visit.id) }
                    )
                    Divider()
                }
            }
        }
    }
}
