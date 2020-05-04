package com.mairwunnx.projectessentials.permissions.impl.commands

import com.mairwunnx.projectessentials.core.api.v1.commands.arguments.StringArrayArgument
import com.mairwunnx.projectessentials.permissions.api.v1.PermissionsAPI
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands

fun takePermissionsLiteral(): LiteralArgumentBuilder<CommandSource> =
    literal<CommandSource>("permissions").then(
        Commands.literal("user").then(
            Commands.literal("remove").then(
                Commands.argument("user-name", StringArgumentType.string())
            )
        ).then(
            Commands.literal("permissions").then(
                Commands.argument(
                    "user-name", StringArgumentType.string()
                ).then(
                    Commands.literal("add").then(
                        Commands.argument("node", StringArgumentType.string())
                    )
                ).then(
                    Commands.literal("remove").then(
                        Commands.argument("node", StringArgumentType.string())
                    )
                ).then(Commands.literal("list"))
            )
        ).then(
            Commands.literal("group").then(
                Commands.literal("set").then(
                    Commands.argument(
                        "group-name",
                        StringArrayArgument.with(PermissionsAPI.getGroups().map { it.name })
                    ).then(
                        Commands.literal("for").then(
                            Commands.argument("user-name", StringArgumentType.string())
                        )
                    )
                )
            )
        )
    ).then(
        Commands.literal("group").then(
            Commands.literal("list")
        ).then(
            Commands.literal("set-default").then(
                Commands.argument(
                    "group-name",
                    StringArrayArgument.with(
                        PermissionsAPI.getGroups().filter { !it.isDefault }.map { it.name }
                    )
                )
            )
        ).then(
            Commands.literal("create").then(
                Commands.argument("group-name", StringArgumentType.string())
            )
        ).then(
            Commands.literal("remove").then(
                Commands.argument(
                    "group-name",
                    StringArrayArgument.with(
                        PermissionsAPI.getGroups().filter { !it.isDefault }.map { it.name }
                    )
                )
            )
        ).then(
            Commands.literal("permissions").then(
                Commands.argument(
                    "group-name",
                    StringArrayArgument.with(PermissionsAPI.getGroups().map { it.name })
                ).then(
                    Commands.literal("add").then(
                        Commands.argument("node", StringArgumentType.string())
                    )
                ).then(
                    Commands.literal("remove").then(
                        Commands.argument("node", StringArgumentType.string())
                    )
                ).then(Commands.literal("list"))
            )
        ).then(
            Commands.literal("inherit").then(
                Commands.argument(
                    "group-name",
                    StringArrayArgument.with(PermissionsAPI.getGroups().map { it.name })
                ).then(
                    Commands.literal("add").then(
                        Commands.argument(
                            "inherit-group",
                            StringArrayArgument.with(PermissionsAPI.getGroups().map { it.name })
                        )
                    )
                ).then(
                    Commands.literal("remove").then(
                        Commands.argument(
                            "inherit-group",
                            StringArrayArgument.with(PermissionsAPI.getGroups().map { it.name })
                        )
                    )
                ).then(Commands.literal("list"))
            )
        ).then(
            Commands.literal("prefix").then(
                Commands.argument("prefix", StringArgumentType.string())
            )
        ).then(
            Commands.literal("rename").then(
                Commands.argument(
                    "old-group-name",
                    StringArrayArgument.with(PermissionsAPI.getGroups().map { it.name })
                ).then(
                    Commands.argument(
                        "new-group-name",
                        StringArrayArgument.with(PermissionsAPI.getGroups().map { it.name })
                    )
                )
            )
        )
    )
