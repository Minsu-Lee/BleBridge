package com.jackson.blebridge.core.designsystem.icon

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Color-free vectors synchronized with the downloadable SVG resources in
 * `docs/design/BLETransferApp.dc.html`.
 *
 * Callers must provide a semantic tint through `Icon`.
 */
@Immutable
object AppIcons {
    val Logo: ImageVector by lazy {
        ImageVector.Builder(
            name = "AppLogo",
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(9f, 19.8f)
                curveTo(10.8f, 19.8f, 12.2f, 21.2f, 12.2f, 23f)
                curveTo(12.2f, 24.8f, 10.8f, 26.2f, 9f, 26.2f)
                curveTo(7.2f, 26.2f, 5.8f, 24.8f, 5.8f, 23f)
                curveTo(5.8f, 21.2f, 7.2f, 19.8f, 9f, 19.8f)
                close()
            }
            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(14f, 23f)
                curveTo(14f, 19.2f, 12f, 15.8f, 9f, 14.3f)
                moveTo(20f, 23f)
                curveTo(20f, 16.8f, 16.8f, 11.2f, 11.5f, 8.3f)
                moveTo(26f, 23f)
                curveTo(26f, 14.2f, 21.4f, 6.1f, 14f, 2f)
            }
        }.build()
    }

    val Add by svgIcon(
        name = "Add",
        stroke("M12 5v14M5 12h14", roundCap = true),
    )
    val Send by svgIcon(
        name = "Send",
        stroke("M5 12h13M12 6l6 6-6 6", width = 2.2f, roundCap = true, roundJoin = true),
    )
    val Back by svgIcon(
        name = "Back",
        stroke("M15 6l-6 6 6 6", roundCap = true, roundJoin = true),
    )
    val Close by svgIcon(
        name = "Close",
        stroke("M6 6l12 12M18 6L6 18", roundCap = true),
    )
    val Check by svgIcon(
        name = "Check",
        stroke("M5 12l5 5 9-11", width = 3f, roundCap = true, roundJoin = true),
    )
    val Chat by svgIcon(
        name = "Chat",
        stroke("M6 4h12a2 2 0 0 1 2 2v9a2 2 0 0 1-2 2H9l-4 4V6a2 2 0 0 1 2-2z", roundJoin = true),
    )
    val Download by svgIcon(
        name = "Download",
        stroke("M12 4v11", roundCap = true),
        stroke("M8 11l4 4 4-4", roundCap = true, roundJoin = true),
        stroke("M5 20h14", roundCap = true),
    )
    val SwitchRole by svgIcon(
        name = "SwitchRole",
        stroke("M7 8h11l-3-3", roundCap = true, roundJoin = true),
        stroke("M17 16H6l3 3", roundCap = true, roundJoin = true),
    )
    val Retry by svgIcon(
        name = "Retry",
        stroke("M4 12a8 8 0 1 1 2.3 5.6", roundCap = true),
        stroke("M4 19v-5h5", roundCap = true, roundJoin = true),
    )
    val Settings by svgIcon(
        name = "Settings",
        stroke("M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0z"),
        stroke(
            "M19.4 15a1.7 1.7 0 0 0 .34 1.88l.06.06-2.83 2.83-.06-.06A1.7 1.7 0 0 0 15 19.4a1.7 1.7 0 0 0-1 .6 1.7 1.7 0 0 0-.4 1.1V21h-4v-.09A1.7 1.7 0 0 0 8.5 19.4a1.7 1.7 0 0 0-1.88.34l-.06.06-2.83-2.83.06-.06A1.7 1.7 0 0 0 4.6 15a1.7 1.7 0 0 0-.6-1 1.7 1.7 0 0 0-1.1-.4H3v-4h.09A1.7 1.7 0 0 0 4.6 8.5a1.7 1.7 0 0 0-.34-1.88l-.06-.06 2.83-2.83.06.06A1.7 1.7 0 0 0 9 4.6a1.7 1.7 0 0 0 1-.6 1.7 1.7 0 0 0 .4-1.1V3h4v.09A1.7 1.7 0 0 0 15.5 4.6a1.7 1.7 0 0 0 1.88-.34l.06-.06 2.83 2.83-.06.06A1.7 1.7 0 0 0 19.4 9c.14.39.36.73.66 1 .3.27.68.41 1.08.4H21v4h-.09A1.7 1.7 0 0 0 19.4 15Z",
            width = 1.7f,
            roundCap = true,
            roundJoin = true,
        ),
    )
    val File by svgIcon(
        name = "File",
        stroke("M14 3v5h5", roundJoin = true),
        stroke("M14 3H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V8z", roundJoin = true),
    )
    val Image by svgIcon(
        name = "Image",
        stroke("M6 4h12a3 3 0 0 1 3 3v10a3 3 0 0 1-3 3H6a3 3 0 0 1-3-3V7a3 3 0 0 1 3-3z"),
        fill("M10.3 9a1.8 1.8 0 1 1-3.6 0 1.8 1.8 0 0 1 3.6 0z"),
        stroke("M4 18l5-5 4 4 3-3 4 4", roundJoin = true),
    )
    val Video by svgIcon(
        name = "Video",
        stroke("M5 6h9a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2z"),
        stroke("M16 10l5-3v10l-5-3", roundJoin = true),
    )
    val Camera by svgIcon(
        name = "Camera",
        stroke("M6 6h12a3 3 0 0 1 3 3v8a3 3 0 0 1-3 3H6a3 3 0 0 1-3-3V9a3 3 0 0 1 3-3z"),
        stroke("M15.4 13a3.4 3.4 0 1 1-6.8 0 3.4 3.4 0 0 1 6.8 0z"),
        stroke("M8 6l1.5-2h5L16 6", roundJoin = true),
    )
    val Play by svgIcon(name = "Play", fill("M8 5v14l11-7z"))
    val Stop by svgIcon(name = "Stop", fill("M7.5 6h9A1.5 1.5 0 0 1 18 7.5v9a1.5 1.5 0 0 1-1.5 1.5h-9A1.5 1.5 0 0 1 6 16.5v-9A1.5 1.5 0 0 1 7.5 6z"))
    val Pause by svgIcon(
        name = "Pause",
        fill("M7 5h2a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1H7a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1z"),
        fill("M15 5h2a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1h-2a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1z"),
    )
    val SkipBackward by svgIcon(
        name = "SkipBackward",
        stroke("M11 6A7 7 0 1 1 5 9", roundCap = true),
        stroke("M11 3v4h4", roundCap = true, roundJoin = true),
    )
    val SkipForward by svgIcon(
        name = "SkipForward",
        stroke("M13 6a7 7 0 1 0 6 3", roundCap = true),
        stroke("M13 3v4H9", roundCap = true, roundJoin = true),
    )
    val DevicePhone by svgIcon(
        name = "DevicePhone",
        stroke("M8 2h8a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2z"),
        stroke("M10 5h4", roundCap = true),
    )
    val SignalStrength by svgIcon(
        name = "SignalStrength",
        fill("M4 16h3v4H4z"),
        fill("M9 12h3v8H9z"),
        fill("M14 8h3v12h-3z"),
        fill("M19 4h3v16h-3z"),
    )
    val More by svgIcon(
        name = "More",
        fill("M6.8 12a1.8 1.8 0 1 1-3.6 0 1.8 1.8 0 0 1 3.6 0z"),
        fill("M13.8 12a1.8 1.8 0 1 1-3.6 0 1.8 1.8 0 0 1 3.6 0z"),
        fill("M20.8 12a1.8 1.8 0 1 1-3.6 0 1.8 1.8 0 0 1 3.6 0z"),
    )
    val Error by svgIcon(
        name = "Error",
        stroke("M12 7v6", roundCap = true),
        fill("M13 17a1 1 0 1 1-2 0 1 1 0 0 1 2 0z"),
        stroke("M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0z"),
    )
}

private data class SvgPath(
    val data: String,
    val fill: Boolean,
    val strokeWidth: Float = 2f,
    val strokeCap: StrokeCap = StrokeCap.Butt,
    val strokeJoin: StrokeJoin = StrokeJoin.Miter,
)

private fun fill(data: String) = SvgPath(data = data, fill = true)

private fun stroke(
    data: String,
    width: Float = 2f,
    roundCap: Boolean = false,
    roundJoin: Boolean = false,
) = SvgPath(
    data = data,
    fill = false,
    strokeWidth = width,
    strokeCap = if (roundCap) StrokeCap.Round else StrokeCap.Butt,
    strokeJoin = if (roundJoin) StrokeJoin.Round else StrokeJoin.Miter,
)

private fun svgIcon(
    name: String,
    vararg paths: SvgPath,
): Lazy<ImageVector> = lazy {
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        paths.forEach { svgPath ->
            addPath(
                pathData = PathParser().parsePathString(svgPath.data).toNodes(),
                fill = if (svgPath.fill) SolidColor(Color.Black) else null,
                stroke = if (svgPath.fill) null else SolidColor(Color.Black),
                strokeLineWidth = svgPath.strokeWidth,
                strokeLineCap = svgPath.strokeCap,
                strokeLineJoin = svgPath.strokeJoin,
            )
        }
    }.build()
}
