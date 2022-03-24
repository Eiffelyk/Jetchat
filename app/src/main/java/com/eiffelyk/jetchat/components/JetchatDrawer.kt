package com.eiffelyk.jetchat.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eiffelyk.jetchat.R
import com.eiffelyk.jetchat.data.colleagueProfile
import com.eiffelyk.jetchat.data.meProfile
import com.eiffelyk.jetchat.ui.theme.JetchatTheme

@Composable
fun ColumnScope.JetchatDrawer(onProfileClicked: (String) -> Unit, onChatClicked: (String) -> Unit) {
    Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
    DrawerHeader()
    DividerItem()
    DrawerItemHeader("Chats")
    ChatItem("composers", true) { onChatClicked("composers") }
    ChatItem("droidcon-nyc", false) { onChatClicked("droidcon-nyc") }
    DividerItem(modifier = Modifier.padding(horizontal = 28.dp))
    DrawerItemHeader(text = "Recent Profiles")
    ProfileItem(text = "Ali Conors(you)", profilePic = meProfile.photo) {
        onProfileClicked(meProfile.userId)
    }
    ProfileItem(text = "Taylor Brooks", profilePic = colleagueProfile.photo) {
        onProfileClicked(colleagueProfile.userId)
    }
}

@Composable
fun ProfileItem(text: String, @DrawableRes profilePic: Int?, onProfileClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(
                CircleShape
            )
            .clickable(onClick = onProfileClicked),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val paddingSizeModifier = Modifier
            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
            .size(24.dp)
        if (profilePic != null) {
            Image(
                painter = painterResource(id = profilePic),
                contentDescription = null,
                modifier = paddingSizeModifier.then(
                    Modifier.clip(
                        CircleShape
                    )
                ),
                contentScale = ContentScale.Crop,
            )
        } else {
            Spacer(modifier = paddingSizeModifier)
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
fun ChatItem(text: String, selected: Boolean, onChatClicked: () -> Unit) {
    val background = if (selected) {
        Modifier.background(MaterialTheme.colorScheme.primaryContainer)
    } else {
        Modifier
    }
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(
                CircleShape
            )
            .then(background)
            .clickable(onClick = onChatClicked),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconTint = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_jetchat),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
        )
        Text(
            text = text, style = MaterialTheme.typography.bodyMedium, color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }, modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
fun DrawerItemHeader(text: String) {
    Box(
        modifier = Modifier
            .heightIn(min = 52.dp)
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DividerItem(modifier: Modifier = Modifier) {
    Divider(
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

@Composable
fun DrawerHeader() {
    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        JetchatIcon(contentDescription = null, modifier = Modifier.size(24.dp))
        Image(
            painter = painterResource(id = R.drawable.jetchat_logo),
            contentDescription = null,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview
@Composable
fun DrawerPreview() {
    JetchatTheme {
        Surface {
            Column {
                JetchatDrawer(onProfileClicked = {}, onChatClicked = {})
            }
        }
    }
}

@Preview
@Composable
fun DrawerPreviewDark() {
    JetchatTheme(darkTheme = true) {
        Surface {
            Column {
                JetchatDrawer(onProfileClicked = {}, onChatClicked = {})
            }
        }
    }
}
