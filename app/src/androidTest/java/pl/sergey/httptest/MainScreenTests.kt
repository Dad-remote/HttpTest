package pl.sergey.httptest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule
import pl.sergey.httptest.ui.MainActivity

@RunWith(AndroidJUnit4::class)
class MainScreenTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun checkStartButton() {
        composeTestRule.onNodeWithText("Start").assertIsDisplayed()
    }

}