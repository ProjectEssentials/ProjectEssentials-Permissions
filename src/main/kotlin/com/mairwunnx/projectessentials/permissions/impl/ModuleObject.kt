@file:Suppress("unused")

package com.mairwunnx.projectessentials.permissions.impl

import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.SETTING_LOC_ENABLED
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI
import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI
import com.mairwunnx.projectessentials.core.api.v1.events.forge.FMLCommonSetupEventData
import com.mairwunnx.projectessentials.core.api.v1.events.forge.ForgeEventType
import com.mairwunnx.projectessentials.core.api.v1.extensions.currentDimensionName
import com.mairwunnx.projectessentials.core.api.v1.localization.Localization
import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.module.IModule
import com.mairwunnx.projectessentials.core.api.v1.module.Module
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderAPI
import com.mairwunnx.projectessentials.core.impl.configurations.GeneralConfiguration
import com.mairwunnx.projectessentials.permissions.api.v1.PermissionsAPI
import com.mairwunnx.projectessentials.permissions.api.v1.PermissionsWrappersAPI
import com.mairwunnx.projectessentials.permissions.impl.commands.ConfigurePermissionsCommand
import com.mairwunnx.projectessentials.permissions.impl.commands.PermissionsCommand
import com.mairwunnx.projectessentials.permissions.impl.configurations.PermissionsConfiguration
import com.mairwunnx.projectessentials.permissions.impl.configurations.PermissionsSettingsConfiguration
import com.sk89q.worldedit.forge.ForgeWorldEdit
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.server.permission.PermissionAPI
import org.apache.logging.log4j.LogManager

@OptIn(ExperimentalUnsignedTypes::class)
@Mod("project_essentials_permissions")
@Module("permissions", "2.0.0-SNAPSHOT.2+MC-1.14.4", 1u, "1.0.0")
internal class ModuleObject : IModule {
    private val providers = listOf(
        PermissionsConfiguration::class,
        PermissionsSettingsConfiguration::class,
        ConfigurePermissionsCommand::class,
        PermissionsCommand::class,
        ModuleObject::class
    )
    private val generalConfiguration by lazy {
        ConfigurationAPI.getConfigurationByName<GeneralConfiguration>("general")
    }
    private val permissionsSettings by lazy {
        ConfigurationAPI.getConfigurationByName<PermissionsSettingsConfiguration>("permissions-settings")
    }
    private val logger = LogManager.getLogger()
    private var moduleDataCached: Module? = null

    init {
        logger.info("Replacing default Forge permissions handler").run {
            PermissionAPI.setPermissionHandler(PermissionsWrappersAPI.ForgeWrapper)
        }
        providers.forEach { ProviderAPI.addProvider(it) }
        subscribeEvents()
        EVENT_BUS.register(this)
        ModList.get().mods.find { it.modId == "worldedit" }?.let {
            if (permissionsSettings.configuration.replaceWorldEditPermissionsHandler) {
                logger.info("WorldEdit mod found and able to replacing permissions handler")
                EVENT_BUS.register(WorldEditEventHandler::class.java)
            }
        }
    }

    fun replaceWorldEditPermissionHandler() =
        logger.info("Replacing default WorldEdit permissions handler").run {
            ForgeWorldEdit.inst.permissionsProvider = PermissionsWrappersAPI.WorldEditWrapper
        }

    private fun subscribeEvents() {
        ModuleEventAPI.subscribeOn<FMLCommonSetupEventData>(
            ForgeEventType.SetupEvent
        ) {
            LocalizationAPI.apply(
                Localization(
                    mutableListOf(
                        "/assets/projectessentialspermissions/lang/de_de.json",
                        "/assets/projectessentialspermissions/lang/en_us.json",
                        "/assets/projectessentialspermissions/lang/ru_ru.json",
                        "/assets/projectessentialspermissions/lang/sr_rs.json",
                        "/assets/projectessentialspermissions/lang/zh_cn.json"
                    ), "permissions", ModuleObject::class.java
                )
            )
        }
    }

    override fun getModule() = this

    override fun getModuleData(): Module {
        if (moduleDataCached == null) {
            moduleDataCached = this.javaClass.getAnnotation(Module::class.java)
        }
        return moduleDataCached!!
    }

    override fun init() = Unit

    @SubscribeEvent
    fun onBlockBreakEvent(event: BlockEvent.BreakEvent) {
        when {
            !PermissionsAPI.hasPermission(
                event.player.name.string,
                if (permissionsSettings.configuration.useSimplifiedWorldPermissions) {
                    "native.event.world.block.break"
                } else {
                    "native.event.world.${event.player.currentDimensionName}.block.${event.state.block.registryName}.break".also {
                        if (permissionsSettings.configuration.debugMode) {
                            logger.debug(it)
                        }
                    }
                }
            ) -> {
                MessagingAPI.sendMessage(
                    event.player as ServerPlayerEntity,
                    "${MESSAGE_MODULE_PREFIX}permissions.perm.block_break.restricted",
                    generalConfiguration.getBool(SETTING_LOC_ENABLED)
                )
                event.isCanceled = true
                return
            }
        }
    }

    @SubscribeEvent
    fun onBlockPlaceEvent(event: BlockEvent.EntityPlaceEvent) {
        if (event.entity is ServerPlayerEntity) {
            val player = event.entity as ServerPlayerEntity
            when {
                !PermissionsAPI.hasPermission(
                    player.name.string,
                    if (permissionsSettings.configuration.useSimplifiedWorldPermissions) {
                        "native.event.world.block.place"
                    } else {
                        "native.event.world.${player.currentDimensionName}.block.${event.state.block.registryName}.place".also {
                            if (permissionsSettings.configuration.debugMode) {
                                logger.debug(it)
                            }
                        }
                    }
                ) -> {
                    MessagingAPI.sendMessage(
                        player,
                        "${MESSAGE_MODULE_PREFIX}permissions.perm.block_break.place",
                        generalConfiguration.getBool(SETTING_LOC_ENABLED)
                    )
                    event.isCanceled = true
                    return
                }
            }
        }
    }

    @SubscribeEvent
    fun onFarmlandTrampleEvent(event: BlockEvent.FarmlandTrampleEvent) {
        if (event.entity is ServerPlayerEntity) {
            val player = event.entity as ServerPlayerEntity
            when {
                !PermissionsAPI.hasPermission(
                    player.name.string,
                    if (permissionsSettings.configuration.useSimplifiedWorldPermissions) {
                        "native.event.world.block.farmland.trample"
                    } else {
                        "native.event.world.${player.currentDimensionName}.block.farmland.trample".also {
                            if (permissionsSettings.configuration.debugMode) {
                                logger.debug(it)
                            }
                        }
                    }
                ) -> {
                    event.isCanceled = true
                    return
                }
            }
        }
    }

    @SubscribeEvent
    fun onItemUseEvent(event: LivingEntityUseItemEvent.Start) {
        if (event.entity is ServerPlayerEntity) {
            val player = event.entity as ServerPlayerEntity
            when {
                !PermissionsAPI.hasPermission(
                    player.name.string,
                    if (permissionsSettings.configuration.useSimplifiedWorldPermissions) {
                        "native.event.world.item.use"
                    } else {
                        "native.event.world.${player.currentDimensionName}.item.${event.item.item.registryName}.use".also {
                            if (permissionsSettings.configuration.debugMode) {
                                logger.debug(it)
                            }
                        }
                    }
                ) -> {
                    event.isCanceled = true
                    return
                }
            }
        }
    }
}
