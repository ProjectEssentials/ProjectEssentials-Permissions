package com.mairwunnx.projectessentials.permissions.api.v1

import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI
import com.mairwunnx.projectessentials.core.api.v1.extensions.empty
import com.mairwunnx.projectessentials.permissions.impl.configurations.PermissionsConfiguration
import com.mairwunnx.projectessentials.permissions.impl.configurations.PermissionsConfigurationModel.Group
import com.mairwunnx.projectessentials.permissions.impl.configurations.PermissionsConfigurationModel.User
import net.minecraftforge.fml.server.ServerLifecycleHooks.getCurrentServer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager

/**
 * Base class for working with user permissions.
 * @since 1.14.4-0.1.0.0
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object PermissionsAPI {
    private val logger = LogManager.getLogger()
    private val marker = MarkerManager.getMarker("PERMISSION MANAGER")
    private val permissions by lazy {
        ConfigurationAPI.getConfigurationByName<PermissionsConfiguration>("permissions")
    }

    fun getGroups() = permissions.take().groups

    fun groupExist(name: String) = permissions.take().groups.filter { it.name == name }.count() > 0

    fun getDefaultGroup() = getGroups().find { it.isDefault } ?: Group(
        "default", true, mutableListOf(), mutableListOf()
    ).also {
        logger.warn(
            marker, "Default group not defined in permissions configuration! Please, define it."
        )
    }

    fun getGroupByName(name: String) = getGroups().find { it.name == name } ?: getDefaultGroup()

    fun getGroupInherits(name: String, deep: Boolean = false): Set<String> = if (!deep) {
        getGroupByName(name).inheritFrom.toSet()
    } else {
        getGroupByName(name).inheritFrom.map {
            if (getGroupInherits(it).isNotEmpty()) getGroupInherits(it, true).forEach { inherit ->
                return@map inherit
            }
            return@map it
        }.toSet()
    }

    fun getGroupPermissions(name: String, includeInherit: Boolean = false) =
        getGroupInherits(name, true).map { getGroupByName(it).permissions }.let {
            return@let if (includeInherit) (it.flatten() + getGroupByName(name).permissions)
            else getGroupByName(name).permissions
        }.toSet()

    fun getGroupUsers(name: String) = getUsers().filter { it.group == name }.toSet()

    fun getUsers() = permissions.take().users

    fun getDefaultUser() = getUsers().find { it.nickname == "*" } ?: User(
        "*", getDefaultGroup().name, mutableListOf()
    ).also {
        logger.warn(
            marker, "Default user not defined in permissions configuration! Please, define it."
        )
    }

    fun getUserByName(name: String) = getUsers().find { it.nickname == name } ?: getDefaultUser()

    fun getUserGroup(name: String) = getUserByName(name).group

    fun getUserPermissions(name: String, deep: Boolean = false): Set<String> = if (!deep) {
        getUserByName(name).permissions
    } else {
        getGroupPermissions(getUserGroup(name), true).plus(
            getDefaultUser().permissions.plus(
                getGroupPermissions(getDefaultUser().group, true)
            )
        ).plus(getUserByName(name).permissions).toMutableSet().also {
            if (name in getCurrentServer().playerList.oppedPlayerNames) it.add("*")
        }
    }.toSet()

    fun shortVariantsOf(node: String) = node.mapIndexed { i, c ->
        return@mapIndexed if (c == '.') node.take(i) + ".*" else String.empty
    }.filter { it != String.empty }.toSet()

    fun isRestrictedPermission(name: String, node: String) =
        "^$node" in getUserPermissions(name, true)

    fun hasPermission(name: String, node: String, isServerSender: Boolean = false): Boolean {
        return if (name == "#server" || isServerSender) true else {
            if (isRestrictedPermission(name, node)) return false
            with(getUserPermissions(name, true)) {
                return when {
                    shortVariantsOf(node).filter {
                        it in this
                    }.count() != 0 || node in this || "*" in this -> true
                    else -> false
                }
            }
        }
    }

    fun addGroup(group: Group) = groupExist(group.name).also { if (it) getGroups().add(group) }

    fun removeGroup(name: String): Boolean {
        return if (groupExist(name) && getGroups().removeIf { it.name == name && !it.isDefault }) {
            getGroupUsers(name).forEach { it.group = getDefaultGroup().name }
            getGroups().filter { name in it.inheritFrom }.forEach {
                it.inheritFrom.remove(name)
            }
            true
        } else false
    }

    fun getGroupPrefix(name: String): String =
        if (!groupExist(name)) String.empty else getGroupByName(name).prefix

    fun setGroupPrefix(name: String, prefix: String): Boolean {
        if (!groupExist(name)) return false
        getGroupByName(name).prefix = prefix
            .replace("&", "§")
            .replace("oliver heldens", "god", true)
        return true
    }

    fun renameGroup(oldName: String, newName: String): Boolean {
        if (!groupExist(oldName) || groupExist(newName)) return false
        getGroupByName(oldName).name = newName
        getGroupUsers(oldName).forEach { it.group = newName }
        getGroups().filter { oldName in it.inheritFrom }.forEach {
            it.inheritFrom.remove(oldName)
            it.inheritFrom.add(newName)
        }
        return true
    }

    fun addGroupPermission(name: String, node: String) {
        if (!groupExist(name)) addGroup(Group(name, false, mutableListOf(name)))
        getGroupByName(name).permissions.add(node)
    }

    fun removeGroupPermission(name: String, node: String): Any {
        if (!groupExist(name)) return false
        if (!getGroupByName(name).permissions.removeAll { perm -> perm == node }) {
            if (node in getGroupPermissions(name, true)) {
                addGroupPermission(name, "^$node")
                return true
            }
        } else return true
        return false
    }

    fun defaultFactorMove(
        newDefault: String,
        oldDefault: String = getDefaultGroup().name
    ): Boolean {
        if (!groupExist(oldDefault) || !groupExist(newDefault)) return false
        getGroupByName(oldDefault).isDefault = false
        getGroupByName(newDefault).isDefault = true
        return true
    }

    fun addGroupInheritances(name: String, vararg inheritances: String): Boolean {
        if (!groupExist(name)) return false
        getGroupByName(name).inheritFrom.addAll(inheritances)
        return true
    }

    fun removeGroupInheritances(name: String, vararg inheritances: String): Boolean {
        if (!groupExist(name)) return false
        getGroupByName(name).inheritFrom.removeAll(inheritances)
        return true
    }

    fun userExist(name: String) = getUsers().filter { it.nickname == name }.count() > 0

    fun addUser(user: User) = userExist(user.nickname).also { if (it) getUsers().add(user) }

    fun removeUser(name: String): Boolean = getUsers().removeIf { it.nickname == name }

    fun setUserGroup(name: String, groupName: String): Boolean {
        if (getUserGroup(name) == groupName) return false

        if (!userExist(name)) addUser(User(name, groupName, mutableListOf()))
        else getUserByName(name).group = groupName

        return true
    }

    fun addUserPermission(name: String, node: String) {
        if (!userExist(name)) addUser(User(name, getDefaultGroup().name, mutableListOf(node)))
        else getUserByName(name).permissions.add(node)
    }

    fun removeUserPermission(name: String, node: String): Boolean {
        if (!userExist(name)) {
            if (node in getUserPermissions(name, true)) {
                addUser(User(name, getDefaultGroup().name, mutableListOf("^$node")))
            } else {
                addUser(User(name, getDefaultGroup().name, mutableListOf(node)))
            }
        } else {
            with(getUserByName(name)) {
                val removed = this.permissions.removeAll { it == node }
                return if (node in getUserPermissions(name, true)) {
                    this.permissions.add("^$node")
                } else removed
            }
        }
        return false
    }
}
