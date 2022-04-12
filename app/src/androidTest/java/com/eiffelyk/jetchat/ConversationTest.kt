package com.eiffelyk.jetchat

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.eiffelyk.jetchat.data.exampleUiState
import com.eiffelyk.jetchat.ui.conversation.ConversationContent
import com.eiffelyk.jetchat.ui.conversation.ConversationTestTag
import com.eiffelyk.jetchat.ui.conversation.ConversationUiState
import com.eiffelyk.jetchat.ui.conversation.LocalBackPressedDispatcher
import com.eiffelyk.jetchat.ui.theme.JetchatTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ConversationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val themeIsDark = MutableStateFlow(false)

    @Before
    fun setUp() {
        // Launch the conversation screen
        composeTestRule.setContent {
            val onBackPressedDispatcher = composeTestRule.activity.onBackPressedDispatcher
            CompositionLocalProvider(
                LocalBackPressedDispatcher provides onBackPressedDispatcher,
            ) {
                JetchatTheme(darkTheme = themeIsDark.collectAsState(false).value) {
                    ConversationContent(
                        uiState = conversationTestUiState,
                        navigateToProfile = { },
                        onNavIconPressed = { }
                    )
                }
            }
        }
    }

    @Test
    fun app_launches() {
        composeTestRule.onNodeWithTag(ConversationTestTag).assertIsDisplayed()
    }

    @Test
    fun userScrollsUp_jumpToBottomAppears() {
        findJumpToBottom().assertDoesNotExist()
        composeTestRule.onNodeWithTag(ConversationTestTag).performTouchInput {
            this.swipe(
                start = this.center,
                end = Offset(this.center.x, this.center.y.plus(500)),
                durationMillis = 200
            )
        }
        findJumpToBottom().assertIsDisplayed()
    }

    @Test
    fun jumpToBottom_snapsToBottomAndDisappears() {
        composeTestRule.onNodeWithTag(ConversationTestTag).performTouchInput {
            this.swipe(
                start = this.center,
                end = Offset(this.center.x, this.center.y.plus(500)),
                durationMillis = 200
            )
        }
        findJumpToBottom().performClick()
        findJumpToBottom().assertDoesNotExist()
    }

    @Test
    fun jumpToBottom_snapsToBottomAfterUserInteracted() {
        composeTestRule.onNodeWithTag(testTag = ConversationTestTag, useUnmergedTree = true)
            .performTouchInput {
                this.swipe(
                    start = this.center,
                    end = Offset(this.center.x, this.center.y.plus(500)),
                    durationMillis = 200
                )
            }
        findJumpToBottom().performClick()
        openEmojiSelector()
        findJumpToBottom().assertDoesNotExist()
    }

    @Test
    fun changeTheme_scrollIsPersisted() {
        composeTestRule.onNodeWithTag(ConversationTestTag).performTouchInput {
            this.swipe(
                start = this.center,
                end = Offset(this.center.x, this.center.y.plus(500)),
                durationMillis = 200
            )
        }
        findJumpToBottom().assertIsDisplayed()
        themeIsDark.value = true
        findJumpToBottom().assertIsDisplayed()
    }


    private fun findJumpToBottom() =
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.jumpBottom))

    private fun openEmojiSelector() = composeTestRule.onNodeWithContentDescription(
        label = composeTestRule.activity.getString(R.string.emoji_selector_bt_desc),
        useUnmergedTree = true
    ).performClick()
}

private val conversationTestUiState = ConversationUiState(
    channelName = "#composers",
    channelMembers = 42,
    initialMessages = (exampleUiState.messages.plus(
        exampleUiState.messages
    ))
)