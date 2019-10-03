package com.mairwunnx.projectessentialspermissions.permissions

import com.mairwunnx.projectessentialspermissions.helpers.MOD_CONFIG_FOLDER
import com.mairwunnx.projectessentialspermissions.helpers.PERMISSIONS_CONFIG
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
        logger.info("    - loading user permissions data ...")
        logger.debug("        - setup json configuration for parsing ...")
        if (!File(PERMISSIONS_CONFIG).exists()) {
            logger.warn("        - permission config not exist! creating it now!")
            createConfigDirs(MOD_CONFIG_FOLDER)
            val defaultConfig = json.stringify(
                PermissionData.serializer(),
                permissionData
            )
            File(PERMISSIONS_CONFIG).writeText(defaultConfig)
        }
        val permConfigRaw = File(PERMISSIONS_CONFIG).readText()
        permissionData = Json.parse(PermissionData.serializer(), permConfigRaw)
    }

    internal fun saveData() {
        logger.info("    - saving user permissions data ...")
        createConfigDirs(MOD_CONFIG_FOLDER)
        val permConfig = json.stringify(
            PermissionData.serializer(),
            permissionData
        )
        File(PERMISSIONS_CONFIG).writeText(permConfig)
    }

    private fun createConfigDirs(path: String) {
        logger.info("        - creating config directory for user data ($path)")
        val configDirectory = File(path)
        if (!configDirectory.exists()) configDirectory.mkdirs()
    }
}
