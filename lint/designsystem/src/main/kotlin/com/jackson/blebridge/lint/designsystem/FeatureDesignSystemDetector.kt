package com.jackson.blebridge.lint.designsystem

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiMember
import com.intellij.psi.PsiNamedElement
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.ULiteralExpression
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.UResolvable
import org.jetbrains.uast.UastCallKind
import org.jetbrains.uast.getContainingUFile

/**
 * Feature 모듈이 디자인시스템 경계를 우회하는 코드를 작성하지 못하도록 검사합니다.
 *
 * 현재 제공하는 lint 규칙은 다음 세 가지입니다.
 *
 * 1. [FOUNDATION_TOKEN_ISSUE]
 *    - `AppTheme.colors`, `spacing`, `radius`, `typography` 등 foundation token 직접 참조를
 *      차단합니다.
 *    - Feature는 `core:ui`의 완성된 컴포넌트와 컴포넌트가 공개한 API/token만 사용합니다.
 * 2. [MATERIAL_COMPONENT_ISSUE]
 *    - Button, Card, TextField, TopAppBar 등 상태·표면·상호작용 정책을 가진 Material3
 *      컴포넌트 직접 사용을 차단합니다.
 *    - Row, Column, Box, LazyColumn 같은 layout primitive와 Text, Icon은 허용합니다.
 * 3. [RAW_DESIGN_VALUE_ISSUE]
 *    - Feature에서 raw `Color(...)`, 숫자 `.dp`, `.sp`를 생성하는 코드를 차단합니다.
 *
 * 검사 범위는 `com.jackson.blebridge.feature.*`의 main source이며 test/androidTest는
 * 제외합니다. 기존 과도기 위반은 각 feature의 `lint-baseline.xml`에서만 관리하고,
 * 신규 규칙을 추가할 때는 이 KDoc, IssueRegistry, 단위 테스트와 lint README를 함께
 * 갱신해야 합니다.
 */
class FeatureDesignSystemDetector : Detector(), SourceCodeScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(
        UQualifiedReferenceExpression::class.java,
        UCallExpression::class.java,
    )

    override fun createUastHandler(context: JavaContext) =
        object : UElementHandler() {
            override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
                if (!context.isFeatureSource()) return

                val selector = node.selector.asSourceString().substringBefore("(")
                val resolved = (node.selector as? UResolvable)?.resolve()
                val owner = (resolved as? PsiMember)
                    ?.containingClass
                    ?.qualifiedName
                val propertyName = (resolved as? PsiNamedElement)
                    ?.name
                    ?.removePrefix("get")
                    ?.replaceFirstChar(Char::lowercase)
                    ?: selector

                if (
                    owner == THEME_QUALIFIED_NAME &&
                    propertyName in FOUNDATION_PROPERTIES
                ) {
                    context.report(
                        FOUNDATION_TOKEN_ISSUE,
                        node,
                        context.getLocation(node),
                        "Feature에서는 `$THEME_NAME.$propertyName`를 직접 참조하지 말고 " +
                            "core:ui 컴포넌트 또는 해당 컴포넌트의 공개 토큰을 사용하세요.",
                    )
                }

                if (
                    selector in DIMENSION_EXTENSIONS &&
                    node.receiver is ULiteralExpression
                ) {
                    context.report(
                        RAW_DESIGN_VALUE_ISSUE,
                        node,
                        context.getLocation(node),
                        "Feature의 raw `${selector}` 값은 디자인시스템 경계를 우회합니다. " +
                            "core:ui 컴포넌트나 공개 컴포넌트 토큰을 사용하세요.",
                    )
                }
            }

            override fun visitCallExpression(node: UCallExpression) {
                if (!context.isFeatureSource()) return

                val resolved = node.resolve() ?: return
                val owner = context.evaluator.getPackage(resolved)?.qualifiedName.orEmpty()

                if (owner == COMPOSE_GRAPHICS_PACKAGE && node.methodName == "Color") {
                    context.report(
                        RAW_DESIGN_VALUE_ISSUE,
                        node,
                        context.getLocation(node),
                        "Feature에서 raw `Color`를 생성하지 말고 core:ui 컴포넌트나 " +
                            "공개 컴포넌트 토큰을 사용하세요.",
                    )
                }

                if (
                    node.kind == UastCallKind.METHOD_CALL &&
                    owner == MATERIAL3_PACKAGE &&
                    node.methodName in BLOCKED_MATERIAL_COMPONENTS
                ) {
                    context.report(
                        MATERIAL_COMPONENT_ISSUE,
                        node,
                        context.getLocation(node),
                        "Feature에서 Material3 `${node.methodName}`를 직접 사용하지 말고 " +
                            "core:ui 컴포넌트를 사용하세요.",
                    )
                }
            }
        }

    private fun JavaContext.isFeatureSource(): Boolean =
        uastFile?.packageName?.startsWith(FEATURE_PACKAGE_PREFIX) == true &&
            !file.path.contains("/test/") &&
            !file.path.contains("/androidTest/")

    companion object {
        private const val FEATURE_PACKAGE_PREFIX = "com.jackson.blebridge.feature."
        private const val THEME_NAME = "AppTheme"
        private const val THEME_QUALIFIED_NAME =
            "com.jackson.blebridge.core.designsystem.theme.AppTheme"
        private const val MATERIAL3_PACKAGE = "androidx.compose.material3"
        private const val COMPOSE_GRAPHICS_PACKAGE = "androidx.compose.ui.graphics"

        private val FOUNDATION_PROPERTIES = setOf(
            "colors",
            "spacing",
            "radius",
            "iconSize",
            "controlSize",
            "gradients",
            "motion",
            "typography",
            "roleColors",
            "activityColors",
            "activityMotion",
            "chatTokens",
            "media",
        )
        private val DIMENSION_EXTENSIONS = setOf("dp", "sp")
        private val BLOCKED_MATERIAL_COMPONENTS = setOf(
            "AlertDialog",
            "AssistChip",
            "Badge",
            "Button",
            "Card",
            "Checkbox",
            "CircularProgressIndicator",
            "DropdownMenu",
            "ElevatedButton",
            "ExtendedFloatingActionButton",
            "FilledIconButton",
            "FloatingActionButton",
            "HorizontalDivider",
            "IconButton",
            "LinearProgressIndicator",
            "NavigationBar",
            "NavigationDrawerItem",
            "OutlinedButton",
            "OutlinedCard",
            "OutlinedTextField",
            "RadioButton",
            "Scaffold",
            "Slider",
            "Snackbar",
            "Switch",
            "Tab",
            "TextButton",
            "TextField",
            "TopAppBar",
        )

        private val IMPLEMENTATION = Implementation(
            FeatureDesignSystemDetector::class.java,
            Scope.JAVA_FILE_SCOPE,
        )

        val FOUNDATION_TOKEN_ISSUE: Issue = Issue.create(
            id = "FoundationTokenInFeature",
            briefDescription = "Feature의 foundation token 직접 참조",
            explanation = """
                Feature는 Primitive/Semantic/Contextual foundation token을 조립하지 않습니다.
                화면은 core:ui의 완성된 컴포넌트와 그 컴포넌트가 공개한 token/API로 구성해야
                테마 변경과 컴포넌트 정책을 한곳에서 통제할 수 있습니다.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 8,
            severity = Severity.ERROR,
            implementation = IMPLEMENTATION,
        )

        val MATERIAL_COMPONENT_ISSUE: Issue = Issue.create(
            id = "MaterialComponentInFeature",
            briefDescription = "Feature의 Material3 컴포넌트 직접 사용",
            explanation = """
                상호작용·표면·상태를 표현하는 Material3 컴포넌트는 앱 디자인시스템의
                크기, 색상, 모션, 접근성 정책을 우회할 수 있습니다. core:ui에 정의된
                컴포넌트를 사용하세요. Row, Column, Box 같은 layout primitive는
                이 규칙의 대상이 아닙니다.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 8,
            severity = Severity.ERROR,
            implementation = IMPLEMENTATION,
        )

        val RAW_DESIGN_VALUE_ISSUE: Issue = Issue.create(
            id = "RawDesignValueInFeature",
            briefDescription = "Feature의 raw 디자인 값 사용",
            explanation = """
                Feature에서 Color, dp, sp 값을 직접 만들면 디자인 토큰과 컴포넌트 정책을
                우회하게 됩니다. core:ui 컴포넌트 또는 공개 컴포넌트 토큰을 사용하세요.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 8,
            severity = Severity.ERROR,
            implementation = IMPLEMENTATION,
        )
    }
}
