package com.mairwunnx.projectessentials.permissions.impl.commands

import com.mairwunnx.projectessentials.core.api.v1.commands.Command
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.CommandSource

@Command("permissions", ["perm", "perms", "permission"])
object PermissionsCommand : CommandBase(
    takePermissionsLiteral(), false
) {
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
}
