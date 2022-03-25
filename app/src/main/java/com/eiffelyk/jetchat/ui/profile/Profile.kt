package com.eiffelyk.jetchat.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eiffelyk.jetchat.FunctionalityNotAvailablePopup
import com.eiffelyk.jetchat.R
import com.eiffelyk.jetchat.components.AnimatingFabContent
import com.eiffelyk.jetchat.components.JetchatAppBar
import com.eiffelyk.jetchat.components.baselineHeight
import com.eiffelyk.jetchat.ui.theme.JetchatTheme

@Composable
fun ProfileError() {
    Text(text = stringResource(id = R.string.profile_error))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(userData: ProfileScreenState, onNavIconPressed: () -> Unit = {}) {
    var funNotAvailablePopupShow by remember {
        mutableStateOf(false)
    }

    if (funNotAvailablePopupShow) {
        FunctionalityNotAvailablePopup {
            funNotAvailablePopupShow = false
        }
    }

    val scrollState = rememberScrollState()
    val scrollBehavior = remember {
        TopAppBarDefaults.pinnedScrollBehavior()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        JetchatAppBar(
            modifier = Modifier.statusBarsPadding(),
            scrollBehavior = scrollBehavior,
            onNavIconPressed = onNavIconPressed,
            title = {},
            actions = {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = stringResource(id = R.string.more_options),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clickable(onClick = { funNotAvailablePopupShow = true })
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                        .height(24.dp)
                )
            }
        )
        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    ProfileHeader(scrollState, userData, this@BoxWithConstraints.maxHeight)
                    UserInfoFields(userData = userData, this@BoxWithConstraints.maxHeight)
                }
            }
            ProfileFab(extended = scrollState.value == 0,
                userIsMe = userData.isMe(),
                modifier = Modifier.align(
                    Alignment.BottomEnd
                ),
                onFabClicked = { funNotAvailablePopupShow = true })
        }
    }
}

@Composable
fun UserInfoFields(userData: ProfileScreenState, containerHeight: Dp) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        NameAndPosition(userData)
        ProfileProperty(stringResource(id = R.string.display_name), userData.displayName)
        ProfileProperty(stringResource(id = R.string.status), userData.status)
        ProfileProperty(stringResource(id = R.string.twitter), userData.twitter, true)
        userData.timeZone?.let {
            ProfileProperty(stringResource(id = R.string.timezone), userData.displayName)
        }
        Spacer(modifier = Modifier.height((containerHeight - 320.dp).coerceAtLeast(0.dp)))
    }
}

@Composable
fun ProfileProperty(label: String, value: String, isLink: Boolean = false) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Divider()
        Text(
            text = label,
            modifier = Modifier.baselineHeight(24.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        val style = if (isLink) {
            MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
        } else {
            MaterialTheme.typography.bodyLarge
        }
        Text(text = value, modifier = Modifier.baselineHeight(24.dp), style = style)
    }
}

@Composable
fun NameAndPosition(userData: ProfileScreenState) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Name(userData, modifier = Modifier.baselineHeight(32.dp))
        Position(
            userData = userData,
            modifier = Modifier
                .padding(bottom = 20.dp)
                .baselineHeight(24.dp)
        )
    }
}

@Composable
fun Position(userData: ProfileScreenState, modifier: Modifier = Modifier) {
    Text(
        text = userData.position, modifier = modifier,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun Name(userData: ProfileScreenState, modifier: Modifier = Modifier) {
    Text(text = userData.name, modifier = modifier, style = MaterialTheme.typography.headlineSmall)
}

@Composable
fun ProfileHeader(scrollState: ScrollState, data: ProfileScreenState, containerHeight: Dp) {
    val offset = (scrollState.value / 2)
    val offsetDp = with(LocalDensity.current) {
        offset.toDp()
    }
    data.photo?.let {
        Image(
            painter = painterResource(id = it),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .heightIn(max = containerHeight / 2)
                .fillMaxWidth()
                .padding(start = 16.dp, top = offsetDp, end = 16.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun ProfileFab(
    extended: Boolean,
    userIsMe: Boolean,
    modifier: Modifier = Modifier,
    onFabClicked: () -> Unit = {}
) {
    key(userIsMe) {
        FloatingActionButton(
            onClick = onFabClicked,
            modifier = modifier
                .padding(16.dp)
                .navigationBarsPadding()
                .height(48.dp)
                .widthIn(min = 48.dp)
        ) {
            AnimatingFabContent(
                icon = {
                    Icon(
                        imageVector = if (userIsMe) Icons.Outlined.Create else Icons.Outlined.Chat,
                        contentDescription = stringResource(id = if (userIsMe) R.string.edit_profile else R.string.message)
                    )
                },
                text = { Text(text = stringResource(id = if (userIsMe) R.string.edit_profile else R.string.message)) },
                extended = extended
            )
        }
    }
}

@Preview
@Composable
fun ProfileFabPreview1() {
    JetchatTheme {
        ProfileFab(extended = true, userIsMe = false)
    }
}

@Preview
@Composable
fun ProfileFabPreview2() {
    JetchatTheme {
        ProfileFab(extended = true, userIsMe = true)
    }
}
