package com.example.navermaptest.domain.usecase

import com.example.navermaptest.domain.repository.StaticMapRepository
import com.naver.maps.geometry.LatLng
import javax.inject.Inject

class GetStaticMapBitmapUseCase @Inject constructor(
    private val mapRepository: StaticMapRepository
) {
    suspend operator fun invoke(pathCoords: List<LatLng>) =
        mapRepository.getStaticMapBitmap(pathCoords)
}