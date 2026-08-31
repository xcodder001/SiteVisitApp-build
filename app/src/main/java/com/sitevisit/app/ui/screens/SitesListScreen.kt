package com.sitevisit.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sitevisit.app.data.entity.Site
import com.sitevisit.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SitesListScreen(
    viewModel: AppViewModel,
    onSiteClick: (Long) -> Unit,
    onAddSite: () -> Unit
) {
    val sites by viewModel.sites.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Sites") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddSite) {
                Icon(Icons.Filled.Add, contentDescription = "Add site")
            }
        }
    ) { padding ->
        if (sites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No sites yet. Tap + to add your first site.", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(sites, key = { it.id }) { site ->
                    SiteRow(site = site, onClick = { onSiteClick(site.id) })
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun SiteRow(site: Site, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.padding(end = 12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(site.name, style = MaterialTheme.typography.titleMedium)
            if (site.address.isNotBlank()) {
                Text(site.address, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
