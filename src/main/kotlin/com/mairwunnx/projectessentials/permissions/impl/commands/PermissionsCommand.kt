package com.mairwunnx.projectessentials.permissions.impl.commands

import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.Command
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mairwunnx.projectessentials.permissions.api.v1.PermissionsAPI
import com.mairwunnx.projectessentials.permissions.impl.configurations.PermissionsConfiguration
import com.mairwunnx.projectessentials.permissions.impl.configurations.PermissionsConfigurationModel
import com.mairwunnx.projectessentials.permissions.impl.configurations.PermissionsSettingsConfiguration
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.util.text.StringTextComponent

@Command("permissions", ["perm", "perms", "permission"])
object PermissionsCommand : CommandBase(
    takePermissionsLiteral(), false
) {
    private val generalConfiguration by lazy {
        getConfigurationByName<GeneralConfiguration>("general")
    }
    private val permissionsConfiguration by lazy {
        getConfigurationByName<PermissionsConfiguration>("permissions")
    }
    private val permissionsSettingsConfiguration by lazy {
        getConfigurationByName<PermissionsSettingsConfiguration>("permissions-settings")
    }

    init {
        data = getData(this.javaClass)
    }

    /*
        This is a correction of the problem in order to get the list
        of settings in the configuration several times, because for
        the first time it is empty.
     */
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        this.literal = takePermissionsLiteral()
        super.register(dispatcher)
    }

    internal fun save(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            permissionsConfiguration.save().run {
                when {
                    isServer -> ServerMessagingAPI.response("Permissions configuration saved.")
                    else -> MessagingAPI.sendMessage(
                        context.getPlayer()!!,
                        "${MESSAGE_MODULE_PREFIX}permissions.perm.save.success",
                        generalConfiguration.getBool(SETTING_LOC_ENABLED)
                    )
                }
            }
        }

        validate(context, "ess.permissions.store.save", 4, ::action) {
            "${MESSAGE_MODULE_PREFIX}permissions.perm.save.restricted"
        }
        return 0
    }

    internal fun reload(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            permissionsConfiguration.load().run {
                when {
                    isServer -> ServerMessagingAPI.response("Permissions configuration reloaded.")
                    else -> MessagingAPI.sendMessage(
                        context.getPlayer()!!,
                        "${MESSAGE_MODULE_PREFIX}permissions.perm.reload.success",
                        generalConfiguration.getBool(SETTING_LOC_ENABLED)
                    )
                }
            }
        }

        validate(context, "ess.permissions.store.reload", 4, ::action) {
            "${MESSAGE_MODULE_PREFIX}permissions.perm.reload.restricted"
        }
        return 0
    }

    internal fun infoUser(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            CommandAPI.getString(context, "user-name").also { user ->
                if (isServer) {
                    ServerMessagingAPI.response(
                        """
                            Permissions information about user: $user
                            ---
                            > Group: ${PermissionsAPI.getUserGroup(user)}
                            > Permissions count: ${PermissionsAPI.getUserPermissions(user, false)
                            .count()}
                            > Inherited all permissions count: ${PermissionsAPI.getUserPermissions(
                            user,
                            true
                        ).count()}
                            > Inherited message prefix: ${PermissionsAPI.getGroupPrefix(
                            PermissionsAPI.getUserGroup(user)
                        )}
                            > Has operator permission: ${PermissionsAPI.hasPermission(user, "*")}
                            > Has native operator access: ${user in context.source.server.playerList.oppedPlayerNames}
                        """.trimIndent()
                    )
                } else {
                    MessagingAPI.sendMessage(
                        context.getPlayer()!!,
                        "${MESSAGE_MODULE_PREFIX}permissions.perm.user_read.info.success",
                        generalConfiguration.getBool(SETTING_LOC_ENABLED),
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

        validate(context, "ess.permissions.user.read.info", 3, ::action) {
            "${MESSAGE_MODULE_PREFIX}permissions.perm.user_read.info.restricted"
        }
        return 0
    }

    internal fun removeUser(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            CommandAPI.getString(context, "user-name").also { user ->
                PermissionsAPI.removeUser(user).also { result ->
                    if (result) {
                        when {
                            isServer -> ServerMessagingAPI.response(
                                "User $user was removed from permissions and refreshed to default group."
                            )
                            else -> MessagingAPI.sendMessage(
                                context.getPlayer()!!,
                                "${MESSAGE_MODULE_PREFIX}permissions.perm.user_modify.remove.success",
                                generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                user
                            )
                        }
                    } else {
                        when {
                            isServer -> ServerMessagingAPI.response(
                                "User $user was not removed from permissions because he does not have special privileges."
                            )
                            else -> MessagingAPI.sendMessage(
                                context.getPlayer()!!,
                                "${MESSAGE_MODULE_PREFIX}permissions.perm.user_modify.remove.error",
                                generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                user
                            )
                        }
                    }
                }
            }
        }

        validate(context, "ess.permissions.user.modify.remove", 3, ::action) {
            "${MESSAGE_MODULE_PREFIX}permissions.perm.user_modify.remove.restricted"
        }
        return 0
    }

    internal fun addUserPermission(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            CommandAPI.getString(context, "user-name").also { user ->
                CommandAPI.getString(context, "node").also { node ->
                    PermissionsAPI.addUserPermission(user, node).also {
                        when {
                            isServer -> ServerMessagingAPI.response(
                                "Permission node `$node` was added to user $user."
                            )
                            else -> MessagingAPI.sendMessage(
                                context.getPlayer()!!,
                                "${MESSAGE_MODULE_PREFIX}permissions.perm.user_modify.permissions.add.success",
                                generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                node, user
                            )
                        }
                    }
                }
            }
        }

        validate(context, "ess.permissions.user.modify.permissions.add", 3, ::action) {
            "${MESSAGE_MODULE_PREFIX}permissions.perm.user_modify.permissions.add.restricted"
        }
        return 0
    }

    internal fun removeUserPermission(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            CommandAPI.getString(context, "user-name").also { user ->
                CommandAPI.getString(context, "node").also { node ->
                    PermissionsAPI.removeUserPermission(user, node).also { result ->
                        if (result) {
                            when {
                                isServer -> ServerMessagingAPI.response(
                                    "Permission node `$node` was removed at user $user."
                                )
                                else -> MessagingAPI.sendMessage(
                                    context.getPlayer()!!,
                                    "${MESSAGE_MODULE_PREFIX}permissions.perm.user_modify.permissions.remove.success",
                                    generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                    node, user
                                )
                            }
                        } else {
                            when {
                                isServer -> ServerMessagingAPI.response(
                                    "Permission node `$node` was not removed at user $user. User not have this permission node for removing it."
                                )
                                else -> MessagingAPI.sendMessage(
                                    context.getPlayer()!!,
                                    "${MESSAGE_MODULE_PREFIX}permissions.perm.user_modify.permissions.remove.error",
                                    generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                    node, user
                                )
                            }
                        }
                    }
                }
            }
        }

        validate(context, "ess.permissions.user.modify.permissions.remove", 3, ::action) {
            "${MESSAGE_MODULE_PREFIX}permissions.perm.user_modify.permissions.remove.restricted"
        }
        return 0
    }

    internal fun listUserPermissions(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            CommandAPI.getString(context, "user-name").also { user ->
                CommandAPI.getBoolExisting(context, "deep").also { isDeep ->
                    PermissionsAPI.getUserPermissions(
                        user, if (isDeep) CommandAPI.getBool(context, "deep") else false
                    ).also { permissions ->
                        if (isServer) {
                            if (permissions.isEmpty()) {
                                ServerMessagingAPI.response(
                                    "Requested permissions list is empty, nothing to listing you."
                                )
                            } else {
                                ServerMessagingAPI.response(
                                    "Full permissions list for requested user $user ${if (isDeep) {
                                        "with including deep permissions"
                                    } else {
                                        ""
                                    }}:\n".plus(
                                        permissions.joinToString(
                                            prefix = "    > ", postfix = ","
                                        ) { "\n" }
                                    )
                                )
                            }
                        } else {
                            if (permissions.isEmpty()) {
                                MessagingAPI.sendMessage(
                                    context.getPlayer()!!,
                                    "project_essentials_permissions.perm.list.empty",
                                    generalConfiguration.getBool(SETTING_LOC_ENABLED)
                                )
                            } else {
                                val linesPerPage =
                                    permissionsSettingsConfiguration.take().displayObjectsLinesPerPage
                                val pages = permissions.count() / linesPerPage + 1
                                val page = when {
                                    CommandAPI.getIntExisting(context, "page") -> {
                                        CommandAPI.getInt(context, "page")
                                    }
                                    else -> 1
                                }

                                val displayedLines = page * linesPerPage
                                val droppedLines = displayedLines - linesPerPage
                                val values = permissions.take(displayedLines).drop(droppedLines)
                                val message = """
                                    §7Permissions page §c$page §7of §c$pages
                                    
                                    §7${values.joinToString { "\n§7" }}
                                """.trimIndent()

                                context.source.sendFeedback(
                                    StringTextComponent(message), false
                                )
                            }
                        }
                    }
                }
            }
        }

        validate(context, "ess.permissions.user.read.permissions", 3, ::action) {
            "${MESSAGE_MODULE_PREFIX}permissions.perm.user_read.permissions.restricted"
        }
        return 0
    }

    internal fun setUserGroup(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            CommandAPI.getString(context, "user-name").also { user ->
                CommandAPI.getString(context, "group-name").also { group ->
                    PermissionsAPI.setUserGroup(user, group).also { result ->
                        if (result) {
                            if (isServer) {
                                ServerMessagingAPI.response(
                                    "New group `$group` was setted up for user `$user`."
                                )
                            } else {
                                MessagingAPI.sendMessage(
                                    context.getPlayer()!!,
                                    "${MESSAGE_MODULE_PREFIX}permissions.perm.user_modify.group.set.success",
                                    generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                    group, user
                                )
                            }
                        } else {
                            if (isServer) {
                                val groups = PermissionsAPI.getGroups()
                                ServerMessagingAPI.response(
                                    """
                                        Configuring user group failed. Group with name `$group` not exist.
                                        
                                        Available groups (${groups.count()}):
                                        
                                    ${groups.map {
                                        it.name
                                    }.joinToString(prefix = "    > ", postfix = ",") { "\n" }}
                                    """.trimIndent()
                                )
                            } else {
                                MessagingAPI.sendMessage(
                                    context.getPlayer()!!,
                                    "${MESSAGE_MODULE_PREFIX}permissions.perm.user_modify.group.set.error",
                                    generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                    group
                                )
                            }
                        }
                    }
                }
            }
        }

        validate(context, "ess.permissions.user.modify.group.set", 3, ::action) {
            "${MESSAGE_MODULE_PREFIX}permissions.perm.user_modify.group.set.restricted"
        }
        return 0
    }

    internal fun listGroups(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            PermissionsAPI.getGroups().map { it.name }.also { groups ->
                if (isServer) {
                    if (groups.isEmpty()) {
                        ServerMessagingAPI.response(
                            "Requested groups list is empty, nothing to listing you."
                        )
                    } else {
                        ServerMessagingAPI.response(
                            "Requested full groups list:\n".plus(
                                groups.joinToString(prefix = "    > ", postfix = ",") { "\n" }
                            )
                        )
                    }
                } else {
                    if (groups.isEmpty()) {
                        MessagingAPI.sendMessage(
                            context.getPlayer()!!,
                            "project_essentials_permissions.group.list.empty",
                            generalConfiguration.getBool(SETTING_LOC_ENABLED)
                        )
                    } else {
                        val linesPerPage =
                            permissionsSettingsConfiguration.take().displayObjectsLinesPerPage
                        val pages = groups.count() / linesPerPage + 1
                        val page = when {
                            CommandAPI.getIntExisting(context, "page") -> {
                                CommandAPI.getInt(context, "page")
                            }
                            else -> 1
                        }

                        val displayedLines = page * linesPerPage
                        val droppedLines = displayedLines - linesPerPage
                        val values = groups.take(displayedLines).drop(droppedLines)
                        val message =
                            """
                                §7Groups list page §c$page §7of §c$pages
                                    
                                §7${values.joinToString { "\n§7" }}
                            """.trimIndent()

                        context.source.sendFeedback(
                            StringTextComponent(message), false
                        )
                    }
                }
            }
        }

        validate(context, "ess.permissions.group.read.list", 3, ::action) {
            "${MESSAGE_MODULE_PREFIX}permissions.perm.group_read.permissions.restricted"
        }
        return 0
    }

    internal fun setDefaultGroup(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            CommandAPI.getString(context, "group-name").also { group ->
                PermissionsAPI.defaultFactorMove(
                    group, PermissionsAPI.getDefaultGroup().name
                ).also { result ->
                    if (isServer) {
                        if (result) {
                            ServerMessagingAPI.response(
                                "Default group factor changed for group $group."
                            )
                        } else {
                            ServerMessagingAPI.response(
                                "Group with name $group not exist, changes was rolled back."
                            )
                        }
                    } else {
                        if (result) {
                            MessagingAPI.sendMessage(
                                context.getPlayer()!!,
                                "${MESSAGE_MODULE_PREFIX}permissions.perm.group_modify.group.default.success",
                                generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                group
                            )
                        } else {
                            MessagingAPI.sendMessage(
                                context.getPlayer()!!,
                                "${MESSAGE_MODULE_PREFIX}permissions.perm.group_modify.group.default.error",
                                generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                group
                            )
                        }
                    }
                }
            }
        }

        validate(context, "ess.permissions.group.modify.default.set", 3, ::action) {
            "${MESSAGE_MODULE_PREFIX}permissions.perm.group_modify.group.default.restricted"
        }
        return 0
    }

    internal fun createGroup(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            CommandAPI.getString(context, "group-name").also { group ->
                PermissionsAPI.addGroup(PermissionsConfigurationModel.Group(group)).also { result ->
                    if (isServer) {
                        if (result) {
                            ServerMessagingAPI.response(
                                "Group with name $group created, but new groups need to configure."
                            )
                        } else {
                            ServerMessagingAPI.response(
                                "Group with name $group not created, group with name $group already exist."
                            )
                        }
                    } else {
                        if (result) {
                            MessagingAPI.sendMessage(
                                context.getPlayer()!!,
                                "${MESSAGE_MODULE_PREFIX}permissions.perm.group_modify.group.create.success",
                                generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                group
                            )
                        } else {
                            MessagingAPI.sendMessage(
                                context.getPlayer()!!,
                                "${MESSAGE_MODULE_PREFIX}permissions.perm.group_modify.group.create.error",
                                generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                group
                            )
                        }
                    }
                }
            }
        }

        validate(context, "ess.permissions.group.modify.create", 3, ::action) {
            "${MESSAGE_MODULE_PREFIX}permissions.perm.group_modify.group.create.restricted"
        }
        return 0
    }

    internal fun removeGroup(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            CommandAPI.getString(context, "group-name").also { group ->
                PermissionsAPI.removeGroup(group).also { result ->
                    if (isServer) {
                        if (result) {
                            ServerMessagingAPI.response(
                                "Group with name $group removed."
                            )
                        } else {
                            ServerMessagingAPI.response(
                                "Group with name $group was not removed. Requested group not exist."
                            )
                        }
                    } else {
                        if (result) {
                            MessagingAPI.sendMessage(
                                context.getPlayer()!!,
                                "${MESSAGE_MODULE_PREFIX}permissions.perm.group_modify.group.remove.success",
                                generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                group
                            )
                        } else {
                            MessagingAPI.sendMessage(
                                context.getPlayer()!!,
                                "${MESSAGE_MODULE_PREFIX}permissions.perm.group_modify.group.remove.error",
                                generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                group
                            )
                        }
                    }
                }
            }
        }

        validate(context, "ess.permissions.group.modify.remove", 3, ::action) {
            "${MESSAGE_MODULE_PREFIX}permissions.perm.group_modify.group.remove.restricted"
        }
        return 0
    }

    internal fun addGroupPermission(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            CommandAPI.getString(context, "group-name").also { group ->
                CommandAPI.getString(context, "node").also { node ->
                    PermissionsAPI.addGroupPermission(group, node)

                    if (isServer) {
                        ServerMessagingAPI.response(
                            "Permission node `$node` was added to group $group."
                        )
                    } else {
                        MessagingAPI.sendMessage(
                            context.getPlayer()!!,
                            "${MESSAGE_MODULE_PREFIX}permissions.perm.group_modify.permissions.add.success",
                            generalConfiguration.getBool(SETTING_LOC_ENABLED),
                            node, group
                        )
                    }
                }
            }
        }

        validate(context, "ess.permissions.group.modify.permissions.add", 3, ::action) {
            "${MESSAGE_MODULE_PREFIX}permissions.perm.group_modify.group.permissions.add.restricted"
        }
        return 0
    }

    internal fun removeGroupPermission(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            CommandAPI.getString(context, "group-name").also { group ->
                CommandAPI.getString(context, "node").also { node ->
                    PermissionsAPI.removeGroupPermission(group, node).also { result ->
                        if (isServer) {
                            if (result) {
                                ServerMessagingAPI.response(
                                    "Permission node `$node` was removed from group $group."
                                )
                            } else {
                                ServerMessagingAPI.response(
                                    "Permission node removing failed, check group existing or this permission not exist."
                                )
                            }
                        } else {
                            if (result) {
                                MessagingAPI.sendMessage(
                                    context.getPlayer()!!,
                                    "${MESSAGE_MODULE_PREFIX}permissions.perm.group_modify.permissions.remove.success",
                                    generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                    node, group
                                )
                            } else {
                                MessagingAPI.sendMessage(
                                    context.getPlayer()!!,
                                    "${MESSAGE_MODULE_PREFIX}permissions.perm.group_modify.permissions.remove.error",
                                    generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                    node, group
                                )
                            }
                        }
                    }
                }
            }
        }

        validate(context, "ess.permissions.group.modify.permissions.add", 3, ::action) {
            "${MESSAGE_MODULE_PREFIX}permissions.perm.group_modify.group.permissions.add.restricted"
        }
        return 0
    }

    internal fun listGroupPermissions(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            CommandAPI.getString(context, "group-name").also { group ->
                CommandAPI.getBoolExisting(context, "deep").also { isDeep ->
                    PermissionsAPI.getGroupPermissions(
                        group, if (isDeep) CommandAPI.getBool(context, "deep") else false
                    ).also { permissions ->
                        if (isServer) {
                            if (permissions.isEmpty()) {
                                ServerMessagingAPI.response(
                                    "Requested permissions list is empty, nothing to listing you."
                                )
                            } else {
                                ServerMessagingAPI.response(
                                    "Full permissions list for requested group $group ${if (isDeep) {
                                        "with including deep permissions"
                                    } else {
                                        ""
                                    }}:\n".plus(
                                        permissions.joinToString(
                                            prefix = "    > ", postfix = ","
                                        ) { "\n" }
                                    )
                                )
                            }
                        } else {
                            if (permissions.isEmpty()) {
                                MessagingAPI.sendMessage(
                                    context.getPlayer()!!,
                                    "project_essentials_permissions.group.perm.list.empty",
                                    generalConfiguration.getBool(SETTING_LOC_ENABLED)
                                )
                            } else {
                                val linesPerPage =
                                    permissionsSettingsConfiguration.take().displayObjectsLinesPerPage
                                val pages = permissions.count() / linesPerPage + 1
                                val page = when {
                                    CommandAPI.getIntExisting(context, "page") -> {
                                        CommandAPI.getInt(context, "page")
                                    }
                                    else -> 1
                                }

                                val displayedLines = page * linesPerPage
                                val droppedLines = displayedLines - linesPerPage
                                val values = permissions.take(displayedLines).drop(droppedLines)
                                val message = """
                                    §7Permissions page §c$page §7of §c$pages
                                    
                                    §7${values.joinToString { "\n§7" }}
                                """.trimIndent()

                                context.source.sendFeedback(
                                    StringTextComponent(message), false
                                )
                            }
                        }
                    }
                }
            }
        }

        validate(context, "ess.permissions.group.read.permissions", 3, ::action) {
            "${MESSAGE_MODULE_PREFIX}permissions.perm.group_read.permissions.restricted"
        }
        return 0
    }

    internal fun addGroupInherit(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean) {
            CommandAPI.getString(context, "group-name").also { group ->
                CommandAPI.getString(context, "inherit-group").also { inherit ->
                    PermissionsAPI.addGroupInheritances(group, inherit).also { result ->
                        if (isServer) {
                            if (result) {
                                ServerMessagingAPI.response(
                                    "New inherit group $inherit was added to group $group."
                                )
                            } else {
                                ServerMessagingAPI.response(
                                    "Requested group with name $group not exist."
                                )
                            }
                        } else {
                            if (result) {
                                MessagingAPI.sendMessage(
                                    context.getPlayer()!!,
                                    "${MESSAGE_MODULE_PREFIX}permissions.perm.group_modify.inherit.add.success",
                                    generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                    inherit, group
                                )
                            } else {
                                MessagingAPI.sendMessage(
                                    context.getPlayer()!!,
                                    "${MESSAGE_MODULE_PREFIX}permissions.perm.group_modify.inherit.add.error",
                                    generalConfiguration.getBool(SETTING_LOC_ENABLED),
                                    group
                                )
                            }
                        }
                    }
                }
            }
        }

        validate(context, "ess.permissions.group.modify.inherit.add", 3, ::action) {
            "${MESSAGE_MODULE_PREFIX}permissions.perm.group_modify.group.inherit.add.restricted"
        }
        return 0
    }

    private fun validate(
        context: CommandContext<CommandSource>,
        node: String,
        opLevel: Int,
        action: (isServer: Boolean) -> Unit,
        failedMessage: () -> String
    ) = context.getPlayer()?.let {
        if (hasPermission(it, node, opLevel)) {
            action(false)
        } else {
            MessagingAPI.sendMessage(
                it, failedMessage(), generalConfiguration.getBool(SETTING_LOC_ENABLED)
            )
        }
    } ?: run { action(true) }
}
