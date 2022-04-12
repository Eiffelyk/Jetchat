package com.eiffelyk.jetchat

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import com.eiffelyk.jetchat.data.exampleUiState
import com.eiffelyk.jetchat.ui.conversation.ConversationContent
import com.eiffelyk.jetchat.ui.conversation.KeyboardShownKey
import com.eiffelyk.jetchat.ui.conversation.LocalBackPressedDispatcher
import com.eiffelyk.jetchat.ui.theme.JetchatTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserInputTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val activity by lazy { composeTestRule.activity }

    @Before
    fun setUp() {
        val onBackPressedDispatcher = composeTestRule.activity.onBackPressedDispatcher
        composeTestRule.setContent {
            CompositionLocalProvider(LocalBackPressedDispatcher provides onBackPressedDispatcher) {
                JetchatTheme {
                    ConversationContent(uiState = exampleUiState, navigateToProfile = {})
                }
            }
        }
    }

    @Test
    fun emojiSelector_isClosedWithBack() {
        openEmojiSelector()
        assertEmojiSelectorIsDisplayed()
        composeTestRule.onNode(SemanticsMatcher.expectValue(KeyboardShownKey, false)).assertExists()
        Espresso.pressBack()
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodesWithContentDescription(activity.getString(R.string.emoji_selector_desc))
                .fetchSemanticsNodes().isEmpty()
        }
        assertEmojiSelectorDoesNotExist()
    }

    @Test
    fun extendedUserInputShown_textFieldClicked_extendedUserInputHides() {
        openEmojiSelector()
        clickOnTextField()
        assertEmojiSelectorDoesNotExist()
    }

    @Test
    fun keyboardShown_emojiSelectorOpened_keyboardHides() {
        clickOnTextField()
        Thread.sleep(200)
        composeTestRule.onNode(SemanticsMatcher.expectValue(KeyboardShownKey, true)).assertExists()
        openEmojiSelector()
        composeTestRule.onNode(SemanticsMatcher.expectValue(KeyboardShownKey, false)).assertExists()
    }

    @Test
    fun sendButton_enableToggles() {
        findSendButton().assertIsNotEnabled()
        findTextInputField().performTextInput("Some Text")
        findSendButton().assertIsEnabled()
    }

    private fun findSendButton() = composeTestRule.onNodeWithText(activity.getString(R.string.send))
    private fun clickOnTextField() =
        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.textfield_desc))
            .performClick()

    private fun assertEmojiSelectorIsDisplayed() =
        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.emoji_selector_desc))
            .assertIsDisplayed()

    private fun assertEmojiSelectorDoesNotExist() =
        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.emoji_selector_desc))
            .assertDoesNotExist()


    private fun openEmojiSelector() = composeTestRule.onNodeWithContentDescription(
        label = activity.getString(R.string.emoji_selector_bt_desc),
        useUnmergedTree = true
    ).performClick()

    private fun findTextInputField(): SemanticsNodeInteraction {
        return composeTestRule.onNode(
            hasSetTextAction() and hasAnyAncestor(hasContentDescription(activity.getString(R.string.textfield_desc)))
        )
    }
}