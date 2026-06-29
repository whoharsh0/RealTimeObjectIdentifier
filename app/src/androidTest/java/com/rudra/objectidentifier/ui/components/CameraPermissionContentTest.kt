package com.rudra.objectidentifier.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rudra.objectidentifier.ui.theme.RealTimeObjectIdentifierTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraPermissionContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun permissionContent_showsTitleAndGrantButton() {
        composeRule.setContent {
            RealTimeObjectIdentifierTheme {
                CameraPermissionContent(
                    permissionDenied = false,
                    onRequestPermission = {}
                )
            }
        }

        composeRule.onNodeWithText("Enable your camera").assertIsDisplayed()
        composeRule.onNodeWithText("Allow camera access").assertIsDisplayed()
    }

    @Test
    fun permissionContent_showsDeniedMessageWhenRequested() {
        composeRule.setContent {
            RealTimeObjectIdentifierTheme {
                CameraPermissionContent(
                    permissionDenied = true,
                    onRequestPermission = {}
                )
            }
        }

        composeRule.onNodeWithText("Camera blocked. Open Settings to enable access.")
            .assertIsDisplayed()
    }
}
