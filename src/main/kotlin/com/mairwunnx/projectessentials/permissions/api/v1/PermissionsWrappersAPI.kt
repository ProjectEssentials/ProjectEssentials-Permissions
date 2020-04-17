package com.mairwunnx.projectessentials.permissions.api.v1

import com.mairwunnx.projectessentials.core.api.v1.extensions.empty
import com.mojang.authlib.GameProfile
import com.sk89q.worldedit.forge.ForgePermissionsProvider
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.server.permission.DefaultPermissionLevel
import net.minecraftforge.server.permission.IPermissionHandler
import net.minecraftforge.server.permission.context.IContext

object PermissionsWrappersAPI {
    object ForgeWrapper : IPermissionHandler {
        override fun getRegisteredNodes() = emptyList<String>()

        override fun registerNode(node: String, level: DefaultPermissionLevel, desc: String) = Unit

        override fun hasPermission(
            profile: GameProfile, node: String, context: IContext?
        ) = PermissionsAPI.hasPermission(profile.name, node)

        override fun getNodeDescription(node: String) = String.empty
    }

    object WorldEditWrapper : ForgePermissionsProvider {
        override fun registerPermission(p0: String) = Unit

        override fun hasPermission(
            p0: ServerPlayerEntity, p1: String
        ) = PermissionsAPI.hasPermission(p0.name.string, p1)
    }
}
