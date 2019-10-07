package com.mairwunnx.projectessentialspermissions.permissions

import com.mairwunnx.projectessentialscore.helpers.MOD_CONFIG_FOLDER
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.apache.logging.log4j.LogManager
import java.io.File

@UseExperimental(UnstableDefault::class)
internal object PermissionBase {
    private val logger = LogManager.getLogger()
    internal var permissionData = PermissionData()
    private val json = Json(
        JsonConfiguration(
            encodeDefaults = true,
            strictMode = true,
            unquoted = false,
            allowStructuredMapKeys = true,
            prettyPrint = true,
            useArrayPolymorphism = false
        )
    )

    internal fun loadData() {
        val permissionConfig = MOD_CONFIG_FOLDER + File.separator + "permissions.json"
        logger.info("    - loading user permissions data ...")
        logger.debug("        - setup json configuration for parsing ...")
        if (!File(permissionConfig).exists()) {
            logger.warn("        - permission config not exist! creating it now!")
            createConfigDirs(MOD_CONFIG_FOLDER)
            val defaultConfig = json.stringify(
                PermissionData.serializer(),
                permissionData
            )
            File(permissionConfig).writeText(defaultConfig)
        }
        val permConfigRaw = File(permissionConfig).readText()
        permissionData = Json.parse(PermissionData.serializer(), permConfigRaw)
    }

    internal fun saveData() {
        val permissionConfig = MOD_CONFIG_FOLDER + File.separator + "permissions.json"
        logger.info("    - saving user permissions data ...")
        createConfigDirs(MOD_CONFIG_FOLDER)
        val permConfig = json.stringify(
            PermissionData.serializer(),
            permissionData
        )
        File(permissionConfig).writeText(permConfig)
    }

    @Suppress("SameParameterValue")
    private fun createConfigDirs(path: String) {
        logger.info("        - creating config directory for user data ($path)")
        val configDirectory = File(path)
        if (!configDirectory.exists()) configDirectory.mkdirs()
    }
}
