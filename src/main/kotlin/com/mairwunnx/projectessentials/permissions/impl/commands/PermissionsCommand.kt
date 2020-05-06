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
                                    permissionsSettingsConfiguration.take().permissionsListLinesPerPage
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
