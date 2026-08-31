package com.sitevisit.app.ui.screens

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sitevisit.app.viewmodel.AppViewModel
import java.io.File

private fun createImageFile(context: Context): File {
    val dir = File(context.getExternalFilesDir(null), "site_photos").apply { mkdirs() }
    val fileName = "SITE_${System.currentTimeMillis()}.jpg"
    return File(dir, fileName)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGalleryScreen(
    viewModel: AppViewModel,
    siteId: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val photos by remember(siteId) { viewModel.photosForSite(siteId) }.collectAsStateWithLifecycle(initialValue = emptyList())

    var pendingUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingUri != null) {
            viewModel.addPhoto(siteId, null, pendingUri.toString(), "")
        }
        pendingUri = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Site Photos") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val file = createImageFile(context)
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                pendingUri = uri
                cameraLauncher.launch(uri)
            }) {
                Icon(Icons.Filled.CameraAlt, contentDescription = "Take photo")
            }
        }
    ) { padding ->
        if (photos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No photos yet. Tap the camera button to add one.")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(photos, key = { it.id }) { photo ->
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .aspectRatio(1f)
                    ) {
                        AsyncImage(
                            model = photo.uri,
                            contentDescription = photo.caption,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { viewModel.deletePhoto(photo) },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete photo", tint = androidx.compose.ui.graphics.Color.White)
                        }
                    }
                }
            }
        }
    }
}
