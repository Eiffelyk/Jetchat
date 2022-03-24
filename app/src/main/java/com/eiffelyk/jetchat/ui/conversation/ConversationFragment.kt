package com.eiffelyk.jetchat.ui.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.eiffelyk.jetchat.MainViewModel
import com.eiffelyk.jetchat.ui.theme.JetchatTheme

class ConversationFragment : Fragment() {
    private val activityViewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = ComposeView(inflater.context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setContent {
            CompositionLocalProvider(LocalBackPressedDispatcher provides requireActivity().onBackPressedDispatcher) {
                JetchatTheme {
                    Column {
                        Text(text = "按钮", modifier = Modifier.padding(100.dp)
                            .size(50.dp)
                            .clickable {
                                activityViewModel.openDrawer()
                            })
                    }
                }
            }
        }
    }
}