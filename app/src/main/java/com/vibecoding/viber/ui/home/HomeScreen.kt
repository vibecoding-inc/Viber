package com.vibecoding.viber.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vibecoding.viber.ui.components.CatModeOverlay
import com.vibecoding.viber.ui.components.ConfettiAnimation
import com.vibecoding.viber.ui.copilot.CopilotScreen
import com.vibecoding.viber.ui.issues.IssuesScreen
import com.vibecoding.viber.ui.profile.ProfileScreen
import com.vibecoding.viber.ui.pullrequests.PullRequestsScreen
import com.vibecoding.viber.ui.repositories.RepositoriesScreen

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Repositories : Screen("repositories", "Repos", Icons.Default.Folder)
    object Issues : Screen("issues", "Issues", Icons.Default.BugReport)
    object PullRequests : Screen("pullrequests", "PRs", Icons.Default.MergeType)
    object Copilot : Screen("copilot", "Copilot", Icons.Default.AutoAwesome)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val vibeMode by viewModel.vibeMode.collectAsState()
    val catMode by viewModel.catMode.collectAsState()
    val showCelebration by viewModel.showCelebration.collectAsState()
    
    val items = listOf(
        Screen.Repositories,
        Screen.Issues,
        Screen.PullRequests,
        Screen.Copilot,
        Screen.Profile
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row {
                        if (catMode) {
                            Text("🐱 Viber")
                        } else if (vibeMode) {
                            Text("✨ Viber")
                        } else {
                            Text("Viber")
                        }
                    }
                },
                actions = {
                    if (vibeMode || catMode) {
                        Icon(
                            imageVector = if (catMode) Icons.Default.Pets else Icons.Default.AutoAwesome,
                            contentDescription = if (catMode) "Cat Mode" else "Vibe Mode",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Repositories.route
            ) {
                composable(Screen.Repositories.route) {
                    RepositoriesScreen(onSuccess = { viewModel.triggerCelebration() })
                }
                composable(Screen.Issues.route) {
                    IssuesScreen(onSuccess = { viewModel.triggerCelebration() })
                }
                composable(Screen.PullRequests.route) {
                    PullRequestsScreen(onSuccess = { viewModel.triggerCelebration() })
                }
                composable(Screen.Copilot.route) {
                    CopilotScreen(onSuccess = { viewModel.triggerCelebration() })
                }
                composable(Screen.Profile.route) {
                    ProfileScreen()
                }
            }

            // Cat mode overlay
            if (catMode && showCelebration) {
                CatModeOverlay(isActive = true)
            }

            // Confetti for celebrations
            if (showCelebration) {
                ConfettiAnimation(isPlaying = true)
            }
        }
    }
}
