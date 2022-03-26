package com.eiffelyk.jetchat.ui.conversation

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eiffelyk.jetchat.FunctionalityNotAvailablePopup
import com.eiffelyk.jetchat.R
import com.eiffelyk.jetchat.components.JetchatAppBar
import com.eiffelyk.jetchat.data.exampleUiState
import com.eiffelyk.jetchat.ui.theme.JetchatTheme
import com.example.compose.jetchat.conversation.SymbolAnnotationType
import com.example.compose.jetchat.conversation.messageFormatter
import kotlinx.coroutines.launch


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
                Messages(uiState.messages, navigateToProfile, scrollState, Modifier.weight(1f))
                UserInput(
                    onMessageSent = { content ->
                        uiState.addMessage(
                            Message(
                                authorMe,
                                content,
                                timeShow
                            )
                        )
                    },
                    resetScroll = {
                        scope.launch {
                            scrollState.scrollToItem(0)
                        }
                    },
                    modifier = Modifier
                        .navigationBarsPadding()
                        .imePadding()
                )
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

const val ConversationTestTag = "ConversationTestTag"
private val JumpToBottomThreshold = 56.dp
private fun ScrollState.asBottom(): Boolean = value == 0

@Composable
fun Messages(
    messages: List<Message>,
    navigateToProfile: (String) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    Box(modifier = modifier) {
        val authorMe = stringResource(id = R.string.author_me)
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            contentPadding = WindowInsets.statusBars.add(
                WindowInsets(top = 90.dp)
            ).asPaddingValues(),
            modifier = Modifier
                .testTag(ConversationTestTag)
                .fillMaxSize()
        ) {
            for (index in messages.indices) {
                val prevAuthor = messages.getOrNull(index - 1)?.author
                val nextAuthor = messages.getOrNull(index + 1)?.author
                val content = messages[index]
                val isFirstMessageByAuthor = prevAuthor != content.author
                val isLastMessageByAuthor = nextAuthor != content.author
                if (index == messages.size - 1) {
                    item { DataHeader("20 Aug") }
                } else if (index == 2) {
                    item { DataHeader(dayString = "Today") }
                }
                item {
                    Message(
                        onAuthorClick = { name -> navigateToProfile(name) },
                        msg = content,
                        isUserMe = content.author == authorMe,
                        isFirstMessageByAuthor = isFirstMessageByAuthor,
                        isLastMessageByAuthor = isLastMessageByAuthor
                    )
                }
            }
        }
        val jumpThreshold = with(LocalDensity.current) {
            JumpToBottomThreshold.toPx()
        }
        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex != 0 || scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }
        JumpToBottom(
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch { scrollState.animateScrollToItem(0) }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun Message(
    onAuthorClick: (String) -> Unit,
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean
) {
    val borderColor =
        if (isUserMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
    val spaceBetweenAuthors = if (isLastMessageByAuthor) Modifier.padding(top = 8.dp) else Modifier
    Row(modifier = spaceBetweenAuthors) {
        if (isLastMessageByAuthor) {
            Image(
                painter = painterResource(id = msg.authorImage),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clickable(onClick = { onAuthorClick(msg.author) })
                    .padding(16.dp)
                    .size(42.dp)
                    .border(1.5.dp, borderColor, CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.Top)
            )
        } else {
            Spacer(modifier = Modifier.width(74.dp))
        }
        AuthorAndTextMessage(
            msg = msg,
            isUserMe = isUserMe,
            isFirstMessageByAuthor = isFirstMessageByAuthor,
            isLastMessageByAuthor = isLastMessageByAuthor,
            authorClicked = onAuthorClick,
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(1f)
        )
    }
}

@Composable
fun AuthorAndTextMessage(
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    authorClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (isLastMessageByAuthor) {
            AuthorNameAndTimestamp(msg)
        }
        ChatItemBubble(msg = msg, isUserMe = isUserMe, authorClicked = authorClicked)
        Spacer(modifier = Modifier.height(if (isFirstMessageByAuthor) 8.dp else 4.dp))
    }
}


@Composable
fun ChatItemBubble(msg: Message, isUserMe: Boolean, authorClicked: (String) -> Unit) {
    val chatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
    val background =
        if (isUserMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    Column {
        Surface(color = background, shape = chatBubbleShape) {
            ClickableMessage(msg = msg, isUserMe = isUserMe, authorClicked = authorClicked)
        }
        msg.image?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(color = background, shape = chatBubbleShape) {
                Image(
                    painter = painterResource(id = it),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(160.dp),
                    contentDescription = stringResource(
                        id = R.string.attach_photo_desc
                    )
                )
            }
        }
    }
}

@Composable
fun ClickableMessage(msg: Message, isUserMe: Boolean, authorClicked: (String) -> Unit) {
    val uriHandler = LocalUriHandler.current
    val styleMessage = messageFormatter(text = msg.content, primary = isUserMe)
    ClickableText(
        text = styleMessage,
        modifier = Modifier.padding(16.dp),
        style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
        onClick = {
            styleMessage.getStringAnnotations(start = it, end = it).firstOrNull()
                ?.let { annotation ->
                    when (annotation.tag) {
                        SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                        SymbolAnnotationType.PERSON.name -> authorClicked(annotation.item)
                        else -> Unit
                    }
                }
        }
    )
}

@Composable
fun AuthorNameAndTimestamp(msg: Message) {
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Text(
            text = msg.author,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .alignBy(
                    LastBaseline
                )
                .paddingFrom(LastBaseline, after = 8.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = msg.timestamp,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alignBy(
                LastBaseline
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DataHeader(dayString: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(16.dp)
    ) {
        DayHeaderLine()
        Text(
            text = dayString,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        DayHeaderLine()
    }
}

@Composable
private fun RowScope.DayHeaderLine() {
    Divider(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}


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


