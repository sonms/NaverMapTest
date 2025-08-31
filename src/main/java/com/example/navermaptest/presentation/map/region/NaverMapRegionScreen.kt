package com.example.navermaptest.presentation.map.region

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.Gravity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.navermaptest.core.util.saveBitmapToCache
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.compose.ArrowheadPathOverlay
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.DisposableMapEffect
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.PathOverlay
import com.naver.maps.map.compose.PolygonOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun NaverMapRegionRoute(
    paddingValues: PaddingValues,
    viewModel: NaverStaticMapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraPositionState = rememberCameraPositionState()
    val context = LocalContext.current

    LaunchedEffect(uiState.regionRouteLineList) {
        val bounds = LatLngBounds.from(uiState.regionRouteLineList)
        cameraPositionState.move(
            CameraUpdate.fitBounds(bounds, 150)
        )
    }

    when {
        uiState.isLoading -> CircularProgressIndicator()
        uiState.error != null -> Text(text = uiState.error.toString())
        else -> NaverMapRegionScreen(
            paddingValues = paddingValues,
            cameraPositionState = cameraPositionState,
            context = context,
            routeCoords = uiState.regionRouteLineList,
            arrowRouteLineCoords = uiState.arrowRouteLineList,
            routeLineCoords = uiState.routeLineList,
            snapshotUri = uiState.snapshotUri,
            onSnapshotTaken = { uri ->
                viewModel.snapshotUri(uri)
            }
        )
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun NaverMapRegionScreen(
    paddingValues: PaddingValues,
    cameraPositionState: CameraPositionState,
    context: Context,
    snapshotUri: Uri?,
    routeCoords: ImmutableList<LatLng>,
    routeLineCoords: ImmutableList<LatLng>,
    arrowRouteLineCoords : ImmutableList<LatLng>,
    onSnapshotTaken: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    var rawNaverMap by remember { mutableStateOf<NaverMap?>(null) }

    // 안에 한글 주석처리들 잘 되어있으니 확인하면서 하기
    var mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                isLocationButtonEnabled = true,
                isZoomControlEnabled = true,
                logoGravity = Gravity.TOP or Gravity.END,
            )
        )
    }

    var mapProperties by remember {
        mutableStateOf(
            MapProperties(
                maxZoom = 20.0,
                minZoom = 5.0,
                isBicycleLayerGroupEnabled = true, // 자전거 도로 그룹
                isCadastralLayerGroupEnabled = true, // 지적편집도 레이어 그룹
                isIndoorEnabled = true // 실내 지형
            )
        )
    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            NaverMap (
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f),
                cameraPositionState = cameraPositionState,
                uiSettings = mapUiSettings
            ) {
                /*
                * @Composable
                @NaverMapComposable
                @ExperimentalNaverMapApi
                public fun DisposableMapEffect(
                    key1: Any?,
                    effect: DisposableEffectScope.(NaverMap) -> DisposableEffectResult,
                ) {
                    val map = (currentComposer.applier as MapApplier).map
                    DisposableEffect(key1 = key1) {
                        effect(map)
                    }
                }
                * 구현 방식이 currentComposer.applier as MapApplier을 cast하여 사용하고 있기 때문에
                * route에서 사용할 시 UiApplier cannot be cast to com.naver.maps.map.compose.MapApplier 오류 발생하니 주의
                * */
                DisposableMapEffect { naverMap ->
                    rawNaverMap = naverMap

                    onDispose {
                        rawNaverMap = null
                    }
                }

                /*PathOverlay(
                    coords = routeCoords,
                    width = 5.dp,         // 선의 두께
                    color = Color.Green,   // 선의 색상
                    outlineWidth = 1.dp,  // 선의 테두리 두께
                    outlineColor = Color.White // 선의 테두리 색상
                )*/

                // 내부 영역을 색칠하는 PolygonOverlay
                PolygonOverlay(
                    coords = routeCoords,
                    color = Color.Blue.copy(alpha = 0.3f), // 채우기 색상 (반투명 파랑)
                    outlineWidth = 1.dp, // 폴리곤의 외곽선은 경로가 이미 있으므로 0으로 설정
                    outlineColor = Color.Blue // 폴리곤의 외곽선 색상
                )

                // 화살표 그려진 루트 라인
                ArrowheadPathOverlay(
                    coords = arrowRouteLineCoords,
                    color = Color.Green
                )

                // 일반 루트 라인
                PathOverlay(
                    coords = routeLineCoords,
                    width = 5.dp,         // 선의 두께
                    color = Color.Green,   // 선의 색상
                )
            }

            Button(
                onClick = {
                    rawNaverMap?.takeSnapshot { bitmap ->
                        val result = saveBitmapToCache(context, bitmap)
                        result.onSuccess { uri ->
                            onSnapshotTaken(uri)
                        }.onFailure { exception ->
                            Log.e("MapSnapshot", "Failed to save snapshot", exception)
                            // Toast.makeText(context, "스냅샷 저장 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("지도 스냅샷 찍기")
            }
        }


        snapshotUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Map Snapshot",
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}