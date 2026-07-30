package com.jackson.blebridge.lint.designsystem

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API

/**
 * BleBridge custom lint의 공개 Issue 목록입니다.
 *
 * Detector에 규칙을 추가했다면 [issues]에도 등록해야 실제 feature의 `lintChecks`에서
 * 실행됩니다. 각 Issue는 신규 위반을 빌드에서 차단할 수 있도록 ERROR severity를
 * 유지합니다.
 */
class DesignSystemIssueRegistry : IssueRegistry() {
    override val api: Int = CURRENT_API

    override val issues = listOf(
        FeatureDesignSystemDetector.FOUNDATION_TOKEN_ISSUE,
        FeatureDesignSystemDetector.MATERIAL_COMPONENT_ISSUE,
        FeatureDesignSystemDetector.RAW_DESIGN_VALUE_ISSUE,
    )

    override val vendor = Vendor(
        vendorName = "BleBridge",
        identifier = "com.jackson.blebridge",
        feedbackUrl = "https://github.com/jackson/blebridge/issues",
    )
}
