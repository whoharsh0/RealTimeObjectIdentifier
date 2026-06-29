package com.rudra.objectidentifier.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rudra.objectidentifier.R
import com.rudra.objectidentifier.ui.theme.AccentCyan
import kotlinx.coroutines.launch

private data class OnboardingPage(val title: String, val body: String)

@Composable
fun OnboardingDialog(
    onDismiss: () -> Unit,
    reduceMotion: Boolean = false
) {
    val pages = listOf(
        OnboardingPage(
            stringResource(R.string.onboarding_page1_title),
            stringResource(R.string.onboarding_page1_body)
        ),
        OnboardingPage(
            stringResource(R.string.onboarding_page2_title),
            stringResource(R.string.onboarding_page2_body)
        ),
        OnboardingPage(
            stringResource(R.string.onboarding_page3_title),
            stringResource(R.string.onboarding_page3_body)
        )
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.onboarding_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth()
                ) { page ->
                    val item = pages[page]
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 160.dp)
                            .padding(horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ScanViewfinder(
                            modifier = Modifier.size(96.dp),
                            reduceMotion = reduceMotion
                        )
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = item.body,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                PageDots(
                    pageCount = pages.size,
                    currentPage = pagerState.currentPage,
                    reduceMotion = reduceMotion
                )

                val isLast = pagerState.currentPage >= pages.size - 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = stringResource(
                                if (isLast) R.string.onboarding_got_it else R.string.onboarding_skip
                            )
                        )
                    }
                    Button(
                        onClick = {
                            if (isLast) {
                                onDismiss()
                            } else {
                                scope.launch {
                                    val next = pagerState.currentPage + 1
                                    if (reduceMotion) {
                                        pagerState.scrollToPage(next)
                                    } else {
                                        pagerState.animateScrollToPage(next)
                                    }
                                }
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(
                                if (isLast) R.string.onboarding_get_started else R.string.onboarding_next
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PageDots(
    pageCount: Int,
    currentPage: Int,
    reduceMotion: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val selected = index == currentPage
            val width by animateDpAsState(
                targetValue = if (selected) 22.dp else 8.dp,
                animationSpec = if (reduceMotion) snap() else tween(durationMillis = 250),
                label = "dot-width"
            )
            val color by animateColorAsState(
                targetValue = if (selected) AccentCyan else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                animationSpec = if (reduceMotion) snap() else tween(durationMillis = 250),
                label = "dot-color"
            )
            Box(
                modifier = Modifier
                    .size(width = width, height = 8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
