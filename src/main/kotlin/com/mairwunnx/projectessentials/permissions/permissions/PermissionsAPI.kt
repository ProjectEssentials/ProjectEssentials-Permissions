package com.mairwunnx.projectessentials.permissions.permissions

import com.mairwunnx.projectessentials.core.extensions.empty

/**
 * Base class for working with user permissions.
 * @since 1.14.4-0.1.0.0
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object PermissionsAPI {
    /**
     * Contain all opped players, for advanced permission checking.
     * @since 1.14.4-1.0.0.0
     */
    var oppedPlayers: MutableList<String> = mutableListOf()

    /**
     * @param playerNickName nickname of target player.
     * @return instance of the class of the rights group
     * the user belongs to.
     * @since 1.14.4-0.1.0.0
     */
    fun getUserGroup(
        playerNickName: String
    ): PermissionModel.Group {
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
        return PermissionModel.Group()
    }

    /**
     * @param groupName just group name.
     * @return list with all able group permissions.
     * @since 1.14.4-0.1.0.0
     */
    fun getGroupPermissions(
        groupName: String,
        includeInherit: Boolean = false
    ): List<String> {
        if (includeInherit) return getAllGroupPermissions(groupName)

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
        groupInstance: PermissionModel.Group,
        includeInherit: Boolean = false
    ): List<String> {
        if (includeInherit) return getAllGroupPermissions(groupInstance)

        PermissionBase.permissionData.groups.forEach { group ->
            if (group.name == groupInstance.name) return group.permissions
        }
        PermissionBase.permissionData.groups.forEach { group ->
            if (group.isDefault) return group.permissions
        }
        return emptyList()
    }

    /**
     * @param groupName just group name.
     * @return list with all group permissions including
     * inherit groups permissions.
     * @since 1.14.4-1.0.0.0
     */
    fun getAllGroupPermissions(
        groupName: String
    ): List<String> {
        val permissions = mutableListOf<String>()
        PermissionBase.permissionData.groups.forEach { group ->
            if (group.name == groupName) {
                permissions.addAll(group.permissions)
                group.inheritFrom.forEach {
                    permissions.addAll(getGroupPermissions(it))
                }
                return permissions
            }
        }
        return emptyList()
    }

    /**
     * @param groupInstance just group class instance.
     * @return list with all group permissions including
     * inherit groups permissions.
     * @since 1.14.4-1.0.0.0
     */
    fun getAllGroupPermissions(
        groupInstance: PermissionModel.Group
    ): List<String> {
        val permissions = mutableListOf<String>()
        PermissionBase.permissionData.groups.forEach { group ->
            if (group.name == groupInstance.name) {
                permissions.addAll(group.permissions)
                group.inheritFrom.forEach {
                    permissions.addAll(getGroupPermissions(it))
                }
                return permissions
            }
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
        val groupPerms = getAllGroupPermissions(groupName)
        val userPerms = getUserPermissions(playerNickName)

        // if player has operator right, then return ALL permissions.
        if (playerNickName in oppedPlayers) return listOf("*")

        return listOf(defaultPerms, groupPerms, userPerms).flatten()
    }

    /**
     * **NOTE:** if default group not exist then return group
     * without permissions and without name.
     * @return default group in what defined configuration file.
     * @since 1.14.4-0.1.0.0
     */
    fun getDefaultGroup(): PermissionModel.Group {
        PermissionBase.permissionData.groups.forEach {
            if (it.isDefault) return it
        }
        return PermissionModel.Group()
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

        var shortenedVariants = emptyList<String>()
        if (!node.contains(".*")) {
            shortenedVariants = node.split('.')
        }
        val permissions = getAllUserPermissions(playerNickName)

        for ((index, it) in shortenedVariants.withIndex()) {
            if (index == 0 && shortenedVariants.count() == 1) {
                if (permissions.contains("$it.*")) return true
            }

            var nodesPart = String.empty
            for (nodePartIndex in 0..index) {
                nodesPart += if (nodePartIndex == index) {
                    shortenedVariants[nodePartIndex] + ".*"
                } else {
                    shortenedVariants[nodePartIndex] + "."
                }
            }

            val permissionString = nodesPart
            if (permissions.contains(permissionString)) return true
        }

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
        groupInstance: PermissionModel.Group,
        node: String
    ) {
        PermissionBase.permissionData.groups.forEach { group ->
            if (group.name == groupInstance.name) group.permissions += node
        }
    }

    /**
     * Install new inherit groups permissions for group.
     * @param groupName just group name.
     * @param toInherit list with inherit group names.
     * @since 1.14.4-1.0.0.0
     */
    fun addInheritForGroup(
        groupName: String,
        toInherit: List<String>
    ) {
        PermissionBase.permissionData.groups.forEach { group ->
            if (group.name == groupName) group.inheritFrom += toInherit
        }
    }

    /**
     * Install new inherit groups permissions for group.
     * @param groupInstance just group class instance.
     * @param toInherit list with inherit group names.
     * @since 1.14.4-1.0.0.0
     */
    fun addInheritForGroup(
        groupInstance: PermissionModel.Group,
        toInherit: List<String>
    ) {
        PermissionBase.permissionData.groups.forEach { group ->
            if (group.name == groupInstance.name) group.inheritFrom += toInherit
        }
    }

    /**
     * Remove targeted inherit groups from group
     * inherit groups list.
     * @param groupName just group name.
     * @param inheritsToRemove list with inherit group names
     * what must be removed from group inherit list.
     * @since 1.14.4-1.0.0.0
     */
    fun removeInheritForGroup(
        groupName: String,
        inheritsToRemove: List<String>
    ) {
        PermissionBase.permissionData.groups.forEach { group ->
            if (group.name == groupName) {
                group.inheritFrom.toMutableList().removeAll(inheritsToRemove)
            }
        }
    }

    /**
     * Remove targeted inherit groups from group
     * inherit groups list.
     * @param groupInstance just group class instance.
     * @param inheritsToRemove list with inherit group names
     * what must be removed from group inherit list.
     * @since 1.14.4-1.0.0.0
     */
    fun removeInheritForGroup(
        groupInstance: PermissionModel.Group,
        inheritsToRemove: List<String>
    ) {
        PermissionBase.permissionData.groups.forEach { group ->
            if (group.name == groupInstance.name) {
                group.inheritFrom.toMutableList().removeAll(inheritsToRemove)
            }
        }
    }

    /**
     * Remove all inheritance groups from target group.
     * @param groupName just group name.
     * @since 1.14.4-1.0.0.0
     */
    fun removeAllInherits(
        groupName: String
    ) {
        PermissionBase.permissionData.groups.forEach { group ->
            if (group.name == groupName) {
                group.inheritFrom.toMutableList().clear()
            }
        }
    }

    /**
     * Remove all inheritance groups from target group.
     * @param groupInstance just group class instance.
     * @since 1.14.4-1.0.0.0
     */
    fun removeAllInherits(
        groupInstance: PermissionModel.Group
    ) {
        PermissionBase.permissionData.groups.forEach { group ->
            if (group.name == groupInstance.name) {
                group.inheritFrom.toMutableList().clear()
            }
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
        PermissionBase.permissionData.users += PermissionModel.User(
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
        PermissionBase.permissionData.users += PermissionModel.User(
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
        groupInstance: PermissionModel.Group
    ) {
        PermissionBase.permissionData.users.forEach {
            if (it.nickname == playerNickName) {
                it.group = groupInstance.name
                return
            }
        }
        PermissionBase.permissionData.users += PermissionModel.User(
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
        groupInstance: PermissionModel.Group,
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
