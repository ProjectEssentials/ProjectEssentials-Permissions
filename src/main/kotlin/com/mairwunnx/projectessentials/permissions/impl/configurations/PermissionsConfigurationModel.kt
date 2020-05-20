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
                "native.messaging.me",
                "native.messaging.message",
                "native.messaging.say",
                "native.messaging.teammsg",
                "native.messaging.chat",
                "native.messaging.chat.mention",
                "native.team",
                "native.event.world.*",
                "ess.teleport.back",
                "ess.teleport.tpa",
                "ess.teleport.tpaccept",
                "ess.teleport.tpdeny",
                "ess.teleport.tpacancel",
                "ess.teleport.tptoggle",
                "ess.spawn.teleport",
                "ess.wapr.set",
                "ess.wapr.remove",
                "ess.wapr.teleport",
                "ess.wapr.list",
                "ess.home.set",
                "ess.home.remove",
                "ess.home.teleport",
                "ess.home.list",
                "ess.backup.notify",
                "ess.auth.login",
                "ess.auth.register",
                "ess.auth.recovery",
                "ess.ping.self",
                "ess.afk.self",
                "ess.sendpos",
                "^native.event.world.overworld.block.bedrock.place",
                "^native.event.world.overworld.block.bedrock.break"
            ), mutableListOf()
        ), Group("owner", false, mutableListOf("*"), mutableListOf(), "§c[§7owner§c]")
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
         * @since Mod: 2.0.0-RC.1
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
