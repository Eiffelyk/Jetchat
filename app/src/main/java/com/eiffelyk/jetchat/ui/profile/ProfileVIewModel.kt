package com.eiffelyk.jetchat.ui.profile

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.eiffelyk.jetchat.data.meProfile

class ProfileVIewModel

@Immutable
data class ProfileScreenState(
    val userId: String,
    @DrawableRes val photo: Int?,
    val name: String,
    val status: String,
    val displayName: String,
    val position: String,
    val twitter: String = "",
    val timeZone: String?,
    val commonChannels: String?
) {
    fun isMe() = userId == meProfile.userId
}