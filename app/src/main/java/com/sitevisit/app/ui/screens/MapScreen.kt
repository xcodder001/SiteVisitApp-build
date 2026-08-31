package com.sitevisit.app.ui.screens

import android.graphics.Color as AndroidColor
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.sitevisit.app.viewmodel.AppViewModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: AppViewModel,
    onSiteClick: (Long) -> Unit
) {
    val sites by viewModel.sites.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("Site Map") }) }
    ) { padding ->
        AndroidView(
            modifier = Modifier.fillMaxSize().padding(padding),
            factory = { ctx ->
                MapView(ctx).apply {
                    setMultiTouchControls(true)
                    controller.setZoom(4.0)
                    controller.setCenter(GeoPoint(20.0, 0.0))
                }
            },
            update = { mapView ->
                mapView.overlays.clear()
                val validSites = sites.filter { it.latitude != 0.0 || it.longitude != 0.0 }
                validSites.forEach { site ->
                    val marker = Marker(mapView)
                    marker.position = GeoPoint(site.latitude, site.longitude)
                    marker.title = site.name
                    marker.snippet = site.address
                    marker.setOnMarkerClickListener { m, _ ->
                        onSiteClick(site.id)
                        m.showInfoWindow()
                        true
                    }
                    mapView.overlays.add(marker)
                }
                if (validSites.isNotEmpty()) {
                    val first = validSites.first()
                    mapView.controller.setCenter(GeoPoint(first.latitude, first.longitude))
                    mapView.controller.setZoom(if (validSites.size == 1) 14.0 else 5.0)
                } else if (sites.isNotEmpty()) {
                    Toast.makeText(context, "Sites need a GPS location set to appear on the map", Toast.LENGTH_SHORT).show()
                }
                mapView.invalidate()
            }
        )
    }
}
