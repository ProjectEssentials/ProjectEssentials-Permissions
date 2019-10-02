package com.mairwunnx.projectessentialspermissions.extensions

import net.minecraft.command.CommandSource
import net.minecraft.util.text.TranslationTextComponent

internal fun sendMsg(
    commandSource: CommandSource,
    l10nString: String,
    vararg args: String
) {
    commandSource.sendFeedback(
        TranslationTextComponent(
            "project_essentials_permissions.$l10nString", *args
        ), false
    )
}
