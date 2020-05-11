package com.mairwunnx.projectessentials.permissions.impl.configurations

import kotlinx.serialization.Serializable

@Serializable
data class PermissionsSettingsConfigurationModel(
    var useSimplifiedWorldPermissions: Boolean = false,
    var replaceWorldEditPermissionsHandler: Boolean = true,
    var displayObjectsLinesPerPage: Int = 8,
    var debugMode: Boolean = false,
    var handleBlockBreaking: Boolean = true,
    var handleBlockPlacing: Boolean = true,
    var handleFarmlandTrampling: Boolean = true,
    var handleItemUsing: Boolean = true
)
