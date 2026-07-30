package com.jackson.blebridge.lint.designsystem

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue

class FeatureDesignSystemDetectorTest : LintDetectorTest() {
    override fun getDetector(): Detector = FeatureDesignSystemDetector()

    override fun getIssues(): List<Issue> = listOf(
        FeatureDesignSystemDetector.FOUNDATION_TOKEN_ISSUE,
        FeatureDesignSystemDetector.MATERIAL_COMPONENT_ISSUE,
        FeatureDesignSystemDetector.RAW_DESIGN_VALUE_ISSUE,
    )

    fun testFeatureFoundationTokenIsBlocked() {
        lint()
            .allowMissingSdk()
            .files(
                themeStub,
                kotlin(
                    "src/main/kotlin/com/jackson/blebridge/feature/sample/Sample.kt",
                    """
                    package com.jackson.blebridge.feature.sample

                    import com.jackson.blebridge.core.designsystem.theme.AppTheme

                    fun sample() {
                        println(AppTheme.colors)
                    }
                    """.trimIndent(),
                ),
            )
            .run()
            .expectContains("[FoundationTokenInFeature]")
    }

    fun testFeatureRawDesignValuesAreBlocked() {
        lint()
            .allowMissingSdk()
            .files(
                dimensionStub,
                colorStub,
                kotlin(
                    "src/main/kotlin/com/jackson/blebridge/feature/sample/Sample.kt",
                    """
                    package com.jackson.blebridge.feature.sample

                    import androidx.compose.ui.graphics.Color
                    import androidx.compose.ui.unit.dp

                    fun sample() {
                        println(16.dp)
                        println(Color(0xFF000000))
                    }
                    """.trimIndent(),
                ),
            )
            .run()
            .expectContains("[RawDesignValueInFeature]")
    }

    fun testFeatureMaterialComponentIsBlocked() {
        lint()
            .allowMissingSdk()
            .files(
                materialStub,
                kotlin(
                    "src/main/kotlin/com/jackson/blebridge/feature/sample/Sample.kt",
                    """
                    package com.jackson.blebridge.feature.sample

                    import androidx.compose.material3.Button

                    fun sample() {
                        Button {}
                    }
                    """.trimIndent(),
                ),
            )
            .run()
            .expectContains("[MaterialComponentInFeature]")
    }

    fun testCoreUiUsageIsAllowed() {
        lint()
            .allowMissingSdk()
            .files(
                themeStub,
                materialStub,
                kotlin(
                    "src/main/kotlin/com/jackson/blebridge/core/ui/Sample.kt",
                    """
                    package com.jackson.blebridge.core.ui

                    import androidx.compose.material3.Button
                    import com.jackson.blebridge.core.designsystem.theme.AppTheme

                    fun sample() {
                        println(AppTheme.colors)
                        Button {}
                    }
                    """.trimIndent(),
                ),
            )
            .run()
            .expectClean()
    }

    private val themeStub = kotlin(
        "src/main/kotlin/com/jackson/blebridge/core/designsystem/theme/AppTheme.kt",
        """
        package com.jackson.blebridge.core.designsystem.theme

        object AppTheme {
            val colors: String = "colors"
        }
        """.trimIndent(),
    )

    private val dimensionStub = kotlin(
        "src/main/kotlin/androidx/compose/ui/unit/Dimension.kt",
        """
        package androidx.compose.ui.unit

        val Int.dp: Int get() = this
        """.trimIndent(),
    )

    private val colorStub = kotlin(
        "src/main/kotlin/androidx/compose/ui/graphics/Color.kt",
        """
        package androidx.compose.ui.graphics

        fun Color(value: Long): Long = value
        """.trimIndent(),
    )

    private val materialStub = kotlin(
        "src/main/kotlin/androidx/compose/material3/Button.kt",
        """
        package androidx.compose.material3

        fun Button(content: () -> Unit) = content()
        """.trimIndent(),
    )
}
