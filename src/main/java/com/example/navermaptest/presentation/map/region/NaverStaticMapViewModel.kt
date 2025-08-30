package com.example.navermaptest.presentation.map.region

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navermaptest.domain.usecase.GetStaticMapBitmapUseCase
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NaverStaticMapViewModel @Inject constructor(
    private val getStaticMapBitmapUseCase: GetStaticMapBitmapUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadRegionRoute(
            route = listOf(
                LatLng(37.4979, 127.0276),
                LatLng(37.5005, 127.0350),
                LatLng(37.5035, 127.0435),
                LatLng(37.5043, 127.0496),
                LatLng(37.4985, 127.0498),
                LatLng(37.4952, 127.0450),
                LatLng(37.4938, 127.0365),
                LatLng(37.4950, 127.0295),
                LatLng(37.4979, 127.0276)
            )
        )
    }
    /*init {
        loadStaticMap(
            pathCoords = listOf(
                LatLng(37.4979, 127.0276),
                LatLng(37.5005, 127.0350),
                LatLng(37.5035, 127.0435),
                LatLng(37.5043, 127.0496),
                LatLng(37.4985, 127.0498),
                LatLng(37.4952, 127.0450),
                LatLng(37.4938, 127.0365),
                LatLng(37.4950, 127.0295),
                LatLng(37.4979, 127.0276)
            )
        )
    }*/

    /*fun loadStaticMap(pathCoords: List<LatLng>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getStaticMapBitmapUseCase(pathCoords)
                .onSuccess { mapImage ->
                    _uiState.update {
                        it.copy(isLoading = false, mapBitmap = mapImage.bitmap)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, error = throwable.message)
                    }
                }
        }
    }
*/
    fun loadRegionRoute(
        route : List<LatLng>
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    regionRouteLineList = route.toPersistentList()
                )
            }
        }
    }

    fun snapshotUri(uri: Uri) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    snapshotUri = uri
                )
            }
        }
    }

    /*fun generateMapUrl(coordinates: List<LatLng>) {
        val coordsString = coordinates.joinToString("|") { "${it.longitude},${it.latitude}" }
        val url = "https://naveropenapi.apigw.ntruss.com/map-static/v2/raster" +
                "?w=800&h=600" +
                "&path=color:0x0000ff|weight:5|coords:$coordsString"
        _uiState.update {
            it.copy(
                mapUrl = url
            )
        }
    }*/
}