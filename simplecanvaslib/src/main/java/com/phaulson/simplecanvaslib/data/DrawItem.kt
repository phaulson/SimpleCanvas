package com.phaulson.simplecanvaslib.data

import android.graphics.Paint
import android.graphics.Path

internal data class DrawItem(var path: Path, var paint: Paint, var x: Float, var y: Float)