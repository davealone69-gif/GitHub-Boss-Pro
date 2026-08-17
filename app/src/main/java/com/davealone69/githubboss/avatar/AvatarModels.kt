package com.davealone69.githubboss.avatar

/**
 * Supported private-room environments for an AI avatar session.
 * Action prompts intentionally remain blank so the app can supply its own
 * interaction layer later.
 */
enum class AvatarRoom {
    BEDROOM,
    LOUNGE,
    FUTURISTIC_POD,
    NIGHTCLUB,
    FANTASY
}

data class AvatarAppearance(
    val avatarAssetId: String,
    val displayName: String,
    val description: String = "",
    val voiceId: String? = null,
    val personalityTags: List<String> = emptyList()
)

data class AvatarRoomState(
    val room: AvatarRoom,
    val lightingPreset: String = "default",
    val ambiencePreset: String = "default",
    val actionPrompt: String = ""
)

data class AvatarProfile(
    val id: String,
    val appearance: AvatarAppearance,
    val roomState: AvatarRoomState = AvatarRoomState(AvatarRoom.LOUNGE),
    val isAdultsOnly: Boolean = true
) {
    init {
        require(id.isNotBlank()) { "Avatar id must not be blank" }
        require(appearance.avatarAssetId.isNotBlank()) { "Avatar asset id must not be blank" }
        require(appearance.displayName.isNotBlank()) { "Avatar display name must not be blank" }
    }
}
