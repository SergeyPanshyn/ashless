package com.span.ashless.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val ArcheryIcon: ImageVector
    get() {
        if (_archery != null) {
            return _archery!!
        }
        _archery = Builder(name = "Archery", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(19.302f, 19.204f)
                lineToRelative(2.698f, 4.796f)
                horizontalLineToRelative(-2.294f)
                lineToRelative(-2.025f, -3.599f)
                curveToRelative(-1.661f, 1.008f, -3.601f, 1.599f, -5.681f, 1.599f)
                reflectiveCurveToRelative(-4.021f, -0.591f, -5.681f, -1.599f)
                lineToRelative(-2.025f, 3.599f)
                horizontalLineToRelative(-2.294f)
                lineToRelative(2.698f, -4.796f)
                curveToRelative(-2.263f, -2.016f, -3.698f, -4.942f, -3.698f, -8.204f)
                curveToRelative(0.0f, -6.065f, 4.935f, -11.0f, 11.0f, -11.0f)
                curveToRelative(1.249f, 0.0f, 2.446f, 0.219f, 3.566f, 0.605f)
                lineToRelative(-1.614f, 1.614f)
                curveToRelative(-0.629f, -0.14f, -1.281f, -0.219f, -1.952f, -0.219f)
                curveToRelative(-4.962f, 0.0f, -9.0f, 4.038f, -9.0f, 9.0f)
                reflectiveCurveToRelative(4.038f, 9.0f, 9.0f, 9.0f)
                reflectiveCurveToRelative(9.0f, -4.038f, 9.0f, -9.0f)
                curveToRelative(0.0f, -0.671f, -0.08f, -1.323f, -0.219f, -1.952f)
                lineToRelative(1.614f, -1.614f)
                curveToRelative(0.386f, 1.12f, 0.605f, 2.317f, 0.605f, 3.566f)
                curveToRelative(0.0f, 3.262f, -1.435f, 6.188f, -3.698f, 8.204f)
                close()
                moveTo(15.0f, 4.0f)
                verticalLineToRelative(2.586f)
                lineToRelative(-1.725f, 1.725f)
                curveToRelative(0.309f, 0.139f, 0.6f, 0.322f, 0.846f, 0.568f)
                reflectiveCurveToRelative(0.433f, 0.531f, 0.578f, 0.835f)
                lineToRelative(1.714f, -1.714f)
                horizontalLineToRelative(2.586f)
                lineToRelative(3.0f, -3.0f)
                horizontalLineToRelative(-4.0f)
                verticalLineToRelative(-4.0f)
                lineToRelative(-3.0f, 3.0f)
                close()
                moveTo(12.0f, 6.0f)
                curveToRelative(0.342f, 0.0f, 0.677f, 0.035f, 1.0f, 0.101f)
                verticalLineToRelative(-2.021f)
                curveToRelative(-0.328f, -0.047f, -0.66f, -0.08f, -1.0f, -0.08f)
                curveToRelative(-3.86f, 0.0f, -7.0f, 3.14f, -7.0f, 7.0f)
                reflectiveCurveToRelative(3.14f, 7.0f, 7.0f, 7.0f)
                reflectiveCurveToRelative(7.0f, -3.14f, 7.0f, -7.0f)
                curveToRelative(0.0f, -0.34f, -0.033f, -0.672f, -0.08f, -1.0f)
                horizontalLineToRelative(-2.021f)
                curveToRelative(0.066f, 0.323f, 0.101f, 0.658f, 0.101f, 1.0f)
                curveToRelative(0.0f, 2.757f, -2.243f, 5.0f, -5.0f, 5.0f)
                reflectiveCurveToRelative(-5.0f, -2.243f, -5.0f, -5.0f)
                reflectiveCurveToRelative(2.243f, -5.0f, 5.0f, -5.0f)
                close()
                moveTo(12.0f, 12.0f)
                curveToRelative(-0.552f, 0.0f, -1.0f, -0.448f, -1.0f, -1.0f)
                curveToRelative(0.0f, -0.276f, 0.112f, -0.526f, 0.293f, -0.707f)
                lineToRelative(1.982f, -1.982f)
                curveToRelative(-1.112f, -0.501f, -2.51f, -0.319f, -3.397f, 0.568f)
                reflectiveCurveToRelative(0.0f, 0.0f, 0.0f, 0.0f)
                curveToRelative(-0.566f, 0.566f, -0.878f, 1.32f, -0.878f, 2.121f)
                reflectiveCurveToRelative(0.312f, 1.555f, 0.878f, 2.121f)
                reflectiveCurveToRelative(1.32f, 0.879f, 2.122f, 0.879f)
                reflectiveCurveToRelative(1.555f, -0.312f, 2.121f, -0.878f)
                reflectiveCurveToRelative(0.879f, -1.32f, 0.879f, -2.122f)
                curveToRelative(0.0f, -0.454f, -0.11f, -0.888f, -0.3f, -1.286f)
                lineToRelative(-1.992f, 1.992f)
                curveToRelative(-0.181f, 0.181f, -0.431f, 0.293f, -0.707f, 0.293f)
                close()
            }
        }
        .build()
        return _archery!!
    }

private var _archery: ImageVector? = null
