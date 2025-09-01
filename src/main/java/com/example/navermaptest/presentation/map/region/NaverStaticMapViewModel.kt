package com.example.navermaptest.presentation.map.region

import android.location.Location
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navermaptest.domain.usecase.GetStaticMapBitmapUseCase
import com.example.navermaptest.presentation.map.util.RealTimeLocationListener
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
) : ViewModel(), RealTimeLocationListener {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    // 마지막으로 경로에 추가된 위치를 저장할 변수
    private var lastAddedLocation: Location? = null

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

        loadRouteLine(
            route = listOf(
                LatLng(37.4979, 127.0276), // 강남역 사거리 중심
                LatLng(37.4982, 127.0284), // 강남역 11번 출구 쪽
                LatLng(37.4988, 127.0292), // 테헤란로 방향
                LatLng(37.4995, 127.0298), // 역삼역 방향 직진
                LatLng(37.5001, 127.0304), // 테헤란로 초입
                LatLng(37.5008, 127.0311)  // 더 위쪽 (샘플 종점)
            )
        )

        loadArrowRouteLine(
            route = listOf(
                LatLng(37.4980, 127.0276), // 출발: 강남역 사거리
                LatLng(37.4985, 127.0282), // 강남역 11번 출구 방향
                LatLng(37.4990, 127.0288), // 카페 골목 진입
                LatLng(37.4993, 127.0295), // 작은 골목길
                LatLng(37.4989, 127.0300), // 오피스 건물 뒷길
                LatLng(37.4982, 127.0302), // 골목길 따라 이동
                LatLng(37.4977, 127.0297), // 다시 역삼역 방향
                LatLng(37.4975, 127.0289), // 큰길로 복귀
                LatLng(37.4979, 127.0279), // 강남역 인근 복귀
                LatLng(37.4980, 127.0276)  // 도착: 출발 지점으로 회귀
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

    fun loadRouteLine(
        route : List<LatLng>
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    routeLineList = route.toPersistentList()
                )
            }
        }
    }

    fun loadArrowRouteLine(
        route : List<LatLng>
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    arrowRouteLineList = route.toPersistentList()
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

    override fun onLocationChanged(location: Location) {
        val newLatLng = LatLng(location.latitude, location.longitude)

        // currentLocation은 GPS 신호가 올 때마다 항상 업데이트
        _uiState.update { it.copy(currentLocation = newLatLng) }

        // 마지막 위치가 없으면(최초 실행 시) 바로 추가
        if (lastAddedLocation == null) {
            addNewRoutePoint(newLatLng, location)
            return
        }

        // 마지막 위치로부터의 거리를 미터(m) 단위로 계산
        val distance = lastAddedLocation!!.distanceTo(location)

        // 거리가 3미터 이상일 경우에만 경로에 점을 추가
        if (distance >= 3.0f) {
            addNewRoutePoint(newLatLng, location)
        }
    }

    private fun addNewRoutePoint(latLng: LatLng, location: Location) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val updatedList = currentState.realTimeRouteLine.add(latLng)

                currentState.copy(
                    realTimeRouteLine = updatedList
                )
            }
            lastAddedLocation = location
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