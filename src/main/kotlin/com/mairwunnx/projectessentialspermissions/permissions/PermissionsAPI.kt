package com.mairwunnx.projectessentialspermissions.permissions

import com.mairwunnx.projectessentialspermissions.extensions.empty
import net.minecraft.entity.player.ServerPlayerEntity

/**
 * Base class for working with user permissions.
 * @since 1.14.4-0.1.0.0
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object PermissionsAPI {
    /**
     * @param player instance of ServerPlayerEntity class.
     * @return instance of the class of the rights group
     * the user belongs to.
     * @since 1.14.4-0.1.0.0
     */
    fun getUserGroup(
        player: ServerPlayerEntity
    ): PermissionData.Group {
        PermissionBase.permissionData.users.forEach { user ->
            if (user.uuid == player.uniqueID.toString()) {
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
     * @param player instance of ServerPlayerEntity class.
     * @return list with all able user permissions.
     * @since 1.14.4-0.1.0.0
     */
    fun getUserPermissions(
        player: ServerPlayerEntity
    ): List<String> {
        PermissionBase.permissionData.users.forEach { user ->
            if (user.uuid == player.uniqueID.toString()) return user.permissions
        }
        PermissionBase.permissionData.users.forEach { user ->
            if (user.uuid == "*") return user.permissions
        }
        return emptyList()
    }

    /**
     * @param player instance of ServerPlayerEntity class.
     * @return list with all able user and group for
     * user permissions.
     * @since 1.14.4-0.1.0.0
     */
    fun getAllUserPermissions(
        player: ServerPlayerEntity
    ): List<String> {
        var groupName = String.empty
        var defaultPerms = emptyList<String>()
        PermissionBase.permissionData.users.forEach { user ->
            if (user.uuid == "*") defaultPerms = user.permissions
            if (user.uuid == player.uniqueID.toString()) groupName = user.group
        }
        val groupPerms = getGroupPermissions(groupName)
        val userPerms = getUserPermissions(player)
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
     * @param player instance of ServerPlayerEntity class.
     * @param node permission node as string, e.g `ess.weather`
     * @return true if user have permission, else
     * return false.
     * @since 1.14.4-0.1.0.0
     */
    fun hasPermission(
        player: ServerPlayerEntity,
        node: String
    ): Boolean {
        val permissions = getAllUserPermissions(player)
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
     * @param player instance of ServerPlayerEntity class.
     * @param node new user permission.
     * @since 1.14.4-0.1.0.0
     */
    fun setUserPermissionNode(
        player: ServerPlayerEntity,
        node: String
    ) {
        PermissionBase.permissionData.users.forEach { user ->
            if (user.uuid == player.uniqueID.toString()) {
                user.permissions += node
                return
            }
        }
        PermissionBase.permissionData.users += PermissionData.User(
            player.uniqueID.toString(),
            getDefaultGroup().name,
            listOf(node)
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
     * @param player instance of ServerPlayerEntity class.
     * @param node user permission.
     * @since 1.14.4-0.1.0.0
     */
    fun removeUserPermission(
        player: ServerPlayerEntity,
        node: String
    ) {
        PermissionBase.permissionData.users.forEach { user ->
            if (user.uuid == player.uniqueID.toString()) {
                val permissions = user.permissions.toMutableList()
                permissions.remove(node)
                user.permissions = permissions
                return
            }
        }
    }
}
