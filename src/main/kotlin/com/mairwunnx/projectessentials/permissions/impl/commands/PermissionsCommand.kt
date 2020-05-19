package com.mairwunnx.projectessentials.permissions.impl.commands

import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.extensions.empty
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.extensions.playerName
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.permissions.api.v1.PermissionsAPI
import com.mairwunnx.projectessentials.permissions.impl.configurations.PermissionsConfiguration
import com.mairwunnx.projectessentials.permissions.impl.configurations.PermissionsConfigurationModel
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager

object PermissionsCommand : CommandBase(
    takePermissionsLiteral(), false
) {
    override val name = "permissions"
    override val aliases = listOf("perm", "perms", "permission")

    private val logger = LogManager.getLogger()
    private val marker = MarkerManager.Log4jMarker("COMMAND OUT")

    private val permissionsConfiguration by lazy {
        getConfigurationByName<PermissionsConfiguration>("permissions")
    }

    internal fun save(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            permissionsConfiguration.save().run {
                when {
                    isServer -> ServerMessagingAPI.response { "Permissions configuration saved." }
                    else -> sendResultMessage(context, "save", "success")
                }
            }
        }
        validate(context, "ess.permissions.store.save", 4, ::action) { "execute" }
        return 0
    }

    internal fun reload(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            permissionsConfiguration.load().run {
                when {
                    isServer -> ServerMessagingAPI.response { "Permissions configuration reloaded." }
                    else -> sendResultMessage(context, "reload", "success")
                }
            }
        }
        validate(context, "ess.permissions.store.reload", 4, ::action) { "execute" }
        return 0
    }

    internal fun userInfo(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            CommandAPI.getString(context, "user-name").also { user ->
                if (isServer) {
                    ServerMessagingAPI.response {
                        """
Permissions information about user: $user
    > Group: ${PermissionsAPI.getUserGroup(user)}
    > Permissions count: ${PermissionsAPI.getUserPermissions(user, false).count()}
    > Inherited all permissions count: ${PermissionsAPI.getUserPermissions(user, true).count()}
    > Inherited message prefix: ${PermissionsAPI.getGroupPrefix(PermissionsAPI.getUserGroup(user))}
    > Has operator permission: ${PermissionsAPI.hasPermission(user, "*")}
    > Has native operator access: ${user in context.source.server.playerList.oppedPlayerNames}
                        """
                    }
                } else {
                    sendResultMessage(
                        context, "user.read.info", "success",
                        user,
                        PermissionsAPI.getUserGroup(user),
                        PermissionsAPI.getUserPermissions(user, false).count().toString(),
                        PermissionsAPI.getUserPermissions(user, true).count().toString(),
                        PermissionsAPI.getGroupPrefix(PermissionsAPI.getUserGroup(user)),
                        PermissionsAPI.hasPermission(user, "*").toString(),
                        (user in context.source.server.playerList.oppedPlayerNames).toString()
                    )
                }
            }
        }
        validate(context, "ess.permissions.user.read.info", 3, ::action) { "execute" }
        return 0
    }

    internal fun userRemove(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            val user = CommandAPI.getString(context, "user-name")
            val result = PermissionsAPI.removeUser(user)
            if (result) {
                when {
                    isServer -> ServerMessagingAPI.response {
                        "User $user was removed from permissions and refreshed to default group."
                    }
                    else -> sendResultMessage(context, "user.remove", "success", user)
                }
            } else {
                when {
                    isServer -> ServerMessagingAPI.response {
                        "User $user was not removed from permissions because he does not have special privileges."
                    }
                    else -> sendResultMessage(context, "user.remove", "error", user)
                }
            }
        }
        validate(context, "ess.permissions.user.remove", 3, ::action) { "execute" }
        return 0
    }

    internal fun userPermissionsAdd(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            val user = CommandAPI.getString(context, "user-name")
            val node = CommandAPI.getString(context, "node")
            PermissionsAPI.addUserPermission(user, node).run {
                when {
                    isServer -> ServerMessagingAPI.response {
                        "Permission node `$node` was added to user $user."
                    }
                    else -> sendResultMessage(
                        context, "user.permissions.add", "success", node, user
                    )
                }
            }
        }
        validate(context, "ess.permissions.user.permissions.add", 3, ::action) { "execute" }
        return 0
    }

    internal fun userPermissionsRemove(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            val user = CommandAPI.getString(context, "user-name")
            val node = CommandAPI.getString(context, "node")
            if (PermissionsAPI.removeUserPermission(user, node)) {
                when {
                    isServer -> ServerMessagingAPI.response {
                        "Permission node `$node` was removed at user $user."
                    }
                    else -> sendResultMessage(
                        context, "user.permissions.remove", "success", node, user
                    )
                }
            } else {
                when {
                    isServer -> somethingIsWrong(
                        listOf(
                            "User with name $user not exist",
                            "User $user not have registered permission node $node"
                        ),
                        mapOf(
                            "Get user permissions list" to "/perm user permissions <user-name> list"
                        )
                    ) {
                        "Permission node $node was not removed from user $user"
                    }
                    else -> sendResultMessage(
                        context, "user.permissions.remove", "error", user, node
                    )
                }
            }
        }
        validate(context, "ess.permissions.user.permissions.remove", 3, ::action) { "execute" }
        return 0
    }

    internal fun userPermissionsList(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            val user = CommandAPI.getString(context, "user-name")
            val deepExist = CommandAPI.getBoolExisting(context, "deep")
            val isDeep = if (deepExist) CommandAPI.getBool(context, "deep") else false
            val result = PermissionsAPI.getUserPermissions(user, isDeep)
            if (isServer) {
                if (result.isEmpty()) {
                    ServerMessagingAPI.response {
                        "Requested permissions list is empty, nothing to listing you."
                    }
                } else {
                    // @formatter:off
                    ServerMessagingAPI.listAsResponse(result.toList()) {
                        """
Permissions of user $user ${if (isDeep) { "with deep permissions" } else { "" }}
                        """.trimIndent()
                    }.run { executed(context) }
                    // @formatter:on
                }
            } else {
                if (result.isEmpty()) {
                    sendResultMessage(context, "user.permissions.list", "empty")
                } else {
                    // @formatter:off
                    MessagingAPI.sendListAsMessage(context, result.toList()) {
                        """
Permissions of user $user ${if (isDeep) { "with deep permissions" } else { "" }}
                        """.trimIndent()
                    }.run { executed(context) }
                    // @formatter:on
                }
            }
        }
        validate(context, "ess.permissions.user.read.permissions", 3, ::action) { "execute" }
        return 0
    }

    internal fun userSetGroup(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            val user = CommandAPI.getString(context, "user-name")
            val group = CommandAPI.getString(context, "group-name")
            val result = PermissionsAPI.setUserGroup(user, group)
            if (result) {
                if (isServer) {
                    ServerMessagingAPI.response {
                        "New group `$group` was setted up for user `$user`."
                    }
                } else {
                    sendResultMessage(context, "user.group.set", "success", group, user)
                }
            } else {
                if (isServer) {
                    somethingIsWrong(
                        listOf(
                            "Group with name `$group` not exist.",
                            "User `$user` already have a group `$group`."
                        ),
                        mapOf(
                            "Get user group and other info" to "/perm user info <user-name>",
                            "Get registered group list" to "/perm group list"
                        )
                    ) {
                        "Configuring $user group failed."
                    }
                } else {
                    sendResultMessage(context, "user.group.set", "error", group, user)
                }
            }
        }
        validate(context, "ess.permissions.user.group.set", 3, ::action) { "execute" }
        return 0
    }

    internal fun groupList(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            val groups = PermissionsAPI.getGroups().map { it.name }
            if (isServer) {
                if (groups.isEmpty()) {
                    ServerMessagingAPI.response {
                        "Requested groups list is empty, nothing to listing you."
                    }
                } else {
                    ServerMessagingAPI.listAsResponse(groups) {
                        "Requested full groups list"
                    }
                }
            } else {
                if (groups.isEmpty()) {
                    sendResultMessage(context, "group.list", "empty")
                } else {
                    MessagingAPI.sendListAsMessage(context, groups) {
                        "Groups list"
                    }.run { executed(context) }
                }
            }
        }
        validate(context, "ess.permissions.group.read.list", 3, ::action) { "execute" }
        return 0
    }

    internal fun groupDefaultSet(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            val group = CommandAPI.getString(context, "group-name")
            val result = PermissionsAPI.defaultFactorMove(
                group, PermissionsAPI.getDefaultGroup().name
            )
            if (isServer) {
                if (result) {
                    ServerMessagingAPI.response {
                        "Default group factor changed for group $group."
                    }
                } else {
                    somethingIsWrong(
                        listOf("Group with name $group probably not exist."),
                        mapOf("Get registered group list" to "/perm group list")
                    ) {
                        "Can't set default group $group"
                    }
                }
            } else {
                if (result) {
                    sendResultMessage(context, "group.default.set", "success", group)
                } else {
                    sendResultMessage(context, "group.default.set", "error", group)
                }
            }
        }
        validate(context, "ess.permissions.group.default.set", 3, ::action) { "execute" }
        return 0
    }

    internal fun groupCreate(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            val group = CommandAPI.getString(context, "group-name")
            val result = PermissionsAPI.addGroup(PermissionsConfigurationModel.Group(group))
            if (isServer) {
                if (result) {
                    ServerMessagingAPI.response {
                        "Group with name $group created, but new group needs to configure."
                    }
                } else {
                    somethingIsWrong(
                        listOf("Group with name $group already exist"),
                        mapOf("Get registered group list" to "/perm group list")
                    ) {
                        "Group with name $group not created"
                    }
                }
            } else {
                if (result) {
                    sendResultMessage(context, "group.create", "success", group)
                } else {
                    sendResultMessage(context, "group.create", "error", group)
                }
            }
        }
        validate(context, "ess.permissions.group.create", 3, ::action) { "execute" }
        return 0
    }

    internal fun groupRemove(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            val group = CommandAPI.getString(context, "group-name")
            val result = PermissionsAPI.removeGroup(group)
            if (isServer) {
                if (result) {
                    ServerMessagingAPI.response {
                        "Group with name $group removed."
                    }
                } else {
                    somethingIsWrong(
                        listOf("Group with name $group not exist"),
                        mapOf("Get registered group list" to "/perm group list")
                    ) {
                        "Group with name $group was not removed"
                    }
                }
            } else {
                if (result) {
                    sendResultMessage(context, "group.remove", "success", group)
                } else {
                    sendResultMessage(context, "group.remove", "error", group)
                }
            }
        }
        validate(context, "ess.permissions.group.remove", 3, ::action) { "execute" }
        return 0
    }

    internal fun groupPermissionsAdd(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            val group = CommandAPI.getString(context, "group-name")
            val node = CommandAPI.getString(context, "node")
            PermissionsAPI.addGroupPermission(group, node).run {
                if (isServer) {
                    ServerMessagingAPI.response {
                        "Permission node `$node` was added to group $group."
                    }
                } else {
                    sendResultMessage(context, "group.permissions.add", "success", node, group)
                }
            }
        }
        validate(context, "ess.permissions.group.permissions.add", 3, ::action) { "execute" }
        return 0
    }

    internal fun groupPermissionsRemove(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            val group = CommandAPI.getString(context, "group-name")
            val node = CommandAPI.getString(context, "node")
            val result = PermissionsAPI.removeGroupPermission(group, node)
            if (isServer) {
                if (result) {
                    ServerMessagingAPI.response {
                        "Permission node `$node` was removed from group $group."
                    }
                } else {
                    somethingIsWrong(
                        listOf(
                            "Requested group with name $group not exist",
                            "Requested permission node to remove $node not registered in group"
                        ),
                        mapOf(
                            "Get registered group list" to "/perm group list",
                            "Get group permissions list" to "/perm group permissions <group> list"
                        )
                    ) {
                        "Permission node removing failed"
                    }
                }
            } else {
                if (result) {
                    sendResultMessage(context, "group.permissions.remove", "success", node, group)
                } else {
                    sendResultMessage(context, "group.permissions.remove", "error", node, group)
                }
            }
        }
        validate(context, "ess.permissions.group.permissions.remove", 3, ::action) { "execute" }
        return 0
    }

    internal fun groupPermissionsList(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            val group = CommandAPI.getString(context, "group-name")
            val deepExist = CommandAPI.getBoolExisting(context, "deep")
            val isDeep = if (deepExist) CommandAPI.getBool(context, "deep") else false
            val result = PermissionsAPI.getGroupPermissions(group, isDeep)
            if (isServer) {
                if (result.isEmpty()) {
                    ServerMessagingAPI.response {
                        "Requested permissions list is empty, nothing to listing you."
                    }
                } else {
                    // @formatter:off
                    ServerMessagingAPI.listAsResponse(result.toList()) {
                        """
Permissions of group $group ${if (isDeep) { "with deep permissions" } else { "" }}
                        """.trimIndent()
                    }.run { executed(context) }
                    // @formatter:on
                }
            } else {
                if (result.isEmpty()) {
                    sendResultMessage(context, "group.permissions.list", "empty")
                } else {
                    // @formatter:off
                    MessagingAPI.sendListAsMessage(context, result.toList()) {
                        """
Permissions of group $group ${if (isDeep) { "with deep permissions" } else { "" }}
                        """.trimIndent()
                    }.run { executed(context) }
                    // @formatter:on
                }
            }
        }
        validate(context, "ess.permissions.group.read.permissions", 3, ::action) { "execute" }
        return 0
    }

    internal fun groupInheritAdd(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            val group = CommandAPI.getString(context, "group-name")
            val inherit = CommandAPI.getString(context, "inherit-group")
            val result = PermissionsAPI.addGroupInheritances(group, inherit)
            if (isServer) {
                if (result) {
                    ServerMessagingAPI.response {
                        "New inherit group $inherit was added to group $group."
                    }
                } else {
                    somethingIsWrong(
                        listOf("Requested group with name $group not exist"),
                        mapOf("Get registered group list" to "/perm group list")
                    ) {
                        "Can't add inherit groups to group $group"
                    }
                }
            } else {
                if (result) {
                    sendResultMessage(context, "group.inherit.add", "success", inherit, group)
                } else {
                    sendResultMessage(context, "group.inherit.add", "error", group)
                }
            }
        }
        validate(context, "ess.permissions.group.inherit.add", 3, ::action) { "execute" }
        return 0
    }

    internal fun groupInheritRemove(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            val group = CommandAPI.getString(context, "group-name")
            val inherit = CommandAPI.getString(context, "inherit-group")
            val result = PermissionsAPI.removeGroupInheritances(group, inherit)
            if (isServer) {
                if (result) {
                    ServerMessagingAPI.response {
                        "Inherit group $inherit was removed from group $group."
                    }
                } else {
                    somethingIsWrong(
                        listOf("Requested group with name $group not exist"),
                        mapOf("Get registered group list" to "/perm group list")
                    ) {
                        "Can't remove any inherit group from group $group"
                    }
                }
            } else {
                if (result) {
                    sendResultMessage(context, "group.inherit.remove", "success", inherit, group)
                } else {
                    sendResultMessage(context, "group.inherit.remove", "error", group)
                }
            }
        }
        validate(context, "ess.permissions.group.inherit.remove", 3, ::action) { "execute" }
        return 0
    }

    internal fun groupInheritList(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            val group = CommandAPI.getString(context, "group-name")
            val result = PermissionsAPI.getGroupInherits(group, false)
            if (isServer) {
                if (result.isEmpty()) {
                    ServerMessagingAPI.response {
                        "Group $group has not childrens. *joke about the condom*"
                    }
                } else {
                    ServerMessagingAPI.listAsResponse(result.toList()) {
                        "Inherits group list for group $group"
                    }
                }
            } else {
                if (result.isEmpty()) {
                    sendResultMessage(context, "group.inherit.list", "empty")
                } else {
                    // @formatter:off
                    MessagingAPI.sendListAsMessage(context, result.toList()) {
                        """
Inherits group list for group $group
                        """.trimIndent()
                    }.run { executed(context) }
                    // @formatter:on
                }
            }
        }
        validate(context, "ess.permissions.group.read.inherit", 3, ::action) { "execute" }
        return 0
    }

    internal fun groupPrefixTake(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            val group = CommandAPI.getString(context, "group-name")
            PermissionsAPI.getGroupPrefix(group).also { result ->
                when {
                    isServer -> ServerMessagingAPI.response {
                        "Group $group prefix is `$result` or formatted `${result.replace(
                            Regex("§."),
                            ""
                        )}`"
                    }
                    else -> sendResultMessage(
                        context, "group.prefix.take", "success",
                        result, result.replace(Regex("§."), "")
                    )
                }
            }
        }
        validate(context, "ess.permissions.group.read.prefix", 3, ::action) { "execute" }
        return 0
    }

    internal fun groupPrefixSet(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            val group = CommandAPI.getString(context, "group-name")
            val prefix = CommandAPI.getString(context, "prefix")
            val oldPrefix = PermissionsAPI.getGroupPrefix(group)
            val result = PermissionsAPI.setGroupPrefix(group, prefix)
            if (result) {
                if (isServer) {
                    ServerMessagingAPI.response {
                        "Prefix for group $group was changed to `$prefix` from `$oldPrefix`"
                    }
                } else {
                    sendResultMessage(
                        context, "group.prefix.set", "success",
                        group, prefix, oldPrefix
                    )
                }
            } else {
                if (isServer) {
                    somethingIsWrong(
                        listOf("Group with name $group probably not exist."),
                        mapOf("Get registered group list" to "/perm group list")
                    ) {
                        "Can't set prefix for group $group"
                    }
                } else {
                    sendResultMessage(context, "group.prefix.set", "error", group)
                }
            }
        }
        validate(context, "ess.permissions.group.prefix.set", 3, ::action) { "execute" }
        return 0
    }

    private fun sendResultMessage(
        context: CommandContext<CommandSource>, action: String, result: String, vararg args: String
    ) {
        MessagingAPI.sendMessage(
            context.getPlayer()!!,
            "${MESSAGE_MODULE_PREFIX}permissions.perm.$action.$result",
            args = *args
        ).run {
            if (result == "success") executed(context)
        }
    }

    private fun executed(context: CommandContext<CommandSource>) =
        logger.debug(
            marker, " :: Executed command ${context.input} by ${context.playerName()}"
        )

    private fun validate(
        context: CommandContext<CommandSource>,
        node: String,
        opLevel: Int,
        action: (isServer: Boolean) -> Unit,
        failedActionName: () -> String
    ) = context.getPlayer()?.let {
        if (hasPermission(it, node, opLevel)) {
            action(false)
        } else {
            sendResultMessage(context, failedActionName(), "restricted")
        }
    } ?: run { action(true) }

    private fun somethingIsWrong(
        possibleReasons: List<String> = emptyList(),
        notes: Map<String, String> = emptyMap(),
        wrongMessage: () -> String
    ) {
        fun notes() = if (notes.isNotEmpty()) {
            notes.map { "${it.key}:\n    - ${it.value}" }.joinToString(separator = "\n\n")
        } else {
            String.empty
        }

        fun reasons() = if (possibleReasons.isNotEmpty()) {
            """

Possible reasons:
${possibleReasons.joinToString(separator = "\n") { "    - $it" }}

            """
        } else {
            "\n"
        }

        ServerMessagingAPI.response {
            """
${wrongMessage()}
${reasons()}
${notes()}
            """
        }
    }
}
