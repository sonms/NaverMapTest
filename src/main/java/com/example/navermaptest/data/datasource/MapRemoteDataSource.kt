package com.example.navermaptest.data.datasource

import com.example.navermaptest.BuildConfig
import com.example.navermaptest.data.service.StaticMapService
import javax.inject.Inject

class MapRemoteDataSource @Inject constructor(
    private val mapApiService: StaticMapService
) {
    suspend fun getStaticMapImage(width: Int, height: Int, center : String) =
        mapApiService.getStaticMapImage(
            clientId = BuildConfig.NAVERMAP_CLIENT_ID,
            clientSecret = BuildConfig.NAVERMAP_CLIENT_SECRET,
            width = width,
            height = height,
            level = 5,
            markers = null,
            center = center
        )
}