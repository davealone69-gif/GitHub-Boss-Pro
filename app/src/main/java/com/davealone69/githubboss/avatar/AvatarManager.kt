package com.davealone69.githubboss.avatar

/**
 * Small dependency-free manager for prepared AI avatars.
 * Persistence and the AI provider can be connected later without changing
 * the room/avatar model.
 */
class AvatarManager(
    initialAvatars: List<AvatarProfile> = emptyList()
) {
    private val avatars = initialAvatars.associateBy { it.id }.toMutableMap()
    private var activeAvatarId: String? = null

    fun upsert(avatar: AvatarProfile) {
        avatars[avatar.id] = avatar
    }

    fun remove(avatarId: String) {
        avatars.remove(avatarId)
        if (activeAvatarId == avatarId) activeAvatarId = null
    }

    fun get(avatarId: String): AvatarProfile? = avatars[avatarId]

    fun all(): List<AvatarProfile> = avatars.values.sortedBy { it.appearance.displayName }

    fun activate(avatarId: String): AvatarProfile {
        val avatar = requireNotNull(avatars[avatarId]) { "Unknown avatar: $avatarId" }
        activeAvatarId = avatar.id
        return avatar
    }

    fun active(): AvatarProfile? = activeAvatarId?.let(avatars::get)

    fun moveToRoom(
        avatarId: String,
        room: AvatarRoom,
        lightingPreset: String = "default",
        ambiencePreset: String = "default"
    ): AvatarProfile {
        val avatar = requireNotNull(avatars[avatarId]) { "Unknown avatar: $avatarId" }
        val updated = avatar.copy(
            roomState = avatar.roomState.copy(
                room = room,
                lightingPreset = lightingPreset,
                ambiencePreset = ambiencePreset,
                actionPrompt = ""
            )
        )
        avatars[avatarId] = updated
        return updated
    }

    /**
     * Stores a caller-supplied action prompt. The model itself never invents
     * or executes an action prompt.
     */
    fun setActionPrompt(avatarId: String, prompt: String): AvatarProfile {
        val avatar = requireNotNull(avatars[avatarId]) { "Unknown avatar: $avatarId" }
        val updated = avatar.copy(roomState = avatar.roomState.copy(actionPrompt = prompt))
        avatars[avatarId] = updated
        return updated
    }
}
