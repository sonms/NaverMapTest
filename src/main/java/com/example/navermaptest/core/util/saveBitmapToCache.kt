package com.example.navermaptest.core.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun saveBitmapToCache(
    context: Context,
    bitmap: Bitmap,
    maxFiles: Int = 5, // 캐시 최대 개수
    maxCacheSizeBytes: Long = 50L * 1024 * 1024 // 50mb
): Result<Uri> {
    val cacheDir = context.cacheDir

    // 기존 스냅샷 파일 목록 오래된 순 정렬
    val existingFiles = cacheDir.listFiles { file ->
        file.name.startsWith("map_snapshot_") && file.extension == "png"
    }?.sortedBy { it.lastModified() } ?: emptyList()

    // 최대 개수 초과 시 오래된 파일 삭제
    if (existingFiles.size >= maxFiles) {
        val filesToDelete = existingFiles.take(existingFiles.size - maxFiles + 1)
        filesToDelete.forEach { it.delete() }
    }

    // 용량 기반 삭제
    var totalSize = existingFiles.sumOf { it.length() }
    val iterator = existingFiles.iterator()
    while (totalSize > maxCacheSizeBytes && iterator.hasNext()) {
        val file = iterator.next()
        totalSize -= file.length()
        file.delete()
    }

    val imageFile = File(cacheDir, "map_snapshot_${System.currentTimeMillis()}.png")
    return try {
        FileOutputStream(imageFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        Result.success(
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )
        )
    } catch (e: IOException) {
        Result.failure(e)
    }
}