package jsy.sample.testwalkietalkie.data

import android.graphics.Bitmap
import java.io.File

data class MediaFile(
    val file: File,
    val thumbnail : Bitmap
)