package com.jackson.blebridge.core.designsystem.theme.semantic.spacing

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp

@Immutable
object Spacing {
    val none = 0.dp
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 6.dp
    val md = 8.dp
    val lg = 10.dp
    val xl = 12.dp
    val xxl = 16.dp
    /*
     * section과 screen은 크기 단계가 아니라 화면 레이아웃 용도를 표현하는 시맨틱 토큰이다.
     *
     * 공통 컴포넌트 구현 전까지 기존 API와 디자인 문서의 기준값으로 유지한다.
     * 화면 구현 시 section은 섹션 사이 간격, screen은 화면 콘텐츠의 기본 수평 여백으로
     * 사용되는지 실제 모바일·태블릿 레이아웃에서 검증한다.
     *
     * 용도가 확정되면 sectionSpacing, screenHorizontalPadding처럼 축과 목적이 드러나는
     * 이름으로 변경하고 Kotlin 참조, 컴포넌트 프롬프트, README, BLETransferApp.dc.html의
     * 표시 JSON과 다운로드 JSON을 같은 작업에서 함께 갱신한다.
     *
     * 특정 Scaffold나 레이아웃 컴포넌트에서만 사용되는 값으로 확인될 경우에만 해당
     * Component Defaults/Tokens로 이동하며, 단순히 의미 이름이라는 이유로 이동하지 않는다.
     */
    val section = 20.dp
    val screen = 24.dp
    val large = 32.dp
    val xLarge = 40.dp
    val huge = 48.dp
}
