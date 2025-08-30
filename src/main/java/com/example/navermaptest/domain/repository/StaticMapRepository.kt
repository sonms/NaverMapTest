package com.example.navermaptest.domain.repository

import com.example.navermaptest.domain.entity.MapImage
import com.naver.maps.geometry.LatLng

interface StaticMapRepository {
    suspend fun getStaticMapBitmap(pathCoords: List<LatLng>): Result<MapImage>
}