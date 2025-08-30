package com.example.navermaptest.data.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface StaticMapService {
    @GET("raster")
    suspend fun getStaticMapImage(
        @Header("x-ncp-apigw-api-key-id") clientId: String,
        @Header("x-ncp-apigw-api-key") clientSecret: String,
        @Query("crs") crs: String? = null, // 좌표 체계 (예: EPSG:4326, NHN:2048 등) [3, 4]
        @Query("w") width: Int, // 가로 이미지 크기 (1~1,024 픽셀) [1, 2]
        @Query("h") height: Int, // 세로 이미지 크기 (1~1,024 픽셀) [1, 2]
        @Query("center") center: String?, // 지도 이미지의 중심 좌표 (markers 미입력 시 필수, level 입력 시 필수) [1, 2]
        @Query("level") level: Int?, // 줌 레벨 (0~20) (markers 미입력 시 필수, center 입력 시 필수) [1, 2]
        @Query("markers") markers: String?, // 마커 설정 (center, level 미입력 시 필수) [3, 4]
        @Query("lang") lang: String? = "ko", // 라벨 언어 (ko, en, ja, zh) [5, 6]
        @Query("format") format: String? = "jpg", // 지도 이미지 형식 (jpg/jpeg, png8, png) [3, 4]
        @Query("scale") scale: Int? = null, // 해상도 (1: 저해상도, 2: 고해상도) [3, 4]
        @Query("dataversion") dataversion: String? = null, // 버전 정보 (이미지 캐싱 문제 해결용) [5, 6]
    ): Response<ResponseBody>
}