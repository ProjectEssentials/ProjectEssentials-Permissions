package com.mairwunnx.projectessentials.permissions.permissions

import com.mairwunnx.projectessentials.core.helpers.MOD_CONFIG_FOLDER
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.apache.logging.log4j.LogManager
import java.io.File

@UseExperimental(UnstableDefault::class)
internal object PermissionBase {
    private val logger = LogManager.getLogger()
    internal var permissionData = PermissionModel()
    private val json = Json(
        JsonConfiguration(
            strictMode = false,
            allowStructuredMapKeys = true,
            prettyPrint = true
        )
    )

    internal fun loadData() {
        val permissionConfig = MOD_CONFIG_FOLDER + File.separator + "permissions.json"
        logger.info("Loading user permissions data ...")
        logger.debug("Setup json configuration for parsing ...")
        if (!File(permissionConfig).exists()) {
            logger.warn("Permission config not exist! creating it now!")
            createConfigDirs(MOD_CONFIG_FOLDER)
            val defaultConfig = json.stringify(
                PermissionModel.serializer(),
                permissionData
            )
            File(permissionConfig).writeText(defaultConfig)
        }
        val permConfigRaw = File(permissionConfig).readText()
        permissionData = json.parse(PermissionModel.serializer(), permConfigRaw)
        logger.info("*** PermissionsAPI by Project Essentials!")
        logger.info("    - loaded groups (${permissionData.groups.size})")
        permissionData.groups.forEach {
            logger.info("        - name: ${it.name}; nodes: ${it.permissions.size}")
        }
        logger.info("    - loaded users (${permissionData.users.size})")
    }

    internal fun saveData() {
        val permissionConfig = MOD_CONFIG_FOLDER + File.separator + "permissions.json"
        createConfigDirs(MOD_CONFIG_FOLDER)
        val permConfig = json.stringify(
            PermissionModel.serializer(),
            permissionData
        )
        File(permissionConfig).writeText(permConfig)
    }

    @Suppress("SameParameterValue")
    private fun createConfigDirs(path: String) {
        logger.info("Creating config directory for user data ($path)")
        val configDirectory = File(path)
        if (!configDirectory.exists()) configDirectory.mkdirs()
    }
}
