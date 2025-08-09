package com.nutrifacts.app

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nutrifacts.app.ui.navigation.NavigationItem
import com.nutrifacts.app.ui.navigation.Screen
import com.nutrifacts.app.ui.screen.account.AccountScreen
import com.nutrifacts.app.ui.screen.detail.DetailScreen
import com.nutrifacts.app.ui.screen.history.HistoryScreen
import com.nutrifacts.app.ui.screen.home.HomeScreen
import com.nutrifacts.app.ui.screen.landing.LandingScreen
import com.nutrifacts.app.ui.screen.login.LoginScreen
import com.nutrifacts.app.ui.screen.news.NewsScreen
import com.nutrifacts.app.ui.screen.notifications.NotificationsScreen
import com.nutrifacts.app.ui.screen.profile.ProfileScreen
import com.nutrifacts.app.ui.screen.saved.SavedScreen
import com.nutrifacts.app.ui.screen.scanner.ScannerActivity
import com.nutrifacts.app.ui.screen.search.SearchScreen
import com.nutrifacts.app.ui.screen.settings.SettingsScreen
import com.nutrifacts.app.ui.screen.signup.SignupScreen
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutrifactsApp(
    modifier: Modifier = Modifier,
    userIsLogin: Boolean,
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        topBar = {
            TopAppBar(navController)
        },
        floatingActionButton = {
            FAB(
                navController = navController
            )
        },
        bottomBar = {
            BottomAppBar(navController)
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (userIsLogin) Screen.Home.route else Screen.Landing.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Landing.route) {
                LandingScreen(navigateToLogin = {
                    navController.navigate(
                        Screen.Login.route
                    )
                })
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    navigateToSignup = { navController.navigate(Screen.Signup.route) },
                    navigateToHome = { navController.navigate(Screen.Home.route) })
            }
            composable(Screen.Signup.route) {
                SignupScreen(
                    navigateToLogin = { navController.navigate(Screen.Login.route) })
            }
            composable(Screen.Home.route) {
                HomeScreen(navigateToNews = { newsId ->
                    navController.navigate(
                        Screen.News.createRoute(newsId)
                    )
                })
            }
            composable(Screen.Search.route) {
                SearchScreen(navigateToDetail = { barcode ->
                    navController.navigate(
                        Screen.Detail.createRoute(barcode)
                    )
                })
            }
            composable(Screen.History.route) {
                HistoryScreen(navigateToDetail = { barcode ->
                    navController.navigate(
                        Screen.Detail.createRoute(barcode)
                    )
                })
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController = navController)
            }
            composable(Screen.Scanner.route) {
                ScannerActivity()
            }
            composable(
                route = Screen.News.route,
                arguments = listOf(navArgument("newsId") { type = NavType.IntType })
            ) {
                val newsId = it.arguments?.getInt("newsId") ?: -1L
                NewsScreen(newsId = newsId.toInt())
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("barcode") { type = NavType.StringType })
            ) {
                val barcode = it.arguments?.getString("barcode") ?: -1L
                DetailScreen(barcode = barcode.toString())
            }
            composable(Screen.Account.route) {
                AccountScreen()
            }
            composable(Screen.Saved.route) {
                SavedScreen(navigateToDetail = { product ->
                    navController.navigate(
                        Screen.Detail.createRoute(product)
                    )
                })
            }
            composable(Screen.Notifications.route) {
                NotificationsScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    if (currentRoute != Screen.Landing.route && currentRoute != Screen.Login.route && currentRoute != Screen.Signup.route && currentRoute != Screen.Scanner.route) {
        androidx.compose.material3.TopAppBar(
            title = {
                if (currentRoute == Screen.Detail.route) {
                    Text(
                        text = "Nutrifacts",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium
                    )
                } else if (currentRoute == Screen.News.route) {
                    Text(
                        text = "News",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    Text(
                        text = currentRoute.toString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            navigationIcon = {
                if (currentRoute != Screen.Home.route && currentRoute != Screen.Search.route && currentRoute != Screen.History.route) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(
                                R.string.menu_back
                            )
                        )
                    }
                }
            },
            actions = {
                if (currentRoute == Screen.Home.route || currentRoute == Screen.Search.route || currentRoute == Screen.History.route) {
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = stringResource(
                                R.string.menu_profile
                            )
                        )
                    }
                }
            },
            scrollBehavior = scrollBehavior,
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .shadow(1.dp)
        )
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
private fun FAB(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    if (currentRoute == Screen.Home.route || currentRoute == Screen.Search.route || currentRoute == Screen.History.route) {
        FloatingActionButton(
            onClick = {
                context.startActivity(Intent(context, ScannerActivity::class.java))
//                      navController.navigate(Screen.Scanner.route)
            },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_qr_code_scanner_24),
                contentDescription = stringResource(
                    id = R.string.scanner
                )
            )
        }
    }
}

@Composable
private fun BottomAppBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    if (currentRoute == Screen.Home.route || currentRoute == Screen.Search.route || currentRoute == Screen.History.route) {
        NavigationBar(
            modifier = modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.onSurface),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            val navigationItems = listOf(
                NavigationItem(
                    title = stringResource(R.string.menu_home),
                    icon = Icons.Default.Home,
                    screen = Screen.Home
                ),
                NavigationItem(
                    title = stringResource(R.string.menu_search),
                    icon = Icons.Default.Search,
                    screen = Screen.Search
                ),
                NavigationItem(
                    title = stringResource(R.string.menu_history),
                    icon = Icons.Default.List,
                    screen = Screen.History
                )
            )
            navigationItems.map { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    },
                    selected = currentRoute == item.screen.route,
                    onClick = {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}