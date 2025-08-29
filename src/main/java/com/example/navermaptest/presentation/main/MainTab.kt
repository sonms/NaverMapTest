package com.example.navermaptest.presentation.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.example.navermaptest.core.navigation.MainTabRoute
import com.example.navermaptest.core.navigation.Route
import com.example.navermaptest.R
import com.example.navermaptest.presentation.home.Home
import com.example.navermaptest.presentation.map.region.navigation.Region
import kotlin.collections.any
import kotlin.collections.find
import kotlin.collections.map

enum class MainTab(
    @param:DrawableRes val selectedIcon: Int,
    @param:DrawableRes val unselectedIcon: Int,
    @param:StringRes val contentDescription: Int,
    val route: MainTabRoute,
) {
    HOME(
        selectedIcon = R.drawable.ic_home_fill,
        unselectedIcon = R.drawable.ic_home_linear,
        contentDescription = R.string.ic_home_description,
        route = Home,
    ),

    REGION(
        selectedIcon = R.drawable.ic_location_on_24,
        unselectedIcon = R.drawable.ic_outline_location_on_24,
        contentDescription = R.string.ic_region_description,
        route = Region,
    );

    companion object {
        @Composable
        fun find(predicate: @Composable (MainTabRoute) -> Boolean): MainTab? {
            return entries.find { predicate(it.route) }
        }

        @Composable
        fun contains(predicate: @Composable (Route) -> Boolean): Boolean {
            return entries.map { it.route }.any { predicate(it) }
        }
    }
}