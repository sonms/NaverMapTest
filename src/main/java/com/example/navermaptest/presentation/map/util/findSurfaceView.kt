// core/util/SnapshotUtil.kt

package com.example.navermaptest.presentation.map.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.opengl.GLException
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.opengles.GL10
import kotlin.math.min

/**
 * MapView에서 GLSurfaceView를 찾는 유틸리티 함수
 */
fun findSurfaceView(view: View): GLSurfaceView? {
    if (view is GLSurfaceView) {
        return view
    }
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val child = view.getChildAt(i)
            val found = findSurfaceView(child)
            if (found != null) {
                return found
            }
        }
    }
    return null
}

/**
 * 스냅샷을 찍고 원하는 비율로 잘라내는 메인 함수
 */
fun captureAndCropMap(
    surfaceView: GLSurfaceView,
    aspectRatio: Float,
    onCaptured: (Bitmap?) -> Unit
) {
    val captureFn: ((Bitmap?) -> Unit) -> Unit = { callback ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            captureUsingPixelCopy(surfaceView, callback)
        } else {
            captureUsingGlReadPixels(surfaceView, callback)
        }
    }

    captureFn { rawBitmap ->
        rawBitmap?.let {
            val croppedBitmap = cropCenterWithAspectRatio(it, aspectRatio)
            onCaptured(croppedBitmap)
        } ?: onCaptured(null)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun captureUsingPixelCopy(
    surfaceView: GLSurfaceView,
    onCaptured: (Bitmap?) -> Unit
) {
    val bitmap = Bitmap.createBitmap(surfaceView.width, surfaceView.height, Bitmap.Config.ARGB_8888)
    try {
        PixelCopy.request(surfaceView, bitmap, { copyResult ->
            if (copyResult == PixelCopy.SUCCESS) {
                onCaptured(bitmap)
            } else {
                Log.e("PixelCopy", "PixelCopy failed: $copyResult")
                onCaptured(null)
            }
        }, Handler(Looper.getMainLooper()))
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        onCaptured(null)
    }
}

private fun captureUsingGlReadPixels(
    surfaceView: GLSurfaceView,
    onCaptured: (Bitmap?) -> Unit
) {
    surfaceView.queueEvent {
        val egl = EGLContext.getEGL() as EGL10
        val gl = egl.eglGetCurrentContext().gl as GL10
        val bitmap = createBitmapFromGLSurface(0, 0, surfaceView.width, surfaceView.height, gl)
        onCaptured(bitmap)
    }
}

fun createBitmapFromGLSurface(x: Int, y: Int, w: Int, h: Int, gl: GL10): Bitmap? {
    val bitmapBuffer = IntArray(w * h)
    val bitmapSource = IntArray(w * h)
    val intBuffer = IntBuffer.wrap(bitmapBuffer)
    intBuffer.position(0)

    try {
        gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer)
        var offset1: Int
        var offset2: Int

        for (i in 0 until h) {
            offset1 = i * w
            offset2 = (h - i - 1) * w

            for (j in 0 until w) {
                val texturePixel = bitmapBuffer[offset1 + j]
                val blue = (texturePixel shr 16) and 0xff
                val red = (texturePixel shl 16) and 0x00ff0000
                val pixel = (texturePixel and 0xff00ff00.toInt()) or red or blue
                bitmapSource[offset2 + j] = pixel
            }
        }
    } catch (e: GLException) {
        return null
    } catch (e: OutOfMemoryError) {
        return null
    }

    // 전체 비트맵 생성
    val fullBitmap = Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888)

    val targetAspectRatio = 16f / 11f

    var cropWidth: Int
    var cropHeight: Int

    val currentAspectRatio = w.toFloat() / h.toFloat()

    if (currentAspectRatio > targetAspectRatio) {
        cropHeight = h
        cropWidth = (h * targetAspectRatio).toInt()
    } else {
        cropWidth = w
        cropHeight = (w / targetAspectRatio).toInt()
    }

    val startX = ((w - cropWidth) / 2).coerceAtLeast(0)
    val startY = ((h - cropHeight) / 2).coerceAtLeast(0)

    val safeWidth = minOf(cropWidth, w - startX)
    val safeHeight = minOf(cropHeight, h - startY)

    // 잘라낸 비트맵 반환
    return Bitmap.createBitmap(fullBitmap, startX, startY, safeWidth, safeHeight)
}

/**
 * Bitmap을 원하는 비율로 중앙을 기준으로 잘라냅니다.
 */
private fun cropCenterWithAspectRatio(bitmap: Bitmap, targetAspectRatio: Float): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val currentAspectRatio = width.toFloat() / height.toFloat()

    val cropWidth: Int
    val cropHeight: Int

    if (currentAspectRatio > targetAspectRatio) {
        cropHeight = height
        cropWidth = (height * targetAspectRatio).toInt()
    } else {
        cropWidth = width
        cropHeight = (width / targetAspectRatio).toInt()
    }

    val startX = (width - cropWidth) / 2
    val startY = (height - cropHeight) / 2
    
    return Bitmap.createBitmap(bitmap, startX, startY, cropWidth, cropHeight)
}