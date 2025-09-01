package com.example.navermaptest.presentation.map.region

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.naver.maps.geometry.LatLng
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class MapUiState(
    val currentLocation: LatLng? = null,
    val regionRouteLineList : ImmutableList<LatLng> = persistentListOf(),
    val routeLineList : ImmutableList<LatLng> = persistentListOf(),
    val arrowRouteLineList : ImmutableList<LatLng> = persistentListOf(),
    val realTimeRouteLine : PersistentList<LatLng> = persistentListOf(),
    val isLoading: Boolean = false,
    val snapshotUri : Uri? = null,
    val error: String? = null,
    val mapUrl : String = ""
)