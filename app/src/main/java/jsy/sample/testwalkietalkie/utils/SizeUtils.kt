package jsy.sample.testwalkietalkie.utils

import android.content.res.Resources

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

val Int.pxToDp: Float
    get() = (this/ Resources.getSystem().displayMetrics.density)