package com.mairwunnx.projectessentials.permissions

import com.mairwunnx.projectessentials.core.EssBase
import com.mairwunnx.projectessentials.core.configuration.localization.LocalizationConfigurationUtils
import com.mairwunnx.projectessentials.core.localization.processLocalizations
import com.mairwunnx.projectessentials.permissions.commands.PermissionsCommand
import com.mairwunnx.projectessentials.permissions.permissions.PermissionBase
import com.mairwunnx.projectessentials.permissions.permissions.PermissionsAPI
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.BlockEvent
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
        modVersion = "1.15.2-1.0.1"
        logBaseInfo()
        validateForgeVersion()
        MinecraftForge.EVENT_BUS.register(this)
        PermissionBase.loadData()
        loadLocalization()
    }

    private fun loadLocalization() {
        if (LocalizationConfigurationUtils.getConfig().enabled) {
            processLocalizations(
                EntryPoint::class.java, listOf(
                    "/assets/projectessentialspermissions/lang/de_de.json",
                    "/assets/projectessentialspermissions/lang/en_us.json",
                    "/assets/projectessentialspermissions/lang/ru_ru.json",
                    "/assets/projectessentialspermissions/lang/sr_rs.json"
                )
            )
        }
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

    @SubscribeEvent
    internal fun onBlockPlaceEvent(event: BlockEvent.EntityPlaceEvent) {
        if (event.entity is ServerPlayerEntity) {
            val player = event.entity as ServerPlayerEntity
            when {
                !PermissionsAPI.hasPermission(
                    player.name.string, "native.event.modifyworld"
                ) && !PermissionsAPI.hasPermission(
                    player.name.string, "native.event.block.place"
                ) -> {
                    sendMessage(player.commandSource, "block_break.place")
                    event.isCanceled = true
                    return
                }
            }
        }
    }

    @SubscribeEvent
    internal fun onBlockBreakEvent(event: BlockEvent.BreakEvent) {
        when {
            !PermissionsAPI.hasPermission(
                event.player.name.string, "native.event.modifyworld"
            ) && !PermissionsAPI.hasPermission(
                event.player.name.string, "native.event.block.break"
            ) -> {
                sendMessage(event.player.commandSource, "block_break.restricted")
                event.isCanceled = true
                return
            }
        }
    }

    internal companion object {
        internal lateinit var modInstance: EntryPoint
        var cooldownsInstalled: Boolean = false
    }

    private fun loadAdditionalModules() {
        try {
            Class.forName(cooldownAPIClassPath)
            cooldownsInstalled = true
        } catch (_: ClassNotFoundException) {
            // ignored
        }
    }
}
