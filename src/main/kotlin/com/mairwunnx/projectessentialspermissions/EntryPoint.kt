package com.mairwunnx.projectessentialspermissions

import com.mairwunnx.projectessentialscore.EssBase
import com.mairwunnx.projectessentialspermissions.commands.PermissionsCommand
import com.mairwunnx.projectessentialspermissions.permissions.PermissionBase
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.CommandSource
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
        modVersion = "1.14.4-0.1.1.0"
        logBaseInfo()
        validateForgeVersion()
        logger.debug("Register event bus for $modName mod ...")
        MinecraftForge.EVENT_BUS.register(this)
        logger.info("Loading $modName permissions data ...")
        PermissionBase.loadData()
    }

    @SubscribeEvent
    internal fun onServerStarting(it: FMLServerStartingEvent) {
        logger.info("$modName starting mod loading ...")
        registerCommands(it.server.commandManager.dispatcher)
    }

    private fun registerCommands(
        cmdDispatcher: CommandDispatcher<CommandSource>
    ) {
        logger.info("Command registering is starting ...")
        PermissionsCommand.register(cmdDispatcher)
    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    internal fun onServerStopping(it: FMLServerStoppingEvent) {
        logger.info("Shutting down $modName mod ...")
        logger.info("    - Saving permission users data ...")
        PermissionBase.saveData()
    }

    internal companion object {
        internal lateinit var modInstance: EntryPoint
    }
}
