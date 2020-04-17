@file:Suppress("unused")

package com.mairwunnx.projectessentials.permissions.impl.configurations

import com.mairwunnx.projectessentials.core.api.v1.configuration.Configuration
import com.mairwunnx.projectessentials.core.api.v1.configuration.IConfiguration
import com.mairwunnx.projectessentials.core.api.v1.helpers.jsonInstance
import com.mairwunnx.projectessentials.core.api.v1.helpers.projectConfigDirectory
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileNotFoundException

@OptIn(ExperimentalUnsignedTypes::class)
@Configuration("permissions-settings", 1u)
object PermissionsSettingsConfiguration : IConfiguration<PermissionsSettingsConfigurationModel> {
    private val logger = LogManager.getLogger()
    private var cachedData: Configuration? = null
    private var configurationData = PermissionsSettingsConfigurationModel()

    override val configuration = take()
    override val path = projectConfigDirectory + File.separator + "permissions-settings.json"

    override fun load() {
        try {
            val configRaw = File(path).readText()
            configurationData = jsonInstance.parse(
                PermissionsSettingsConfigurationModel.serializer(), configRaw
            )
        } catch (ex: FileNotFoundException) {
            logger.error("Configuration file ($path) not found!")
            logger.warn("The default configuration will be used")
        }
    }

    override fun save() {
        File(path).parentFile.mkdirs()

        logger.info("Saving configuration `${data().name}`")
        val raw = jsonInstance.stringify(
            PermissionsSettingsConfigurationModel.serializer(), configuration
        )
        try {
            File(path).writeText(raw)
        } catch (ex: SecurityException) {
            logger.error(
                "An error occurred while saving commands configuration", ex
            )
        }
    }

    override fun data(): Configuration {
        if (cachedData == null) {
            cachedData = this.javaClass.getAnnotation(Configuration::class.java)
        }
        return cachedData!!
    }

    override fun take() = configurationData
}
