package com.mairwunnx.projectessentials.permissions.impl.commands

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands

fun takePermissionsLiteral(): LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("permissions").then(
        Commands.literal("save").executes { PermissionsCommand.save(it) }
    ).then(
        Commands.literal("reload").executes { PermissionsCommand.reload(it) }
    ).then(
        Commands.literal("user").then(
            Commands.literal("info").then(
                Commands.argument("user-name", StringArgumentType.string())
                    .executes { PermissionsCommand.userInfo(it) }
            )
        ).then(
            Commands.literal("remove").then(
                Commands.argument("user-name", StringArgumentType.string())
                    .executes { PermissionsCommand.userRemove(it) }
            )
        ).then(
            Commands.literal("permissions").then(
                Commands.argument(
                    "user-name", StringArgumentType.string()
                ).then(
                    Commands.literal("add").then(
                        Commands.argument("node", StringArgumentType.string())
                            .executes { PermissionsCommand.userPermissionsAdd(it) }
                    )
                ).then(
                    Commands.literal("remove").then(
                        Commands.argument("node", StringArgumentType.string())
                    ).executes { PermissionsCommand.userPermissionsRemove(it) }
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
                    ).then(
                        Commands.literal("for").then(
                            Commands.argument("user-name", StringArgumentType.string())
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
                ).executes { PermissionsCommand.groupDefaultSet(it) }
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
                ).executes { PermissionsCommand.groupRemove(it) }
            )
        ).then(
            Commands.literal("permissions").then(
                Commands.argument(
                    "group-name", StringArgumentType.string()
                ).then(
                    Commands.literal("add").then(
                        Commands.argument("node", StringArgumentType.string())
                            .executes { PermissionsCommand.groupPermissionsAdd(it) }
                    )
                ).then(
                    Commands.literal("remove").then(
                        Commands.argument("node", StringArgumentType.string())
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
                ).then(
                    Commands.literal("add").then(
                        Commands.argument(
                            "inherit-group", StringArgumentType.string()
                        ).executes { PermissionsCommand.groupInheritAdd(it) }
                    )
                ).then(
                    Commands.literal("remove").then(
                        Commands.argument(
                            "inherit-group", StringArgumentType.string()
                        ).executes { PermissionsCommand.groupInheritRemove(it) }
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
                ).then(
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
            Commands.literal("enable-permissions-command").then(
                Commands.literal("set").then(
                    Commands.argument("value", StringArgumentType.string()).executes {
                        ConfigurePermissionsCommand.enablePermissionsCommand(it)
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
