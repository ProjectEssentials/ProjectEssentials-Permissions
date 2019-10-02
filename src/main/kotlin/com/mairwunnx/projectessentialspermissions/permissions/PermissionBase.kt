package com.mairwunnx.projectessentialspermissions.permissions

import com.mairwunnx.projectessentialspermissions.helpers.PERMISSIONS_CONFIG
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.apache.logging.log4j.LogManager
import java.io.File

internal object PermissionBase {
    private val logger = LogManager.getLogger()
    internal var permissionData = PermissionData()

    @ImplicitReflectionSerializer
    @UnstableDefault
    internal fun loadData() {
        logger.info("    - loading user permissions data ...")
        logger.debug("        - setup json configuration for parsing ...")
        val json = Json(
            JsonConfiguration(
                encodeDefaults = true,
                strictMode = true,
                unquoted = false,
                allowStructuredMapKeys = true,
                prettyPrint = true,
                useArrayPolymorphism = false
            )
        )
        if (!File(PERMISSIONS_CONFIG).exists()) {
            createConfigDirs(
                PERMISSIONS_CONFIG
            )
            val defaultConfig = json.stringify(PermissionData.serializer(),
                permissionData
            )
            File(PERMISSIONS_CONFIG).writeText(defaultConfig)
        }
        val permConfigRaw = File(PERMISSIONS_CONFIG).readText()
        permissionData = Json.parse(PermissionData.serializer(), permConfigRaw)
    }

    @ImplicitReflectionSerializer
    @UnstableDefault
    internal fun saveData() {
        logger.info("    - saving user permissions data ...")
        createConfigDirs(
            PERMISSIONS_CONFIG
        )
        val json = Json(
            JsonConfiguration(
                encodeDefaults = true,
                strictMode = true,
                unquoted = false,
                allowStructuredMapKeys = true,
                prettyPrint = true,
                useArrayPolymorphism = false
            )
        )
        val permConfig = json.stringify(PermissionData.serializer(),
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
