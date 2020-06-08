package com.mairwunnx.projectessentials.permissions.impl.commands

import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.permissions.api.v1.PermissionsAPI
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.ISuggestionProvider
import net.minecraftforge.fml.server.ServerLifecycleHooks.getCurrentServer

fun takePermissionsLiteral(): LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("permissions").then(
        Commands.literal("save").executes { PermissionsCommand.save(it) }
    ).then(
        Commands.literal("reload").executes { PermissionsCommand.reload(it) }
    ).then(
        Commands.literal("user").then(
            Commands.literal("info").then(
                Commands.argument("user-name", StringArgumentType.string())
                    .suggests { _, builder ->
                        ISuggestionProvider.suggest(
                            getCurrentServer().playerList.players.map { it.name.string }, builder
                        )
                    }
                    .executes { PermissionsCommand.userInfo(it) }
            )
        ).then(
            Commands.literal("remove").then(
                Commands.argument("user-name", StringArgumentType.string())
                    .suggests { _, builder ->
                        ISuggestionProvider.suggest(
                            PermissionsAPI.getUsers().map { it.nickname }, builder
                        )
                    }.executes { PermissionsCommand.userRemove(it) }
            )
        ).then(
            Commands.literal("permissions").then(
                Commands.argument(
                    "user-name", StringArgumentType.string()
                ).suggests { _, builder ->
                    ISuggestionProvider.suggest(
                        getCurrentServer().playerList.players.map { it.name.string }, builder
                    )
                }.then(
                    Commands.literal("add").then(
                        Commands.argument("node", StringArgumentType.string())
                            .executes { PermissionsCommand.userPermissionsAdd(it) }
                    )
                ).then(
                    Commands.literal("remove").then(
                        Commands.argument("node", StringArgumentType.string())
                            .suggests { context, builder ->
                                ISuggestionProvider.suggest(
                                    PermissionsAPI.getUserPermissions(
                                        CommandAPI.getString(context, "user-name"), false
                                    ), builder
                                )
                            }
                            .executes { PermissionsCommand.userPermissionsRemove(it) }
                    )
                ).then(
                    Commands.literal("list").then(
                        Commands.argument("deep", BoolArgumentType.bool())
                            .executes { PermissionsCommand.userPermissionsList(it) }
                            .then(
                                Commands.argument("page", IntegerArgumentType.integer(0))
                                    .executes { PermissionsCommand.userPermissionsList(it) }
                            )
                    ).then(
                        Commands.argument("page", IntegerArgumentType.integer(0))
                            .executes { PermissionsCommand.userPermissionsList(it) }
                    ).executes { PermissionsCommand.userPermissionsList(it) }
                )
            )
        ).then(
            Commands.literal("group").then(
                Commands.literal("set").then(
                    Commands.argument(
                        "group-name", StringArgumentType.string()
                    ).suggests { _, builder ->
                        ISuggestionProvider.suggest(
                            PermissionsAPI.getGroups().map { it.name }, builder
                        )
                    }.then(
                        Commands.literal("for").then(
                            Commands.argument("user-name", StringArgumentType.string())
                                .suggests { _, builder ->
                                    ISuggestionProvider.suggest(
                                        getCurrentServer().playerList.players.map { it.name.string },
                                        builder
                                    )
                                }
                                .executes { PermissionsCommand.userSetGroup(it) }
                        )
                    )
                )
            )
        )
    ).then(
        Commands.literal("group").then(
            Commands.literal("list").then(
                Commands.argument("page", IntegerArgumentType.integer(0))
                    .executes { PermissionsCommand.groupList(it) }
            ).executes { PermissionsCommand.groupList(it) }
        ).then(
            Commands.literal("set-default").then(
                Commands.argument(
                    "group-name", StringArgumentType.string()
                ).suggests { _, builder ->
                    ISuggestionProvider.suggest(
                        PermissionsAPI.getGroups().filter { !it.isDefault }.map { it.name }, builder
                    )
                }.executes { PermissionsCommand.groupDefaultSet(it) }
            )
        ).then(
            Commands.literal("create").then(
                Commands.argument("group-name", StringArgumentType.string())
                    .executes { PermissionsCommand.groupCreate(it) }
            )
        ).then(
            Commands.literal("remove").then(
                Commands.argument(
                    "group-name", StringArgumentType.string()
                ).suggests { _, builder ->
                    ISuggestionProvider.suggest(PermissionsAPI.getGroups().map { it.name }, builder)
                }.executes { PermissionsCommand.groupRemove(it) }
            )
        ).then(
            Commands.literal("permissions").then(
                Commands.argument(
                    "group-name", StringArgumentType.string()
                ).suggests { _, builder ->
                    ISuggestionProvider.suggest(PermissionsAPI.getGroups().map { it.name }, builder)
                }.then(
                    Commands.literal("add").then(
                        Commands.argument("node", StringArgumentType.string())
                            .executes { PermissionsCommand.groupPermissionsAdd(it) }
                    )
                ).then(
                    Commands.literal("remove").then(
                        Commands.argument("node", StringArgumentType.string())
                            .suggests { context, builder ->
                                ISuggestionProvider.suggest(
                                    PermissionsAPI.getGroupPermissions(
                                        CommandAPI.getString(context, "group-name")
                                    ), builder
                                )
                            }
                            .executes { PermissionsCommand.groupPermissionsRemove(it) }
                    )
                ).then(
                    Commands.literal("list").then(
                        Commands.argument("deep", BoolArgumentType.bool())
                            .executes { PermissionsCommand.groupPermissionsList(it) }
                            .then(
                                Commands.argument("page", IntegerArgumentType.integer(0))
                                    .executes { PermissionsCommand.groupPermissionsList(it) }
                            )
                    ).then(
                        Commands.argument("page", IntegerArgumentType.integer(0))
                            .executes { PermissionsCommand.groupPermissionsList(it) }
                    ).executes { PermissionsCommand.groupPermissionsList(it) }
                )
            )
        ).then(
            Commands.literal("inherit").then(
                Commands.argument(
                    "group-name", StringArgumentType.string()
                ).suggests { _, builder ->
                    ISuggestionProvider.suggest(
                        PermissionsAPI.getGroups().map { it.name }, builder
                    )
                }.then(
                    Commands.literal("add").then(
                        Commands.argument(
                            "inherit-group", StringArgumentType.string()
                        ).suggests { context, builder ->
                            ISuggestionProvider.suggest(
                                PermissionsAPI.getGroups().filter {
                                    it.name != CommandAPI.getString(context, "group-name")
                                }.map { it.name }, builder
                            )
                        }.executes { PermissionsCommand.groupInheritAdd(it) }
                    )
                ).then(
                    Commands.literal("remove").then(
                        Commands.argument(
                            "inherit-group", StringArgumentType.string()
                        ).suggests { context, builder ->
                            ISuggestionProvider.suggest(
                                PermissionsAPI.getGroupInherits(
                                    CommandAPI.getString(context, "group-name"), false
                                ), builder
                            )
                        }.executes { PermissionsCommand.groupInheritRemove(it) }
                    )
                ).then(
                    Commands.literal("list").then(
                        Commands.argument("page", IntegerArgumentType.integer(0))
                            .executes { PermissionsCommand.groupInheritList(it) }
                    ).executes { PermissionsCommand.groupInheritList(it) }
                )
            )
        ).then(
            Commands.literal("prefix").then(
                Commands.argument(
                    "group-name", StringArgumentType.string()
                ).suggests { _, builder ->
                    ISuggestionProvider.suggest(
                        PermissionsAPI.getGroups().map { it.name }, builder
                    )
                }.then(
                    Commands.argument("prefix", StringArgumentType.string())
                        .executes { PermissionsCommand.groupPrefixSet(it) }
                ).executes { PermissionsCommand.groupPrefixTake(it) }
            )
        )
    )

fun takeConfigurePermissionsLiteral(): LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("configure-permissions")
        .then(
            Commands.literal("use-simplified-world-permissions").then(
                Commands.literal("set").then(
                    Commands.argument("value", StringArgumentType.string()).executes {
                        ConfigurePermissionsCommand.useSimplifiedWorldPermissions(it)
                    }
                )
            )
        ).then(
            Commands.literal("replace-world-edit-permissions-handler").then(
                Commands.literal("set").then(
                    Commands.argument("value", StringArgumentType.string()).executes {
                        ConfigurePermissionsCommand.replaceWorldEditPermissionsHandler(it)
                    }
                )
            )
        ).then(
            Commands.literal("debug-mode").then(
                Commands.literal("set").then(
                    Commands.argument("value", StringArgumentType.string()).executes {
                        ConfigurePermissionsCommand.debugMode(it)
                    }
                )
            )
        ).then(
            Commands.literal("handle-block-breaking").then(
                Commands.literal("set").then(
                    Commands.argument("value", StringArgumentType.string()).executes {
                        ConfigurePermissionsCommand.handleBlockBreaking(it)
                    }
                )
            )
        ).then(
            Commands.literal("handle-block-placing").then(
                Commands.literal("set").then(
                    Commands.argument("value", StringArgumentType.string()).executes {
                        ConfigurePermissionsCommand.handleBlockPlacing(it)
                    }
                )
            )
        ).then(
            Commands.literal("handle-farmland-trampling").then(
                Commands.literal("set").then(
                    Commands.argument("value", StringArgumentType.string()).executes {
                        ConfigurePermissionsCommand.handleFarmlandTrampling(it)
                    }
                )
            )
        ).then(
            Commands.literal("handle-item-using").then(
                Commands.literal("set").then(
                    Commands.argument("value", StringArgumentType.string()).executes {
                        ConfigurePermissionsCommand.handleItemUsing(it)
                    }
                )
            )
        )
