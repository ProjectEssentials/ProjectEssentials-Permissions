package com.mairwunnx.projectessentialspermissions.permissions

import com.mairwunnx.projectessentialscore.extensions.empty

/**
 * Base class for working with user permissions.
 * @since 1.14.4-0.1.0.0
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object PermissionsAPI {
    /**
     * @param playerNickName nickname of target player.
     * @return instance of the class of the rights group
     * the user belongs to.
     * @since 1.14.4-0.1.0.0
     */
    fun getUserGroup(
        playerNickName: String
    ): PermissionData.Group {
        PermissionBase.permissionData.users.forEach { user ->
            if (user.nickname == playerNickName) {
                PermissionBase.permissionData.groups.forEach { group ->
                    if (group.name == user.group) return group
                }
                PermissionBase.permissionData.groups.forEach { group ->
                    if (group.isDefault) return group
                }
            }
        }
        return PermissionData.Group()
    }

    /**
     * @param groupName just group name.
     * @return list with all able group permissions.
     * @since 1.14.4-0.1.0.0
     */
    fun getGroupPermissions(
        groupName: String
    ): List<String> {
        PermissionBase.permissionData.groups.forEach { group ->
            if (group.name == groupName) return group.permissions
        }
        PermissionBase.permissionData.groups.forEach { group ->
            if (group.isDefault) return group.permissions
        }
        return emptyList()
    }

    /**
     * @param groupInstance just group class instance.
     * @return list with all able group permissions.
     * @since 1.14.4-0.1.0.0
     */
    fun getGroupPermissions(
        groupInstance: PermissionData.Group
    ): List<String> {
        PermissionBase.permissionData.groups.forEach { group ->
            if (group.name == groupInstance.name) return group.permissions
        }
        PermissionBase.permissionData.groups.forEach { group ->
            if (group.isDefault) return group.permissions
        }
        return emptyList()
    }

    /**
     * @param playerNickName nickname of target player.
     * @return list with all able user permissions.
     * @since 1.14.4-0.1.0.0
     */
    fun getUserPermissions(
        playerNickName: String
    ): List<String> {
        PermissionBase.permissionData.users.forEach { user ->
            if (user.nickname == playerNickName) return user.permissions
        }
        PermissionBase.permissionData.users.forEach { user ->
            if (user.nickname == "*") return user.permissions
        }
        return emptyList()
    }

    /**
     * @param playerNickName nickname of target player.
     * @return list with all able user and group for
     * user permissions.
     * @since 1.14.4-0.1.0.0
     */
    fun getAllUserPermissions(
        playerNickName: String
    ): List<String> {
        var groupName = String.empty
        var defaultPerms = emptyList<String>()
        PermissionBase.permissionData.users.forEach { user ->
            if (user.nickname == "*") defaultPerms = user.permissions
            if (user.nickname == playerNickName) groupName = user.group
        }
        val groupPerms = getGroupPermissions(groupName)
        val userPerms = getUserPermissions(playerNickName)
        return listOf(defaultPerms, groupPerms, userPerms).flatten()
    }

    /**
     * **NOTE:** if default group not exist then return group
     * without permissions and without name.
     * @return default group in what defined configuration file.
     * @since 1.14.4-0.1.0.0
     */
    fun getDefaultGroup(): PermissionData.Group {
        PermissionBase.permissionData.groups.forEach {
            if (it.isDefault) return it
        }
        return PermissionData.Group()
    }

    /**
     * @param playerNickName nickname of target player.
     * @param node permission node as string, e.g `ess.weather`
     * @param isServerSender needed for additional checking permissions.
     * @return true if user have permission, else
     * return false.
     * @since 1.14.4-0.1.0.0
     */
    fun hasPermission(
        playerNickName: String,
        node: String,
        isServerSender: Boolean = false
    ): Boolean {
        if (isServerSender) return true
        val permissions = getAllUserPermissions(playerNickName)
        return permissions.contains(node) || permissions.contains("*")
    }

    /**
     * Install \ Add new permission for group.
     * @param groupName just group name.
     * @param node new group permission.
     * @since 1.14.4-0.1.0.0
     */
    fun setGroupPermissionNode(
        groupName: String,
        node: String
    ) {
        PermissionBase.permissionData.groups.forEach { group ->
            if (group.name == groupName) group.permissions += node
        }
    }

    /**
     * Install \ Add new permission for group.
     * @param groupInstance just group class instance.
     * @param node new group permission.
     * @since 1.14.4-0.1.0.0
     */
    fun setGroupPermissionNode(
        groupInstance: PermissionData.Group,
        node: String
    ) {
        PermissionBase.permissionData.groups.forEach { group ->
            if (group.name == groupInstance.name) group.permissions += node
        }
    }

    /**
     * Install \ Add new permission for user.
     * @param playerNickName nickname of target player.
     * @param node new user permission.
     * setting up for "*" (any) player.
     * @since 1.14.4-0.1.0.0
     */
    fun setUserPermissionNode(
        playerNickName: String,
        node: String
    ) {
        PermissionBase.permissionData.users.forEach { user ->
            if (user.nickname == playerNickName) {
                user.permissions += node
                return
            }
        }
        PermissionBase.permissionData.users += PermissionData.User(
            playerNickName,
            getDefaultGroup().name,
            listOf(node)
        )
    }

    /**
     * Install \ Set new permission group for user.
     * @param playerNickName nickname of target player.
     * @param groupName new user permission group.
     * @since 1.14.4-0.1.0.0
     */
    fun setUserPermissionGroup(
        playerNickName: String,
        groupName: String
    ) {
        PermissionBase.permissionData.users.forEach {
            if (it.nickname == playerNickName) {
                it.group = groupName
                return
            }
        }
        PermissionBase.permissionData.users += PermissionData.User(
            playerNickName,
            groupName,
            emptyList()
        )
    }

    /**
     * Install \ Set new permission group for user.
     * @param playerNickName nickname of target player.
     * @param groupInstance new user permission group.
     * @since 1.14.4-0.1.0.0
     */
    fun setUserPermissionGroup(
        playerNickName: String,
        groupInstance: PermissionData.Group
    ) {
        PermissionBase.permissionData.users.forEach {
            if (it.nickname == playerNickName) {
                it.group = groupInstance.name
                return
            }
        }
        PermissionBase.permissionData.users += PermissionData.User(
            playerNickName,
            groupInstance.name,
            emptyList()
        )
    }

    /**
     * Remove permission node from group.
     * @param groupName just group name.
     * @param node group permission.
     * @since 1.14.4-0.1.0.0
     */
    fun removeGroupPermission(
        groupName: String,
        node: String
    ) {
        PermissionBase.permissionData.groups.forEach { group ->
            if (group.name == groupName) {
                val permissions = group.permissions.toMutableList()
                permissions.remove(node)
                group.permissions = permissions
                return
            }
        }
    }

    /**
     * Remove permission node from group.
     * @param groupInstance just group class instance.
     * @param node group permission.
     * @since 1.14.4-0.1.0.0
     */
    fun removeGroupPermission(
        groupInstance: PermissionData.Group,
        node: String
    ) {
        PermissionBase.permissionData.groups.forEach { group ->
            if (group.name == groupInstance.name) {
                val permissions = group.permissions.toMutableList()
                permissions.remove(node)
                group.permissions = permissions
                return
            }
        }
    }

    /**
     * Remove permission node from user.
     * @param playerNickName nickname of target player.
     * @param node user permission.
     * setting up for "*" (any) player.
     * @since 1.14.4-0.1.0.0
     */
    fun removeUserPermission(
        playerNickName: String,
        node: String
    ) {
        PermissionBase.permissionData.users.forEach { user ->
            if (user.nickname == playerNickName) {
                val permissions = user.permissions.toMutableList()
                permissions.remove(node)
                user.permissions = permissions
                return
            }
        }
    }
}
