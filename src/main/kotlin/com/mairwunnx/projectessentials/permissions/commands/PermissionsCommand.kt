package com.mairwunnx.projectessentials.permissions.commands

import com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases
import com.mairwunnx.projectessentials.core.extensions.isPlayerSender
import com.mairwunnx.projectessentials.core.extensions.playerName
import com.mairwunnx.projectessentials.core.extensions.sendMsg
import com.mairwunnx.projectessentials.core.helpers.PERMISSION_LEVEL
import com.mairwunnx.projectessentials.permissions.EntryPoint
import com.mairwunnx.projectessentials.permissions.permissions.PermissionBase
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import org.apache.logging.log4j.LogManager

@Suppress("DuplicatedCode")
internal object PermissionsCommand {
    private val aliases = listOf("permissions", "permission", "perm")
    private val logger = LogManager.getLogger()

    internal fun register(dispatcher: CommandDispatcher<CommandSource>) {
        tryAssignAliases()
        aliases.forEach { command ->
            dispatcher.register(
                literal<CommandSource>(command)
                    .then(buildAboutCommand())
                    .then(buildGroupCommand())
                    .then(buildUserCommand())
                    .then(buildReloadCommand())
                    .then(buildSaveCommand())
            )
        }
    }

    private fun tryAssignAliases() {
        try {
            Class.forName(
                "com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases"
            )
            CommandsAliases.aliases["permissions"] = aliases.toMutableList()
        } catch (_: ClassNotFoundException) {
            try {
                Class.forName(
                    "com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases"
                )
                CommandsAliases.aliases["permissions"] = aliases.toMutableList()
            } catch (_: ClassNotFoundException) {
                // ignored
            }
            // ignored
        }
    }

    private fun buildAboutCommand(): LiteralArgumentBuilder<CommandSource> {
        return Commands.literal("about").executes {
            return@executes aboutCommandExecute(it)
        }
    }

    private fun aboutCommandExecute(
        c: CommandContext<CommandSource>
    ): Int {
        if (c.isPlayerSender()) {
            if (PermissionsAPI.hasPermission(c.playerName(), "ess.perm")) {
                sendMsg(
                    "permissions",
                    c.source,
                    "perm.about.out",
                    EntryPoint.modInstance.modName,
                    EntryPoint.modInstance.modVersion,
                    EntryPoint.modInstance.modMaintainer,
                    EntryPoint.modInstance.modTargetForge,
                    EntryPoint.modInstance.modTargetMC,
                    EntryPoint.modInstance.modSources,
                    EntryPoint.modInstance.modTelegram,
                    EntryPoint.modInstance.modCurseForge
                )
            } else {
                sendMsg("permissions", c.source, "perm.about.restricted")
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", c.playerName())
                        .replace("%1", "permissions")
                )
            }
        } else {
            logger.info("        ${EntryPoint.modInstance.modName}")
            logger.info("Version: ${EntryPoint.modInstance.modVersion}")
            logger.info("Maintainer: ${EntryPoint.modInstance.modMaintainer}")
            logger.info("Target Forge version: ${EntryPoint.modInstance.modTargetForge}")
            logger.info("Target Minecraft version: ${EntryPoint.modInstance.modTargetMC}")
            logger.info("Source code: ${EntryPoint.modInstance.modSources}")
            logger.info("Telegram chat: ${EntryPoint.modInstance.modTelegram}")
            logger.info("CurseForge: ${EntryPoint.modInstance.modCurseForge}")
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
                c.playerName(), "ess.perm.reload"
            ) -> {
                sendMsg("permissions", c.source, "perm.reload.restricted")
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", c.playerName())
                        .replace("%1", "permissions reload")
                )
                0
            }
            else -> {
                PermissionBase.loadData()
                PermissionsAPI.oppedPlayers.clear()
                PermissionsAPI.oppedPlayers.addAll(c.source.server.playerList.oppedPlayerNames)
                tryAssignAliases()
                when {
                    c.isPlayerSender() -> sendMsg(
                        "permissions", c.source, "perm.reload.success"
                    )
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
                sendMsg("permissions", c.source, "perm.save.restricted")
                logger.info(
                    PERMISSION_LEVEL
                        .replace("%0", c.playerName())
                        .replace("%1", "permissions save")
                )
                0
            }
            else -> {
                PermissionBase.saveData()
                when {
                    c.isPlayerSender() -> sendMsg(
                        "permissions", c.source, "perm.save.success"
                    )
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
            sendMsg("permissions", c.source, "perm.group.restricted")
            logger.info(
                PERMISSION_LEVEL
                    .replace("%0", c.playerName())
                    .replace("%1", "permissions group [...]")
            )
            return 0
        }
        when {
            c.isPlayerSender() -> sendMsg(
                "permissions", c.source, "perm.group.example"
            )
            else -> logger.info("Usage example: /ess permissions group <group> [set|remove] <node>")
        }
        return 0
    }

    private fun groupCommandSetExecute(c: CommandContext<CommandSource>): Int {
        if (c.isPlayerSender() &&
            !PermissionsAPI.hasPermission(c.playerName(), "ess.perm.group")
        ) {
            sendMsg("permissions", c.source, "perm.group.restricted")
            logger.info(
                PERMISSION_LEVEL
                    .replace("%0", c.playerName())
                    .replace("%1", "permissions group [...]")
            )
            return 0
        }
        val targetGroup = StringArgumentType.getString(c, "name")
        val targetNode = StringArgumentType.getString(c, "node")
        PermissionsAPI.setGroupPermissionNode(targetGroup, targetNode)
        if (c.isPlayerSender()) {
            sendMsg(
                "permissions",
                c.source,
                "perm.group.success",
                targetNode,
                targetGroup
            )
        } else {
            logger.info("Permission $targetNode added to group $targetGroup.")
        }
        return 0
    }

    private fun groupCommandRemoveExecute(c: CommandContext<CommandSource>): Int {
        if (c.isPlayerSender() &&
            !PermissionsAPI.hasPermission(c.playerName(), "ess.perm.group")
        ) {
            sendMsg("permissions", c.source, "perm.group.restricted")
            logger.info(
                PERMISSION_LEVEL
                    .replace("%0", c.playerName())
                    .replace("%1", "permissions group [...]")
            )
            return 0
        }
        val targetGroup = StringArgumentType.getString(c, "name")
        val targetNode = StringArgumentType.getString(c, "node")
        PermissionsAPI.removeGroupPermission(targetGroup, targetNode)
        if (c.isPlayerSender()) {
            sendMsg(
                "permissions",
                c.source,
                "perm.group.remove.success",
                targetNode,
                targetGroup
            )
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
            sendMsg("permissions", c.source, "perm.user.restricted")
            logger.info(
                PERMISSION_LEVEL
                    .replace("%0", c.playerName())
                    .replace("%1", "permissions user [...]")
            )
            return 0
        }
        when {
            c.isPlayerSender() -> sendMsg(
                "permissions", c.source, "perm.user.example"
            )
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
            sendMsg("permissions", c.source, "perm.user.restricted")
            logger.info(
                PERMISSION_LEVEL
                    .replace("%0", c.playerName())
                    .replace("%1", "permissions user [...]")
            )
            return 0
        }
        val targetUser = StringArgumentType.getString(c, "nickname")
        val targetNode = StringArgumentType.getString(c, "node")
        PermissionsAPI.setUserPermissionNode(targetUser, targetNode)
        if (c.isPlayerSender()) {
            sendMsg(
                "permissions",
                c.source,
                "perm.user.success",
                targetNode,
                targetUser
            )
        } else {
            logger.info("Permission $targetNode added for user $targetUser.")
        }
        return 0
    }

    private fun userCommandSetGroupExecute(c: CommandContext<CommandSource>): Int {
        if (c.isPlayerSender() &&
            !PermissionsAPI.hasPermission(c.playerName(), "ess.perm.user")
        ) {
            sendMsg("permissions", c.source, "perm.user.restricted")
            logger.info(
                PERMISSION_LEVEL
                    .replace("%0", c.playerName())
                    .replace("%1", "permissions user [...]")
            )
            return 0
        }
        val targetUser = StringArgumentType.getString(c, "nickname")
        val targetGroup = StringArgumentType.getString(c, "groupName")
        if (targetGroup.isNullOrEmpty()) {
            when {
                c.isPlayerSender() -> sendMsg(
                    "permissions", c.source, "perm.user.group.example"
                )
                else -> logger.info(
                    "Usage example: /ess permissions user <nickname> set group <group name>"
                )
            }
            return 0
        }
        PermissionsAPI.setUserPermissionGroup(targetUser, targetGroup)
        if (c.isPlayerSender()) {
            sendMsg(
                "permissions",
                c.source,
                "perm.user.group.success",
                targetGroup,
                targetUser
            )
        } else {
            logger.info("Installed new group $targetGroup for user $targetUser.")
        }
        return 0
    }

    private fun userCommandRemoveExecute(c: CommandContext<CommandSource>): Int {
        if (c.isPlayerSender() &&
            !PermissionsAPI.hasPermission(c.playerName(), "ess.perm.user")
        ) {
            sendMsg("permissions", c.source, "perm.user.restricted")
            logger.info(
                PERMISSION_LEVEL
                    .replace("%0", c.playerName())
                    .replace("%1", "permissions user [...]")
            )
            return 0
        }
        val targetUser = StringArgumentType.getString(c, "nickname")
        val targetNode = StringArgumentType.getString(c, "node")
        PermissionsAPI.removeUserPermission(targetUser, targetNode)
        if (c.isPlayerSender()) {
            sendMsg(
                "permissions",
                c.source,
                "perm.user.remove.success",
                targetNode,
                targetUser
            )
        } else {
            logger.info("Permission $targetNode removed from user $targetUser")
        }
        return 0
    }
}
