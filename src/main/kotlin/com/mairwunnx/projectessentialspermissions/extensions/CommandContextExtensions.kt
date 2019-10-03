package com.mairwunnx.projectessentialspermissions.extensions

import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity

/**
 * Return true if command sender is player.
 */
internal fun CommandContext<CommandSource>.isPlayerSender(): Boolean =
    this.source.entity is ServerPlayerEntity

internal fun CommandContext<CommandSource>.playerName() = this.source.asPlayer().name.string
