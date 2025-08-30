package com.example.navermaptest.presentation.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.navermaptest.core.navigation.MainTabRoute
import kotlinx.serialization.Serializable

fun NavController.navigateHome(
    navOptions: NavOptions?
) {
    navigate(Home, navOptions)
}

fun NavGraphBuilder.homeGraph(
    paddingValues: PaddingValues,
) {
    composable<Home> {
        HomeRoute(
            paddingValues = paddingValues,
        )
    }
}

@Serializable
data object Home : MainTabRoute