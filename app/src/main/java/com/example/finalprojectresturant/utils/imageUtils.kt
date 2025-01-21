package com.example.finalprojectresturant.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log

fun decodeBase64ToImage(base64: String): Bitmap {
    val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}