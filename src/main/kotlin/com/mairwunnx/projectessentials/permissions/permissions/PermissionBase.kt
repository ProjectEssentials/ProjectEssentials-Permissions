package com.mairwunnx.projectessentials.permissions.permissions

import com.mairwunnx.projectessentials.core.helpers.MOD_CONFIG_FOLDER
import com.mairwunnx.projectessentials.core.helpers.jsonInstance
import kotlinx.serialization.UnstableDefault
import org.apache.logging.log4j.LogManager
import java.io.File

@UseExperimental(UnstableDefault::class)
internal object PermissionBase {
    private val logger = LogManager.getLogger()
    internal var permissionData = PermissionModel()

    internal fun loadData() {
        val permissionConfig = MOD_CONFIG_FOLDER + File.separator + "permissions.json"
        logger.info("Loading user permissions data")
        if (!File(permissionConfig).exists()) {
            logger.warn("Permission config not exist! creating it now!")
            File(MOD_CONFIG_FOLDER).mkdirs()
            val defaultConfig = jsonInstance.stringify(
                PermissionModel.serializer(), permissionData
            )
            File(permissionConfig).writeText(defaultConfig)
        }
        val permConfigRaw = File(permissionConfig).readText()
        permissionData = jsonInstance.parse(PermissionModel.serializer(), permConfigRaw)
        logger.info("Loaded permission groups (${permissionData.groups.size})")
        logger.info("Loaded permission users (${permissionData.users.size})")
    }

    internal fun saveData() {
        val permissionConfig = MOD_CONFIG_FOLDER + File.separator + "permissions.json"
        File(MOD_CONFIG_FOLDER).mkdirs()
        val permConfig = jsonInstance.stringify(
            PermissionModel.serializer(), permissionData
        )
        File(permissionConfig).writeText(permConfig)
    }
}
