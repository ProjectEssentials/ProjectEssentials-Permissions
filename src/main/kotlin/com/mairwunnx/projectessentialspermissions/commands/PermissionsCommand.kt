package com.mairwunnx.projectessentialspermissions.commands

import com.mairwunnx.projectessentialspermissions.*
import com.mairwunnx.projectessentialspermissions.extensions.isPlayerSender
import com.mairwunnx.projectessentialspermissions.extensions.playerName
import com.mairwunnx.projectessentialspermissions.extensions.sendMsg
import com.mairwunnx.projectessentialspermissions.permissions.PermissionBase
import com.mairwunnx.projectessentialspermissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import org.apache.logging.log4j.LogManager

internal object PermissionsCommand {
    private val aliases = arrayOf("essentials", "ess")
    private val logger = LogManager.getLogger()

    internal fun register(dispatcher: CommandDispatcher<CommandSource>) {
        aliases.forEach { command ->
            dispatcher.register(
                literal<CommandSource>(command).then(
                    buildAboutCommand()
                        .then(buildGroupCommand())
                        .then(buildUserCommand())
                        .then(buildReloadCommand())
                        .then(buildSaveCommand())
                )
            )
        }
    }

    private fun buildAboutCommand(): LiteralArgumentBuilder<CommandSource> {
        return Commands.literal("permissions").executes {
            return@executes aboutCommandExecute(it)
        }
    }

    private fun aboutCommandExecute(
        c: CommandContext<CommandSource>
    ): Int {
        if (c.isPlayerSender()) {
            if (PermissionsAPI.hasPermission(c.playerName(), "ess.perm")) {
                sendMsg(
                    c.source,
                    "perm.about.out",
                    MOD_NAME,
                    MOD_VERSION,
                    MOD_MAINTAINER,
                    MOD_TARGET_FORGE,
                    MOD_TARGET_MC,
                    MOD_SOURCES_LINK,
                    MOD_TELEGRAM_LINK
                )
            } else {
                sendMsg(c.source, "perm.about.restricted")
            }
        } else {
            logger.info("        $MOD_NAME")
            logger.info("Version: $MOD_VERSION")
            logger.info("Maintainer: $MOD_MAINTAINER")
            logger.info("Target Forge version: $MOD_TARGET_FORGE")
            logger.info("Target Minecraft version: $MOD_TARGET_MC")
            logger.info("Source code: $MOD_SOURCES_LINK")
            logger.info("Telegram chat: $MOD_TELEGRAM_LINK")
        }
        return 0
    }

    private fun buildReloadCommand(): LiteralArgumentBuilder<CommandSource> {
        return Commands.literal("reload").executes {
            return@executes reloadCommandExecute(it)
        }
    }

    private fun reloadCommandExecute(
        c: CommandContext<CommandSource>
    ): Int {
        return when {
            c.isPlayerSender() && !PermissionsAPI.hasPermission(
                c.playerName(), "perm.reload"
            ) -> {
                sendMsg(c.source, "perm.reload.restricted")
                0
            }
            else -> {
                PermissionBase.loadData()
                when {
                    c.isPlayerSender() -> sendMsg(c.source, "perm.reload.success")
                    else -> logger.info("Permission configuration reloaded.")
                }
                0
            }
        }
    }

    private fun buildSaveCommand(): LiteralArgumentBuilder<CommandSource> {
        return Commands.literal("save").executes {
            return@executes saveCommandExecute(it)
        }
    }

    private fun saveCommandExecute(
        c: CommandContext<CommandSource>
    ): Int {
        return when {
            c.isPlayerSender() && !PermissionsAPI.hasPermission(
                c.source.asPlayer().name.string, "ess.perm.save"
            ) -> {
                sendMsg(c.source, "perm.save.restricted")
                0
            }
            else -> {
                PermissionBase.saveData()
                when {
                    c.isPlayerSender() -> sendMsg(c.source, "perm.save.success")
                    else -> logger.info("Permission configuration saved.")
                }
                0
            }
        }
    }

    private fun buildGroupCommand(): LiteralArgumentBuilder<CommandSource> {
        return Commands.literal("group").executes {
            return@executes groupCommandExecute(it)
        }.then(
            Commands.argument(
                "name", StringArgumentType.string()
            ).executes {
                return@executes groupCommandExecute(it)
            }.then(
                Commands.literal("set").executes {
                    return@executes groupCommandExecute(it)
                }.then(
                    Commands.argument(
                        "node",
                        StringArgumentType.string()
                    ).executes {
                        return@executes groupCommandSetExecute(it)
                    }
                )
            ).then(
                Commands.literal("remove").executes {
                    return@executes groupCommandExecute(it)
                }.then(
                    Commands.argument(
                        "node",
                        StringArgumentType.string()
                    ).executes {
                        return@executes groupCommandRemoveExecute(it)
                    }
                )
            )
        )
    }

    private fun groupCommandExecute(c: CommandContext<CommandSource>): Int {
        if (c.isPlayerSender() &&
            !PermissionsAPI.hasPermission(c.playerName(), "ess.perm.group")
        ) {
            sendMsg(c.source, "perm.group.restricted")
            return 0
        }
        when {
            c.isPlayerSender() -> sendMsg(c.source, "perm.group.example")
            else -> logger.info("Usage example: /ess permissions group <group> [set|remove] <node>")
        }
        return 0
    }

    private fun groupCommandSetExecute(c: CommandContext<CommandSource>): Int {
        if (c.isPlayerSender() &&
            !PermissionsAPI.hasPermission(c.playerName(), "ess.perm.group")
        ) {
            sendMsg(c.source, "perm.group.restricted")
            return 0
        }
        val targetGroup = StringArgumentType.getString(c, "name")
        val targetNode = StringArgumentType.getString(c, "node")
        PermissionsAPI.setGroupPermissionNode(targetGroup, targetNode)
        if (c.isPlayerSender()) {
            sendMsg(c.source, "perm.group.success", targetNode, targetGroup)
        } else {
            logger.info("Permission $targetNode added to group $targetGroup.")
        }
        return 0
    }

    private fun groupCommandRemoveExecute(c: CommandContext<CommandSource>): Int {
        if (c.isPlayerSender() &&
            !PermissionsAPI.hasPermission(c.playerName(), "ess.perm.group")
        ) {
            sendMsg(c.source, "perm.group.restricted")
            return 0
        }
        val targetGroup = StringArgumentType.getString(c, "name")
        val targetNode = StringArgumentType.getString(c, "node")
        PermissionsAPI.removeGroupPermission(targetGroup, targetNode)
        if (c.isPlayerSender()) {
            sendMsg(c.source, "perm.group.remove.success", targetNode, targetGroup)
        } else {
            logger.info("Permission $targetNode removed from group $targetGroup.")
        }
        return 0
    }

    private fun buildUserCommand(): LiteralArgumentBuilder<CommandSource> {
        return Commands.literal("user").executes {
            return@executes userCommandExecute(it)
        }.then(
            Commands.argument(
                "nickname", StringArgumentType.string()
            ).executes {
                return@executes userCommandExecute(it)
            }.then(
                Commands.literal("set").executes {
                    return@executes userCommandExecute(it)
                }.then(
                    Commands.argument(
                        "node",
                        StringArgumentType.string()
                    ).executes {
                        return@executes userCommandSetExecute(it)
                    }
                ).then(
                    Commands.literal("group").executes {
                        return@executes userCommandSetGroupExecute(it)
                    }.then(
                        Commands.argument(
                            "groupName",
                            StringArgumentType.string()
                        ).executes {
                            return@executes userCommandSetGroupExecute(it)
                        }
                    )
                )
            ).then(
                Commands.literal("remove").executes {
                    return@executes userCommandExecute(it)
                }.then(
                    Commands.argument(
                        "node",
                        StringArgumentType.string()
                    ).executes {
                        return@executes userCommandRemoveExecute(it)
                    }
                )
            )
        )
    }

    private fun userCommandExecute(c: CommandContext<CommandSource>): Int {
        if (c.isPlayerSender() &&
            !PermissionsAPI.hasPermission(c.playerName(), "ess.perm.user")
        ) {
            sendMsg(c.source, "perm.user.restricted")
            return 0
        }
        when {
            c.isPlayerSender() -> sendMsg(c.source, "perm.user.example")
            else -> logger.info(
                "Usage example: /ess permissions user <nickname> [[set]|remove] [<node>] [[group]] [[<group name>]]"
            )
        }
        return 0
    }

    private fun userCommandSetExecute(c: CommandContext<CommandSource>): Int {
        if (c.isPlayerSender() &&
            !PermissionsAPI.hasPermission(c.playerName(), "ess.perm.user")
        ) {
            sendMsg(c.source, "perm.user.restricted")
            return 0
        }
        val targetUser = StringArgumentType.getString(c, "nickname")
        val targetNode = StringArgumentType.getString(c, "node")
        PermissionsAPI.setUserPermissionNode(targetUser, targetNode)
        if (c.isPlayerSender()) {
            sendMsg(c.source, "perm.user.success", targetNode, targetUser)
        } else {
            logger.info("Permission $targetNode added for user $targetUser.")
        }
        return 0
    }

    private fun userCommandSetGroupExecute(c: CommandContext<CommandSource>): Int {
        if (c.isPlayerSender() &&
            !PermissionsAPI.hasPermission(c.playerName(), "ess.perm.user")
        ) {
            sendMsg(c.source, "perm.user.restricted")
            return 0
        }
        val targetUser = StringArgumentType.getString(c, "nickname")
        val targetGroup = StringArgumentType.getString(c, "groupName")
        if (targetGroup.isNullOrEmpty()) {
            when {
                c.isPlayerSender() -> sendMsg(c.source, "perm.user.group.example")
                else -> logger.info(
                    "Usage example: /ess permissions user <nickname> set group <group name>"
                )
            }
            return 0
        }
        PermissionsAPI.setUserPermissionGroup(targetUser, targetGroup)
        if (c.isPlayerSender()) {
            sendMsg(c.source, "perm.user.group.success", targetGroup, targetUser)
        } else {
            logger.info("Installed new group $targetGroup for user $targetUser.")
        }
        return 0
    }

    private fun userCommandRemoveExecute(c: CommandContext<CommandSource>): Int {
        if (c.isPlayerSender() &&
            !PermissionsAPI.hasPermission(c.playerName(), "ess.perm.user")
        ) {
            sendMsg(c.source, "perm.user.restricted")
            return 0
        }
        val targetUser = StringArgumentType.getString(c, "nickname")
        val targetNode = StringArgumentType.getString(c, "node")
        PermissionsAPI.setUserPermissionNode(targetUser, targetNode)
        if (c.isPlayerSender()) {
            sendMsg(c.source, "perm.user.remove.success", targetNode, targetUser)
        } else {
            logger.info("Permission $targetNode removed from user $targetUser")
        }
        return 0
    }
}
