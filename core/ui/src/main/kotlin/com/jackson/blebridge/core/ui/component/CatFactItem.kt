package com.jackson.blebridge.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jackson.blebridge.core.designsystem.theme.AppTheme
import com.jackson.blebridge.core.ui.R

@Composable
fun CatFactItem(
    fact: String,
    length: Int,
    modifier: Modifier = Modifier,
) {
    val lengthDescription = stringResource(
        R.string.cat_fact_length_description,
        length,
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = AppTheme.spacing.screen,
                vertical = AppTheme.spacing.md,
            ),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxl),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Card(
            modifier = Modifier
                .size(CatFactItemDefaults.ThumbnailSize)
                .semantics { contentDescription = lengthDescription }
                .testTag("${CatFactItemDefaults.LENGTH_TAG_PREFIX}$length"),
            shape = RoundedCornerShape(AppTheme.radius.medium),
            colors = CardDefaults.cardColors(
                containerColor = AppTheme.colors.serverContainer,
            ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = length.toString(),
                    style = AppTheme.typography.monoTitle,
                    color = AppTheme.colors.onServerContainer,
                )
            }
        }
        Text(
            text = fact,
            modifier = Modifier
                .weight(1f)
                .testTag("${CatFactItemDefaults.FACT_TAG_PREFIX}$length"),
            style = AppTheme.typography.bodyLarge,
            color = AppTheme.colors.textPrimary,
        )
    }
}

internal object CatFactItemDefaults {
    const val LENGTH_TAG_PREFIX = "cat_fact_length_"
    const val FACT_TAG_PREFIX = "cat_fact_"
    val ThumbnailSize = 64.dp
}

@Preview
@Composable
private fun CatFactItemPreview() {
    AppTheme {
        CatFactItem(
            fact = "Cats have five toes on their front paws.",
            length = 42,
        )
    }
}
