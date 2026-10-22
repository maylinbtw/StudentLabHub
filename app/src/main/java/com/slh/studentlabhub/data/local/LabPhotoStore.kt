package com.slh.studentlabhub.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class LabPhotoStore(context: Context) {

    private val appContext = context.applicationContext
    private val root = File(appContext.filesDir, "lab_photos")

    private fun dir(labId: Long): File = File(root, labId.toString())

    suspend fun list(labId: Long): List<String> = withContext(Dispatchers.IO) {
        dir(labId).listFiles()
            ?.filter { it.isFile && it.extension == "jpg" }
            ?.sortedBy { it.lastModified() }
            ?.map { it.absolutePath }
            .orEmpty()
    }

    suspend fun add(labId: Long, uri: Uri): String? = withContext(Dispatchers.IO) {
        val bitmap = decodeScaled(uri, MAX_SIDE) ?: return@withContext null
        val target = File(dir(labId).apply { mkdirs() }, "${System.currentTimeMillis()}_${UUID.randomUUID()}.jpg")
        target.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, it) }
        bitmap.recycle()
        target.absolutePath
    }

    suspend fun delete(path: String) = withContext(Dispatchers.IO) {
        val file = File(path)
        if (file.canonicalPath.startsWith(root.canonicalPath)) {
            file.delete()
        }
    }

    suspend fun deleteAll(labId: Long) = withContext(Dispatchers.IO) {
        dir(labId).deleteRecursively()
    }

    private fun decodeScaled(uri: Uri, maxSide: Int): Bitmap? {
        val resolver = appContext.contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) } ?: return null
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        var sample = 1
        while (bounds.outWidth / (sample * 2) >= maxSide || bounds.outHeight / (sample * 2) >= maxSide) {
            sample *= 2
        }
        val options = BitmapFactory.Options().apply { inSampleSize = sample }
        return resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
    }

    private companion object {
        const val MAX_SIDE = 2048
        const val JPEG_QUALITY = 85
    }
}

fun decodeSampledFile(path: String, maxSide: Int): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(path, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
    var sample = 1
    while (bounds.outWidth / (sample * 2) >= maxSide || bounds.outHeight / (sample * 2) >= maxSide) {
        sample *= 2
    }
    return BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = sample })
}
