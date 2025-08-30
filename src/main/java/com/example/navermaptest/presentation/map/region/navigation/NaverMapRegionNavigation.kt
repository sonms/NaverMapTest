package com.example.navermaptest.presentation.map.region.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.navermaptest.core.navigation.MainTabRoute
import com.example.navermaptest.presentation.map.region.NaverMapRegionRoute
import kotlinx.serialization.Serializable

fun NavController.navigateRegion(
    navOptions: NavOptions?
) {
    navigate(Region, navOptions)
}

fun NavGraphBuilder.regionGraph(
    paddingValues: PaddingValues,
) {
    composable<Region> {
        NaverMapRegionRoute(
            paddingValues = paddingValues,
        )
    }
}


@Serializable
data object Region : MainTabRoute