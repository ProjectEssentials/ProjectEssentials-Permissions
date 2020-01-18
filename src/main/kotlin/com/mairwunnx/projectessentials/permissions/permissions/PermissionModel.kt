package com.mairwunnx.projectessentials.permissions.permissions

import com.mairwunnx.projectessentials.core.extensions.empty
import kotlinx.serialization.Serializable

/**
 * Base permission data class, it store
 * list of all registered groups and storing
 * list of all registered users.
 * @since 1.14.4-0.1.0.0
 */
@Serializable
data class PermissionModel(
    /**
     * stores all registered groups.
     */
    var groups: List<Group> = listOf(
        Group(
            "default", true, listOf(
                "native.help",
                "native.list",
                "native.me",
                "native.message",
                "native.teammsg",
                "native.tell",
                "native.trigger",
                "native.w",
                "native.event.modifyworld",
                "ess.warp",
                "ess.warp.set",
                "ess.warp.remove",
                "ess.chat",
                "ess.chat.mention.all",
                "ess.home",
                "ess.home.set",
                "ess.home.remove",
                "ess.spawn",
                "ess.afk",
                "ess.sendpos",
                "ess.tpaccept",
                "ess.tpdeny",
                "ess.tptoggle",
                "ess.tpaall",
                "ess.tpacancel",
                "ess.tpa",
                "ess.tpahere"
            ), listOf()
        ),
        Group("owner", false, listOf("*"), listOf("default"))
    ),
    /**
     * stores all registered users.
     */
    var users: List<User> = listOf(
        User("*", "default", emptyList())
    )
) {
    /**
     * Base group data class, it store:
     * group name, default state, list of permissions.
     */
    @Serializable
    data class Group(
        /**
         * name of group.
         */
        var name: String = String.empty,
        /**
         * stores state is default group
         * for new users.
         */
        var isDefault: Boolean = false,
        /**
         * stores all group permissions.
         */
        var permissions: List<String> = emptyList(),
        /**
         * stores groups whose permissions must
         * inherit it group.
         * @since 1.14.4-1.0.0.0
         */
        var inheritFrom: List<String> = emptyList()
    )

    /**
     * Base user data class, it store:
     * uuid of user, user group, list of
     * additional permissions.
     */
    @Serializable
    data class User(
        /**
         * player nickname.
         */
        var nickname: String = String.empty,
        /**
         * user group.
         */
        var group: String = String.empty,
        /**
         * stores all user additional permissions.
         */
        var permissions: List<String> = emptyList()
    )
}
