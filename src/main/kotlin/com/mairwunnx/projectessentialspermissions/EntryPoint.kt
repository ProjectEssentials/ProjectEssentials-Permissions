package com.mairwunnx.projectessentialspermissions

import com.mairwunnx.projectessentialspermissions.commands.PermissionsCommand
import com.mairwunnx.projectessentialspermissions.helpers.validateForgeVersion
import com.mairwunnx.projectessentialspermissions.permissions.PermissionBase
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.CommandSource
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent
import org.apache.logging.log4j.LogManager

internal const val MOD_ID = "project_essentials_permissions"
internal const val MOD_NAME = "Project Essentials Permissions"
internal const val PART_OF_MOD = "Project Essentials"
internal const val MOD_VERSION = "1.14.4-0.1.0.0"
internal const val MOD_MAINTAINER = "MairwunNx (Pavel Erokhin)"
internal const val MOD_TARGET_FORGE = "28.0.X"
internal const val MOD_TARGET_FORGE_REGEX = "^28\\.0\\..\\d{1,}|28\\.0\\.[\\d]\$"
internal const val MOD_TARGET_MC = "1.14.4"
internal const val MOD_SOURCES_LINK = "https://github.com/MairwunNx/ProjectEssentials-Permissions/"
internal const val MOD_TELEGRAM_LINK = "https://t.me/minecraftforge"

@Suppress("unused")
@Mod(MOD_ID)
internal class EntryPoint {
    private val logger = LogManager.getLogger()

    init {
        logBaseInfo()
        validateForgeVersion()
        logger.debug("Register event bus for $MOD_NAME mod ...")
        MinecraftForge.EVENT_BUS.register(this)
        logger.info("Loading $MOD_NAME permissions data ...")
        PermissionBase.loadData()
    }

    private fun logBaseInfo() {
        logger.info("$MOD_NAME starting initializing ...")
        logger.info("    - Mod Id: $MOD_ID")
        logger.info("    - Version: $MOD_VERSION")
        logger.info("    - Maintainer: $MOD_MAINTAINER")
        logger.info("    - Target Forge version: $MOD_TARGET_FORGE")
        logger.info("    - Target Minecraft version: $MOD_TARGET_MC")
        logger.info("    - Source code: $MOD_SOURCES_LINK")
        logger.info("    - Telegram chat: $MOD_TELEGRAM_LINK")
    }

    @SubscribeEvent
    internal fun onServerStarting(it: FMLServerStartingEvent) {
        logger.info("$MOD_NAME starting mod loading ...")
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
        logger.info("Shutting down $MOD_NAME mod ...")
        logger.info("    - Saving permission users data ...")
        PermissionBase.saveData()
    }
}
