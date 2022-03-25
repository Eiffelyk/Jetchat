package com.eiffelyk.jetchat.ui.conversation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eiffelyk.jetchat.FunctionalityNotAvailablePopup
import com.eiffelyk.jetchat.R
import com.eiffelyk.jetchat.components.JetchatAppBar
import com.eiffelyk.jetchat.data.exampleUiState
import com.eiffelyk.jetchat.ui.theme.JetchatTheme


@Composable
fun ChannelNameBar(
    channelName: String,
    channelMembers: Int,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = {}
) {
    var functionalityNotAvailablePopupShow by remember { mutableStateOf(false) }
    if (functionalityNotAvailablePopupShow) {
        FunctionalityNotAvailablePopup {
            functionalityNotAvailablePopupShow = false
        }
    }
    JetchatAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        onNavIconPressed = onNavIconPressed,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = channelName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(id = R.string.members, channelMembers),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(id = R.string.search),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = {
                        functionalityNotAvailablePopupShow = true
                    })
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp)
            )
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(id = R.string.info),
                modifier = Modifier
                    .clickable(onClick = {
                        functionalityNotAvailablePopupShow = true
                    })
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationContent(
    uiState: ConversationUiState,
    navigateToProfile: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNavIconPressed: () -> Unit = {}
) {
    val authorMe = stringResource(id = R.string.author_me)
    val timeShow = stringResource(id = R.string.now)
    val scrollState = rememberLazyListState()
    val scrollBehavior = remember {
        TopAppBarDefaults.pinnedScrollBehavior()
    }
    val scope = rememberCoroutineScope()
    Surface(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {

            }
            ChannelNameBar(
                channelName = uiState.channelName,
                channelMembers = uiState.channelMembers,
                onNavIconPressed = onNavIconPressed,
                scrollBehavior = scrollBehavior,
                modifier = Modifier.statusBarsPadding()
            )
        }
    }
}

@Preview
@Composable
fun ChannelBarPreview() {
    JetchatTheme {
        ChannelNameBar(channelName = "composers", channelMembers = 52)
    }
}

@Preview
@Composable
fun ConversationPreview() {
    JetchatTheme {
        ConversationContent(uiState = exampleUiState, navigateToProfile = {})
    }
}


