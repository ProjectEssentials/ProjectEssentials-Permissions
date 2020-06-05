package com.mairwunnx.projectessentials.permissions.impl.commands

import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_CORE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.extensions.isPlayerSender
import com.mairwunnx.projectessentials.core.api.v1.extensions.playerName
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.core.impl.commands.ConfigureEssentialsCommandAPI
import com.mairwunnx.projectessentials.permissions.impl.configurations.PermissionsSettingsConfiguration
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager

internal object ConfigurePermissionsCommand : CommandBase(
    takeConfigurePermissionsLiteral(), false
) {
    private val logger = LogManager.getLogger()
    private val marker = MarkerManager.Log4jMarker("COMMAND OUT")

    private val permissionsSettings by lazy {
        getConfigurationByName<PermissionsSettingsConfiguration>("permissions-settings")
    }

    init {
        ConfigureEssentialsCommandAPI.required("replace-world-edit-permissions-handler")
        ConfigureEssentialsCommandAPI.required("enable-permissions-command")
    }

    override val name = "configure-permissions"

    internal fun useSimplifiedWorldPermissions(context: CommandContext<CommandSource>): Int {
        validate(
            context,
            "ess.configure.permissions.use-simplified-world-permissions",
            "use-simplified-world-permissions"
        ) {
            val value = CommandAPI.getString(context, "value")
            val oldValue = permissionsSettings.take().useSimplifiedWorldPermissions
            permissionsSettings.take().useSimplifiedWorldPermissions = value.toBoolean()
            changed(context, "use-simplified-world-permissions", oldValue.toString(), value)
            executed(context)
        }
        return 0
    }

    internal fun replaceWorldEditPermissionsHandler(context: CommandContext<CommandSource>): Int {
        validate(
            context,
            "ess.configure.permissions.replace-world-edit-permissions-handler",
            "replace-world-edit-permissions-handler"
        ) {
            val value = CommandAPI.getString(context, "value")
            val oldValue = permissionsSettings.take().replaceWorldEditPermissionsHandler
            permissionsSettings.take().replaceWorldEditPermissionsHandler = value.toBoolean()
            changed(context, "replace-world-edit-permissions-handler", oldValue.toString(), value)
            executed(context)
        }
        return 0
    }

    internal fun debugMode(context: CommandContext<CommandSource>): Int {
        validate(
            context,
            "ess.configure.permissions.debug-mode",
            "debug-mode"
        ) {
            val value = CommandAPI.getString(context, "value")
            val oldValue = permissionsSettings.take().debugMode
            permissionsSettings.take().debugMode = value.toBoolean()
            changed(context, "debug-mode", oldValue.toString(), value)
            executed(context)
        }
        return 0
    }

    internal fun handleBlockBreaking(context: CommandContext<CommandSource>): Int {
        validate(
            context,
            "ess.configure.permissions.handle-block-breaking",
            "handle-block-breaking"
        ) {
            val value = CommandAPI.getString(context, "value")
            val oldValue = permissionsSettings.take().handleBlockBreaking
            permissionsSettings.take().handleBlockBreaking = value.toBoolean()
            changed(context, "handle-block-breaking", oldValue.toString(), value)
            executed(context)
        }
        return 0
    }

    internal fun handleBlockPlacing(context: CommandContext<CommandSource>): Int {
        validate(
            context,
            "ess.configure.permissions.handle-block-placing",
            "handle-block-placing"
        ) {
            val value = CommandAPI.getString(context, "value")
            val oldValue = permissionsSettings.take().handleBlockPlacing
            permissionsSettings.take().handleBlockPlacing = value.toBoolean()
            changed(context, "handle-block-placing", oldValue.toString(), value)
            executed(context)
        }
        return 0
    }

    internal fun handleFarmlandTrampling(context: CommandContext<CommandSource>): Int {
        validate(
            context,
            "ess.configure.permissions.handle-farmland-trampling",
            "handle-farmland-trampling"
        ) {
            val value = CommandAPI.getString(context, "value")
            val oldValue = permissionsSettings.take().handleFarmlandTrampling
            permissionsSettings.take().handleFarmlandTrampling = value.toBoolean()
            changed(context, "handle-farmland-trampling", oldValue.toString(), value)
            executed(context)
        }
        return 0
    }

    internal fun handleItemUsing(context: CommandContext<CommandSource>): Int {
        validate(
            context,
            "ess.configure.permissions.handle-item-using",
            "handle-item-using"
        ) {
            val value = CommandAPI.getString(context, "value")
            val oldValue = permissionsSettings.take().handleItemUsing
            permissionsSettings.take().handleItemUsing = value.toBoolean()
            changed(context, "handle-item-using", oldValue.toString(), value)
            executed(context)
        }
        return 0
    }

    private fun executed(context: CommandContext<CommandSource>) =
        logger.debug(
            marker, " :: Executed command ${context.input} by ${context.playerName()}"
        )

    private fun validate(
        context: CommandContext<CommandSource>,
        node: String,
        setting: String,
        action: (isServer: Boolean) -> Unit
    ) = context.getPlayer()?.let {
        if (hasPermission(it, node, 4)) {
            action(false)
        } else {
            MessagingAPI.sendMessage(
                context.getPlayer()!!,
                "$MESSAGE_CORE_PREFIX.configure.restricted",
                args = *arrayOf(setting)
            )
        }
    } ?: run { action(true) }

    private fun changed(
        context: CommandContext<CommandSource>,
        setting: String,
        oldValue: String,
        value: String
    ) {
        if (context.isPlayerSender()) {
            if (ConfigureEssentialsCommandAPI.isRequired(setting)) {
                LogManager.getLogger().info(
                    "Setting name `$setting` value changed by ${context.playerName()} from `$oldValue` to $value, but restart required for applying changes."
                )
                MessagingAPI.sendMessage(
                    context.getPlayer()!!,
                    "$MESSAGE_CORE_PREFIX.configure.successfully_required_restart",
                    args = *arrayOf(setting, oldValue, value)
                )
            } else {
                LogManager.getLogger().info(
                    "Setting name `$setting` value changed by ${context.playerName()} from `$oldValue` to $value"
                )
                MessagingAPI.sendMessage(
                    context.getPlayer()!!,
                    "$MESSAGE_CORE_PREFIX.configure.successfully",
                    args = *arrayOf(setting, oldValue, value)
                )
            }
        } else {
            if (ConfigureEssentialsCommandAPI.isRequired(setting)) {
                ServerMessagingAPI.response {
                    "Setting name `$setting` value changed from `$oldValue` to $value, but restart required for applying changes."
                }
            } else {
                ServerMessagingAPI.response {
                    "Setting name `$setting` value changed from `$oldValue` to $value"
                }
            }
        }
    }
}
