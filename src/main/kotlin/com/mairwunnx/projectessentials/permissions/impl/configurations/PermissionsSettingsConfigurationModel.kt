package com.mairwunnx.projectessentials.permissions.impl.configurations

import kotlinx.serialization.Serializable

@Serializable
data class PermissionsSettingsConfigurationModel(
    var useSimplifiedWorldPermissions: Boolean = false,
    var replaceWorldEditPermissionsHandler: Boolean = true,
    var permissionsListLinesPerPage: Int = 8,
    var debugMode: Boolean = false
)
