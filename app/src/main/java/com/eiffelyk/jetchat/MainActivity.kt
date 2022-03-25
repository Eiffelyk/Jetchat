package com.eiffelyk.jetchat

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.eiffelyk.jetchat.components.JetchatScaffold
import com.eiffelyk.jetchat.databinding.ContentMainBinding
import com.eiffelyk.jetchat.ui.conversation.BackPressHandler
import com.eiffelyk.jetchat.ui.conversation.LocalBackPressedDispatcher
import com.eiffelyk.jetchat.ui.theme.JetchatTheme
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            CompositionLocalProvider(LocalBackPressedDispatcher provides this.onBackPressedDispatcher) {
                val drawerState: DrawerState =
                    rememberDrawerState(initialValue = DrawerValue.Closed)
                val drawerOpen by viewModel.drawerShouldBeOpened.collectAsState()
                if (drawerOpen) {
                    LaunchedEffect(Unit) {
                        drawerState.open()
                        viewModel.resetOpenDrawerAction()
                    }
                }
                val scope = rememberCoroutineScope()
                if (drawerState.isOpen) {
                    BackPressHandler {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                }
                JetchatScaffold(
                    drawerState = drawerState,
                    onChatClicked = {
                        findNavController().popBackStack(R.id.nav_home, false)
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    onProfileClicked = {
                        val bundle = bundleOf("userId" to it)
                        findNavController().navigate(R.id.nav_profile, bundle)
                        scope.launch {
                            drawerState.close()
                        }
                    }
                ) {
                    AndroidViewBinding(ContentMainBinding::inflate)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * See https://issuetracker.google.com/142847973
     */
    private fun findNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }
}
