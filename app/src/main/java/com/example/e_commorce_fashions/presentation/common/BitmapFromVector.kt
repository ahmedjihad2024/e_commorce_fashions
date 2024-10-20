package com.example.e_commorce_fashions.presentation.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun bitmapDescriptorFromVector(context: Context, @DrawableRes vectorResId: Int, pxWidth: Int? = null, pxHeight: Int? = null, color: Color? = null): BitmapDescriptor {
    val db = ContextCompat.getDrawable(context, vectorResId)
    val bitmap = Bitmap.createBitmap(pxWidth ?: db!!.intrinsicWidth, pxHeight ?: db!!.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    db!!.setBounds(0, 0, canvas.width, canvas.height)
    db.draw(canvas)
    if (color != null) db.setTint(color.toArgb())
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}