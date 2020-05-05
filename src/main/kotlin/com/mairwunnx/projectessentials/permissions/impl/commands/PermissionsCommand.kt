package com.mairwunnx.projectessentials.permissions.impl.commands

import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.commands.Command
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mairwunnx.projectessentials.permissions.impl.configurations.PermissionsConfiguration
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

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
        fun action(isServer: Boolean = false) {
            permissionsConfiguration.save().run {
                if (isServer) {
                    ServerMessagingAPI.response("Permissions configuration saved.")
                } else {
                    MessagingAPI.sendMessage(
                        context.getPlayer()!!,
                        "${MESSAGE_MODULE_PREFIX}permissions.perm.save.success",
                        generalConfiguration.getBool(SETTING_LOC_ENABLED)
                    )
                }
            }
        }

        context.getPlayer()?.let {
            if (hasPermission(it, "ess.permissions.store.save", 4)) {
                action()
            } else {
                MessagingAPI.sendMessage(
                    it,
                    "${MESSAGE_MODULE_PREFIX}permissions.perm.save.restricted",
                    generalConfiguration.getBool(SETTING_LOC_ENABLED)
                )
            }
        } ?: run {
            action(true)
        }

        return 0
    }

    internal fun reload(context: CommandContext<CommandSource>): Int {
        fun action(isServer: Boolean = false) {
            permissionsConfiguration.load().run {
                if (isServer) {
                    ServerMessagingAPI.response("Permissions configuration reloaded.")
                } else {
                    MessagingAPI.sendMessage(
                        context.getPlayer()!!,
                        "${MESSAGE_MODULE_PREFIX}permissions.perm.reload.success",
                        generalConfiguration.getBool(SETTING_LOC_ENABLED)
                    )
                }
            }
        }

        context.getPlayer()?.let {
            if (hasPermission(it, "ess.permissions.store.reload", 4)) {
                action()
            } else {
                MessagingAPI.sendMessage(
                    it,
                    "${MESSAGE_MODULE_PREFIX}permissions.perm.reload.restricted",
                    generalConfiguration.getBool(SETTING_LOC_ENABLED)
                )
            }
        } ?: run {
            action(true)
        }

        return 0
    }
}
