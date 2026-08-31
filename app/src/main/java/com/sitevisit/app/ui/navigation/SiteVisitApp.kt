package com.sitevisit.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.sitevisit.app.ui.screens.*
import com.sitevisit.app.viewmodel.AppViewModel

private data class BottomTab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val bottomTabs = listOf(
    BottomTab(Dest.SITES, "Sites", Icons.Filled.Place),
    BottomTab(Dest.MAP, "Map", Icons.Filled.Map),
    BottomTab(Dest.SCHEDULE, "Schedule", Icons.Filled.CalendarMonth)
)

@Composable
fun SiteVisitApp() {
    val navController = rememberNavController()
    val viewModel: AppViewModel = viewModel()

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination
            // Only show the bottom bar on the three top-level tabs
            if (bottomTabs.any { it.route == currentRoute?.route }) {
                NavigationBar {
                    bottomTabs.forEach { tab ->
                        val selected = currentRoute?.hierarchy?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Dest.SITES,
            modifier = Modifier.padding(padding)
        ) {
            composable(Dest.SITES) {
                SitesListScreen(
                    viewModel = viewModel,
                    onSiteClick = { navController.navigate(Dest.siteDetail(it)) },
                    onAddSite = { navController.navigate(Dest.SITE_ADD) }
                )
            }
            composable(Dest.MAP) {
                MapScreen(
                    viewModel = viewModel,
                    onSiteClick = { navController.navigate(Dest.siteDetail(it)) }
                )
            }
            composable(Dest.SCHEDULE) {
                ScheduleScreen(
                    viewModel = viewModel,
                    onVisitClick = { navController.navigate(Dest.visitEdit(it)) }
                )
            }
            composable(Dest.SITE_ADD) {
                AddEditSiteScreen(viewModel = viewModel, siteId = null, onDone = { navController.popBackStack() })
            }
            composable(
                Dest.SITE_EDIT,
                arguments = listOf(navArgument("siteId") { type = NavType.LongType })
            ) { entry ->
                val siteId = entry.arguments?.getLong("siteId") ?: return@composable
                AddEditSiteScreen(viewModel = viewModel, siteId = siteId, onDone = { navController.popBackStack() })
            }
            composable(
                Dest.SITE_DETAIL,
                arguments = listOf(navArgument("siteId") { type = NavType.LongType })
            ) { entry ->
                val siteId = entry.arguments?.getLong("siteId") ?: return@composable
                SiteDetailScreen(
                    viewModel = viewModel,
                    siteId = siteId,
                    onBack = { navController.popBackStack() },
                    onEditSite = { navController.navigate(Dest.siteEdit(siteId)) },
                    onAddVisit = { navController.navigate(Dest.visitAdd(siteId)) },
                    onVisitClick = { navController.navigate(Dest.visitEdit(it)) },
                    onAddQuotation = { navController.navigate(Dest.quotationAdd(siteId)) },
                    onQuotationClick = { navController.navigate(Dest.quotationDetail(it)) },
                    onAddPayment = { navController.navigate(Dest.paymentAdd(siteId)) },
                    onOpenPhotos = { navController.navigate(Dest.photoGallery(siteId)) }
                )
            }
            composable(
                Dest.VISIT_ADD,
                arguments = listOf(navArgument("siteId") { type = NavType.LongType })
            ) { entry ->
                val siteId = entry.arguments?.getLong("siteId") ?: return@composable
                AddEditVisitScreen(viewModel = viewModel, siteId = siteId, visitId = null, onDone = { navController.popBackStack() })
            }
            composable(
                Dest.VISIT_EDIT,
                arguments = listOf(navArgument("visitId") { type = NavType.LongType })
            ) { entry ->
                val visitId = entry.arguments?.getLong("visitId") ?: return@composable
                AddEditVisitScreen(viewModel = viewModel, siteId = null, visitId = visitId, onDone = { navController.popBackStack() })
            }
            composable(
                Dest.QUOTATION_ADD,
                arguments = listOf(navArgument("siteId") { type = NavType.LongType })
            ) { entry ->
                val siteId = entry.arguments?.getLong("siteId") ?: return@composable
                AddQuotationScreen(
                    viewModel = viewModel,
                    siteId = siteId,
                    onBack = { navController.popBackStack() },
                    onDone = { qId -> navController.navigate(Dest.quotationDetail(qId)) { popUpTo(Dest.SITES) } }
                )
            }
            composable(
                Dest.QUOTATION_DETAIL,
                arguments = listOf(navArgument("quotationId") { type = NavType.LongType })
            ) { entry ->
                val quotationId = entry.arguments?.getLong("quotationId") ?: return@composable
                QuotationDetailScreen(
                    viewModel = viewModel,
                    quotationId = quotationId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                Dest.PAYMENT_ADD,
                arguments = listOf(navArgument("siteId") { type = NavType.LongType })
            ) { entry ->
                val siteId = entry.arguments?.getLong("siteId") ?: return@composable
                AddPaymentScreen(viewModel = viewModel, siteId = siteId, onDone = { navController.popBackStack() })
            }
            composable(
                Dest.PHOTO_GALLERY,
                arguments = listOf(navArgument("siteId") { type = NavType.LongType })
            ) { entry ->
                val siteId = entry.arguments?.getLong("siteId") ?: return@composable
                PhotoGalleryScreen(viewModel = viewModel, siteId = siteId, onBack = { navController.popBackStack() })
            }
        }
    }
}
