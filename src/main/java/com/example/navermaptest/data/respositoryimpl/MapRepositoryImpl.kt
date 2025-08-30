package com.example.navermaptest.data.respositoryimpl

import android.graphics.BitmapFactory
import com.example.navermaptest.data.datasource.MapRemoteDataSource
import com.example.navermaptest.domain.entity.MapImage
import com.example.navermaptest.domain.repository.StaticMapRepository
import com.naver.maps.geometry.LatLng
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(
    private val remoteDataSource: MapRemoteDataSource
) : StaticMapRepository {
    override suspend fun getStaticMapBitmap(pathCoords: List<LatLng>): Result<MapImage> {
        if (pathCoords.size < 2) {
            return Result.failure(IllegalArgumentException("경로 좌표는 최소 2개 이상 필요합니다."))
        }

        return try {
            val response = remoteDataSource.getStaticMapImage(800, 600, center = "${pathCoords.first().longitude},${pathCoords.first().latitude}")
            if (response.isSuccessful && response.body() != null) {
                val inputStream = response.body()!!.byteStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)
                Result.success(MapImage(bitmap = bitmap))
            } else {
                Result.failure(Exception("네트워크 에러: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}