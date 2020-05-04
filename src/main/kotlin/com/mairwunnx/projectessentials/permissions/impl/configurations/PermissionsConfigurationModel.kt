package com.mairwunnx.projectessentials.permissions.impl.configurations

import com.mairwunnx.projectessentials.core.api.v1.extensions.empty
import kotlinx.serialization.Serializable

/**
 * Base permission data class, it store
 * list of all registered groups and storing
 * list of all registered users.
 * @since 1.14.4-0.1.0.0
 */
@Serializable
data class PermissionsConfigurationModel(
    /**
     * stores all registered groups.
     */
    val groups: MutableList<Group> = mutableListOf(
        Group(
            "default", true, mutableListOf(
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
            ), mutableListOf()
        ), Group("owner", false, mutableListOf("*"), mutableListOf("default"), "§c[§7owner§c]")
    ),
    /**
     * stores all registered users.
     */
    val users: MutableList<User> = mutableListOf(
        User("*", "default", mutableListOf()),
        User("#server", "owner", mutableListOf())
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
        val permissions: MutableList<String> = mutableListOf(),
        /**
         * stores groups whose permissions must
         * inherit it group.
         * @since 1.14.4-1.0.0.0
         */
        val inheritFrom: MutableList<String> = mutableListOf(),
        /**
         * Group message prefix for user.
         * @since Mod: 2.0.0-SNAPSHOT.1, API: 1.0.0
         */
        var prefix: String = String.empty
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
        val permissions: MutableList<String> = mutableListOf()
    )
}
