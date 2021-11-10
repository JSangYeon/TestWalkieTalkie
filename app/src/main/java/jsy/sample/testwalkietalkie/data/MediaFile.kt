package jsy.sample.testwalkietalkie.data

import android.graphics.Bitmap
import java.io.File
import java.util.*

data class MediaFile(
    val file: File,
    val createDate : Date,
    val thumbnail : Bitmap
)