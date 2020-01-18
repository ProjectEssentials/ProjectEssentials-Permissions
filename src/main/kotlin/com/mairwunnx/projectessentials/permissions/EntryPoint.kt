package com.mairwunnx.projectessentials.permissions

import com.mairwunnx.projectessentials.core.EssBase
import com.mairwunnx.projectessentials.permissions.commands.PermissionsCommand
import com.mairwunnx.projectessentials.permissions.permissions.PermissionBase
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent
import org.apache.logging.log4j.LogManager

@Suppress("unused")
@Mod("project_essentials_permissions")
internal class EntryPoint : EssBase() {
    private val logger = LogManager.getLogger()

    init {
        modInstance = this
        modVersion = "1.14.4-1.1.0.0"
        logBaseInfo()
        validateForgeVersion()
        MinecraftForge.EVENT_BUS.register(this)
        PermissionBase.loadData()
    }

    @SubscribeEvent
    internal fun onServerStarting(it: FMLServerStartingEvent) {
        loadAdditionalModules()
        PermissionsCommand.register(it.server.commandManager.dispatcher)
        PermissionsAPI.oppedPlayers.addAll(it.server.playerList.oppedPlayerNames)
    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    internal fun onServerStopping(it: FMLServerStoppingEvent) {
        PermissionBase.saveData()
    }

    internal companion object {
        internal lateinit var modInstance: EntryPoint
        var cooldownsInstalled: Boolean = false
    }

    private fun loadAdditionalModules() {
        try {
            Class.forName(
                "com.mairwunnx.projectessentials.cooldown.essentials.CommandsAliases"
            )
            cooldownsInstalled = true
        } catch (_: ClassNotFoundException) {
            // ignored
        }
    }
}
