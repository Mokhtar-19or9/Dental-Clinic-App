package com.example.dentalclinic.data

import android.content.Context
import com.example.dentalclinic.data.api.RayResponse
import kotlin.math.absoluteValue

object LocalRayProvider {
    private const val ASSET_DIR = "rays"
    private const val ASSET_PREFIX = "file:///android_asset/"

    fun getRandomLocalImage(context: Context, seed: Int = 0): String? {
        val files = listOf(
            "local_ray_5.jpg",
            "local_ray_6.jpg",
            "local_ray_7.jpg",
            "local_ray_8.jpg",
            "photo_2024-03-21_20-50-31.jpg",
            "photo_2024-03-21_20-50-32.jpg",
            "photo_2024-03-21_20-50-34.jpg"
        )
        val index = seed.absoluteValue % files.size
        return "$ASSET_PREFIX$ASSET_DIR/${files[index]}"
    }

    fun getFallbackRays(context: Context, patientId: String?): List<RayResponse> {
        val files = listOf(
            "local_ray_5.jpg",
            "local_ray_6.jpg",
            "local_ray_7.jpg",
            "local_ray_8.jpg",
            "photo_2024-03-21_20-50-31.jpg",
            "photo_2024-03-21_20-50-32.jpg",
            "photo_2024-03-21_20-50-34.jpg"
        )
        val seed = (patientId?.hashCode() ?: 0).absoluteValue
        val isAr = AppSettings.currentLanguage == "ar"
        
        return files.mapIndexed { index, name ->
            val day = (10 + index) % 28 + 1
            val month = (index % 12) + 1
            val dateStr = "2024-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}T10:00:00"
            
            RayResponse(
                id = "local_${seed}_$index",
                name = if (isAr) "صورة أشعة ${index + 1}" else "X-Ray Scan ${index + 1}",
                description = if (isAr) "فحص روتيني للأسنان والفك" else "Routine dental and jaw examination",
                image = "$ASSET_PREFIX$ASSET_DIR/$name",
                patientId = patientId,
                createdAt = dateStr,
                rayType = 0,
                aiAnalysisJson = null,
                totalDetections = 0
            )
        }
    }
}
