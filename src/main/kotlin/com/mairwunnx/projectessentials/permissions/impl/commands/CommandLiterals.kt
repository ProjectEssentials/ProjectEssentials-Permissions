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
        Commands.literal("save").executes(PermissionsCommand::save)
    ).then(
        Commands.literal("reload").executes(PermissionsCommand::reload)
    ).then(
        Commands.literal("user").then(
            Commands.literal("info").then(
                Commands.argument("user-name", StringArgumentType.string()).executes(
                    PermissionsCommand::userInfo
                )
            )
        ).then(
            Commands.literal("remove").then(
                Commands.argument("user-name", StringArgumentType.string()).executes(
                    PermissionsCommand::userRemove
                )
            )
        ).then(
            Commands.literal("permissions").then(
                Commands.argument(
                    "user-name", StringArgumentType.string()
                ).then(
                    Commands.literal("add").then(
                        Commands.argument("node", StringArgumentType.string()).executes(
                            PermissionsCommand::userPermissionsAdd
                        )
                    )
                ).then(
                    Commands.literal("remove").then(
                        Commands.argument("node", StringArgumentType.string())
                    ).executes(PermissionsCommand::userPermissionsRemove)
                ).then(
                    Commands.literal("list").then(
                        Commands.argument("deep", BoolArgumentType.bool()).executes(
                            PermissionsCommand::userPermissionsList
                        ).then(
                            Commands.argument("page", IntegerArgumentType.integer(0)).executes(
                                PermissionsCommand::userPermissionsList
                            )
                        )
                    ).then(
                        Commands.argument("page", IntegerArgumentType.integer(0)).executes(
                            PermissionsCommand::userPermissionsList
                        )
                    ).executes(PermissionsCommand::userPermissionsList)
                )
            )
        ).then(
            Commands.literal("group").then(
                Commands.literal("set").then(
                    Commands.argument(
                        "group-name", StringArgumentType.string()
                    ).then(
                        Commands.literal("for").then(
                            Commands.argument("user-name", StringArgumentType.string()).executes(
                                PermissionsCommand::userSetGroup
                            )
                        )
                    )
                )
            )
        )
    ).then(
        Commands.literal("group").then(
            Commands.literal("list").then(
                Commands.argument("page", IntegerArgumentType.integer(0)).executes(
                    PermissionsCommand::groupList
                )
            ).executes(PermissionsCommand::groupList)
        ).then(
            Commands.literal("set-default").then(
                Commands.argument(
                    "group-name", StringArgumentType.string()
                ).executes(PermissionsCommand::groupDefaultSet)
            )
        ).then(
            Commands.literal("create").then(
                Commands.argument("group-name", StringArgumentType.string()).executes(
                    PermissionsCommand::groupCreate
                )
            )
        ).then(
            Commands.literal("remove").then(
                Commands.argument(
                    "group-name", StringArgumentType.string()
                ).executes(PermissionsCommand::groupRemove)
            )
        ).then(
            Commands.literal("permissions").then(
                Commands.argument(
                    "group-name", StringArgumentType.string()
                ).then(
                    Commands.literal("add").then(
                        Commands.argument("node", StringArgumentType.string()).executes(
                            PermissionsCommand::groupPermissionsAdd
                        )
                    )
                ).then(
                    Commands.literal("remove").then(
                        Commands.argument("node", StringArgumentType.string()).executes(
                            PermissionsCommand::groupPermissionsRemove
                        )
                    )
                ).then(
                    Commands.literal("list").then(
                        Commands.argument("deep", BoolArgumentType.bool()).executes(
                            PermissionsCommand::groupPermissionsList
                        ).then(
                            Commands.argument("page", IntegerArgumentType.integer(0)).executes(
                                PermissionsCommand::groupPermissionsList
                            )
                        )
                    ).then(
                        Commands.argument("page", IntegerArgumentType.integer(0)).executes(
                            PermissionsCommand::groupPermissionsList
                        )
                    ).executes(PermissionsCommand::groupPermissionsList)
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
                        ).executes(PermissionsCommand::groupInheritAdd)
                    )
                ).then(
                    Commands.literal("remove").then(
                        Commands.argument(
                            "inherit-group", StringArgumentType.string()
                        ).executes(PermissionsCommand::groupInheritRemove)
                    )
                ).then(
                    Commands.literal("list").then(
                        Commands.argument("page", IntegerArgumentType.integer(0)).executes(
                            PermissionsCommand::groupInheritList
                        )
                    ).executes(PermissionsCommand::groupInheritList)
                )
            )
        ).then(
            Commands.literal("prefix").then(
                Commands.argument(
                    "group-name", StringArgumentType.string()
                ).then(
                    Commands.argument("prefix", StringArgumentType.string()).executes(
                        PermissionsCommand::groupPrefixSet
                    )
                ).executes(
                    PermissionsCommand::groupPrefixTake
                )
            )
        )
    )
