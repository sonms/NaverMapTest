package com.example.navermaptest.presentation.main

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import com.example.navermaptest.presentation.home.homeGraph
import com.example.navermaptest.presentation.main.component.MainBottomBar
import com.example.navermaptest.presentation.map.region.navigation.regionGraph
import kotlinx.collections.immutable.toPersistentList

private const val EXIT_MILLIS = 3000L

@Composable
fun MainScreen(
    navigator: MainNavigator = rememberMainNavigator(),
) {
    val context = LocalContext.current
    var backPressedTime by remember { mutableLongStateOf(0L) }

    val exitToast = remember {
        Toast.makeText(context, "한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT)
    }

    if (navigator.currentTab != null) {
        BackHandler {
            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressedTime <= EXIT_MILLIS) {
                (context as? Activity)?.finish()
            } else {
                backPressedTime = currentTime
                exitToast.show()
            }
        }
    }

    Scaffold (
        bottomBar = {
            MainBottomBar(
                isVisible = navigator.showBottomBar(),
                tabs = MainTab.entries.toPersistentList(),
                currentTab = navigator.currentTab,
                onTabSelected = navigator::navigate,
            )
        },
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
    ) { innerPadding ->
        NavHost(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
            navController = navigator.navController,
            startDestination = navigator.startDestination,
        ) {
            homeGraph(
                paddingValues = innerPadding,
            )

            regionGraph(
                paddingValues = innerPadding,
            )

        }
    }
}