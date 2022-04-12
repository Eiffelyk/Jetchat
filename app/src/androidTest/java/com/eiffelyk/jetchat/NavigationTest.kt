package com.eiffelyk.jetchat

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.test.espresso.Espresso
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class NavigationTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun app_launches() {
        assertEquals(getNavController().currentDestination?.id, R.id.nav_home)
    }

    @Test
    fun profileScreen_back_conversationScreen() {
        val navController = getNavController()
        navigateToProfile("Taylor Brooks")
        assertEquals(navController.currentDestination?.id, R.id.nav_profile)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.display_name))
            .assertIsDisplayed()
        Espresso.pressBack()
        assertEquals(navController.currentDestination?.id, R.id.nav_home)
    }

    @Test
    fun drawer_conversationScreen_backstackPopUp() {
        navigateToProfile("Ali Conors(you)")
        navigateToHome()
        navigateToProfile("Taylor Brooks")
        navigateToHome()
        assertEquals(getNavController().currentDestination?.id, R.id.nav_home)
    }


    private fun getNavController(): NavController {
        return composeTestRule.activity.findNavController(R.id.nav_host_fragment)
    }

    private fun navigateToProfile(name: String) {
        composeTestRule.onNodeWithContentDescription(
            composeTestRule.activity.getString(R.string.navigation_drawer_open)
        ).performClick()
        composeTestRule.onNode(hasText(name) and isInDrawer()).performClick()
    }

    private fun navigateToHome() {
        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.navigation_drawer_open))
            .performClick()
        composeTestRule.onNode(hasText("composers") and isInDrawer()).performClick()
    }

    private fun isInDrawer() = hasAnyAncestor(isDrawer())

    private fun isDrawer() = SemanticsMatcher.expectValue(
        SemanticsProperties.PaneTitle,
        composeTestRule.activity.getString(androidx.compose.ui.R.string.navigation_menu)
    )
}