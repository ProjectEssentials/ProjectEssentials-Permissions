package com.mairwunnx.projectessentials.permissions.api.v1

import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.extensions.empty
import com.mairwunnx.projectessentials.permissions.impl.configurations.PermissionsConfiguration
import com.mairwunnx.projectessentials.permissions.impl.configurations.PermissionsConfigurationModel.Group
import com.mairwunnx.projectessentials.permissions.impl.configurations.PermissionsConfigurationModel.User
import net.minecraftforge.fml.server.ServerLifecycleHooks.getCurrentServer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager

/**
 * Base class for working with user permissions.
 * @since 2.0.0-SNAPSHOT.1.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object PermissionsAPI {
    private val logger = LogManager.getLogger()
    private val marker = MarkerManager.getMarker("PERMISSION MANAGER")
    private val permissions by lazy {
        getConfigurationByName<PermissionsConfiguration>("permissions")
    }

    /**
     * @return registered groups collection as mutable list.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getGroups() = permissions.take().groups

    /**
     * @param name requested group by name.
     * @return true if group with specified name exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun groupExist(name: String) = permissions.take().groups.filter { it.name == name }.count() > 0

    /**
     * @return default group data model instance.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getDefaultGroup() = getGroups().find { it.isDefault } ?: Group(
        "default", true, mutableListOf(), mutableListOf()
    ).also {
        logger.warn(
            marker, "Default group not defined in permissions configuration! Please, define it."
        )
    }

    /**
     * @param name requested group by name.
     * @return group data model instance if group exist.
     * If group not exist then will returned null.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getGroupByName(name: String) = getGroups().find { it.name == name }

    /**
     * @param name requested group by name.
     * @return group data model instance if group exist.
     * If group not exist then will returned default group.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getGroupByNameOrDefault(name: String) = getGroupByName(name) ?: getDefaultGroup()

    /**
     * @param name requested group by name.
     * @param deep deep inherits group checking.
     * @return set of inherited groups of requested group.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getGroupInherits(name: String, deep: Boolean = false): Set<String> = if (!deep) {
        getGroupByName(name)?.inheritFrom?.toSet() ?: emptySet()
    } else {
        getGroupByNameOrDefault(name).inheritFrom.map {
            if (getGroupInherits(it).isNotEmpty()) getGroupInherits(it, true).forEach { inherit ->
                return@map inherit
            }
            return@map it
        }.toSet()
    }

    /**
     * @param name requested group by name.
     * @param includeInherit deep permissions group checking.
     * @return set of permissions of requested group.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getGroupPermissions(name: String, includeInherit: Boolean = false) =
        getGroupInherits(name, true).map { getGroupByNameOrDefault(it).permissions }.let {
            return@let if (includeInherit) (it.flatten() + getGroupByNameOrDefault(name).permissions)
            else getGroupByName(name)?.permissions ?: emptySet<String>()
        }.toSet()

    /**
     * @param name requested group by name.
     * @return set of users (members) of requested group.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getGroupUsers(name: String) = getUsers().filter { it.group == name }.toSet()

    /**
     * @return mutable list with all registered users.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getUsers() = permissions.take().users

    /**
     * @return default user data model instance.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getDefaultUser() = getUsers().find { it.nickname == "*" } ?: User(
        "*", getDefaultGroup().name, mutableListOf()
    ).also {
        logger.warn(
            marker, "Default user not defined in permissions configuration! Please, define it."
        )
    }

    /**
     * @param name requested user by name.
     * @return if user exist then user data model instance of
     * requested user. If user not exist then will returned null.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getUserByName(name: String) = getUsers().find { it.nickname == name }

    /**
     * @param name requested user by name.
     * @return if user exist then user data model instance of
     * requested user. If user not exist then will default user
     * model instance.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getUserByNameOrDefault(name: String) = getUserByName(name) ?: getDefaultUser()

    /**
     * @param name requested user by name.
     * @return return requested user group name.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getUserGroup(name: String) = getUserByNameOrDefault(name).group

    /**
     * @param name requested user by name.
     * @return return requested user group name.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getUserPermissions(name: String, deep: Boolean = false): Set<String> = if (!deep) {
        getUserByName(name)?.permissions ?: emptySet<String>()
    } else {
        getGroupPermissions(getUserGroup(name), true).plus(
            getDefaultUser().permissions.plus(
                getGroupPermissions(getDefaultUser().group, true)
            )
        ).plus(getUserByName(name)?.permissions ?: mutableListOf()).toMutableSet().also {
            if (name in getCurrentServer().playerList.oppedPlayerNames) it.add("*")
        }
    }.toSet()

    /**
     * @param node permission node for taking short variants.
     * @return return set of short variants of permission node.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun shortVariantsOf(node: String) = node.mapIndexed { i, c ->
        return@mapIndexed if (c == '.') node.take(i) + ".*" else String.empty
    }.filter { it != String.empty }.toSet()

    /**
     * @param name user name for checking permission.
     * @param node permission node for checking this.
     * @return return true if permissions is inverted or
     * restricted.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun isRestrictedPermission(name: String, node: String) =
        "^$node" in getUserPermissions(name, true)

    /**
     * @param name user name for checking permission.
     * @param node permission node for checking it able.
     * @param isServerSender if this value will equals to
     * `true` then hasPermission will return true.
     * @return return true if target user has a requested
     * permission node.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun hasPermission(name: String, isServerSender: Boolean = false, node: () -> String): Boolean =
        hasPermission(name, node(), isServerSender)

    /**
     * @param name user name for checking permission.
     * @param node permission node for checking it able.
     * @param isServerSender if this value will equals to
     * `true` then hasPermission will return true.
     * @return return true if target user has a requested
     * permission node.
     * @since 2.0.0-SNAPSHOT.1.
     */
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

    /**
     * Adds new group to group list.
     *
     * @param group [Group] data class instance.
     * @return true if group added, false if group
     * already exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun addGroup(group: Group) =
        groupExist(group.name).also { return if (!it) getGroups().add(group) else false }

    /**
     * Removes group from group list.
     *
     * @param name group name to remove.
     * @return true if group removed, false if group
     * not exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun removeGroup(name: String): Boolean {
        return if (groupExist(name) && getGroups().removeIf { it.name == name && !it.isDefault }) {
            getGroupUsers(name).forEach { it.group = getDefaultGroup().name }
            getGroups().filter { name in it.inheritFrom }.forEach {
                it.inheritFrom.remove(name)
            }
            true
        } else false
    }

    /**
     * @param name requested group name for getting it prefix.
     * @return group prefix, if prefix not installed then
     * will return empty string.
     * @since 2.0.0-RC.1.
     */
    fun getGroupPrefix(name: String): String =
        if (!groupExist(name)) String.empty else getGroupByName(name)!!.prefix

    /**
     * Setting up new prefix to target group.
     *
     * @param name requested group name for setting up prefix for it.
     * @param prefix new prefix value.
     * @return true if prefix installed, false if group not exist.
     * @since 2.0.0-RC.1.
     */
    fun setGroupPrefix(name: String, prefix: String = String.empty): Boolean {
        if (!groupExist(name)) return false
        getGroupByName(name)!!.prefix = prefix
            .replace("&", "§")
            .replace("oliver heldens", "god", true)
        return true
    }

    /**
     * Rename target group.
     *
     * @param oldName target group to rename or old group name.
     * @param newName new group name.
     * @return true if group renamed, false if new group exist
     * or old group not exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun renameGroup(oldName: String, newName: String): Boolean {
        if (!groupExist(oldName) || groupExist(newName)) return false
        getGroupByName(oldName)!!.name = newName
        getGroupUsers(oldName).forEach { it.group = newName }
        getGroups().filter { oldName in it.inheritFrom }.forEach {
            it.inheritFrom.remove(oldName)
            it.inheritFrom.add(newName)
        }
        return true
    }

    /**
     * Adds new permission node to group.
     *
     * If group not exist then requested
     * group will be added.
     *
     * @param name group name to add permission.
     * @param node permission node to add.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun addGroupPermission(name: String, node: String) {
        if (!groupExist(name)) addGroup(Group(name, false, mutableListOf(name)))
        getGroupByName(name)!!.permissions.add(node)
    }

    /**
     * Removes permission node from existing group.
     *
     * @param name group name to remove permission.
     * @param node permission node to remove.
     * @return true if permission node was removed, false
     * if group not exist of permission node not exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun removeGroupPermission(name: String, node: String): Boolean {
        if (!groupExist(name)) return false
        if (!getGroupByName(name)!!.permissions.removeAll { perm -> perm == node }) {
            if (node in getGroupPermissions(name, true)) {
                addGroupPermission(name, "^$node")
                return true
            }
        } else return true
        return false
    }

    /**
     * Changes default group state to other group.
     *
     * @param newDefault new default group name.
     * @param oldDefault old default group name.
     * @return true if default group factor was changed, false
     * if new or old group not exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun defaultFactorMove(
        newDefault: String,
        oldDefault: String = getDefaultGroup().name
    ): Boolean {
        if (!groupExist(oldDefault) || !groupExist(newDefault)) return false
        getGroupByName(oldDefault)!!.isDefault = false
        getGroupByName(newDefault)!!.isDefault = true
        return true
    }

    /**
     * Adds new inherits groups to target group.
     *
     * @param name target group to add inheritances.
     * @param inheritances new inheritances group names.
     * @return false if target group not exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun addGroupInheritances(name: String, vararg inheritances: String): Boolean {
        if (!groupExist(name)) return false
        getGroupByName(name)!!.inheritFrom.addAll(inheritances)
        return true
    }

    /**
     * Removes existing inherits groups of target group.
     *
     * @param name target group to remove inheritances.
     * @param inheritances inheritances group names to remove.
     * @return false if target group not exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun removeGroupInheritances(name: String, vararg inheritances: String): Boolean {
        if (!groupExist(name)) return false
        getGroupByName(name)!!.inheritFrom.removeAll(inheritances)
        return true
    }

    /**
     * @param name requested user name to check.
     * @return true if user exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun userExist(name: String) = getUsers().filter { it.nickname == name }.count() > 0

    /**
     * Adds new user to user list.
     *
     * @param user [User] data class instance.
     * @return true if user added, false
     * if user already exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun addUser(user: User) =
        userExist(user.nickname).also { return if (!it) getUsers().add(user) else false }

    /**
     * Removes existing user from user list.
     *
     * @param name user name to remove.
     * @return true if user removed, false
     * if user not exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun removeUser(name: String) = getUsers().removeIf { it.nickname == name }

    /**
     * Sets new group to user.
     *
     * @param name target user name.
     * @param groupName new group name.
     * @return false if target group not exist
     * or user already has this group.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun setUserGroup(name: String, groupName: String): Boolean {
        if (!groupExist(name)) return false
        if (getUserGroup(name) == groupName) return false

        if (!userExist(name)) addUser(User(name, groupName, mutableListOf()))
        else getUserByName(name)!!.group = groupName

        return true
    }

    /**
     * Adds permission node to user.
     *
     * @param name target user name.
     * @param node new permission node.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun addUserPermission(name: String, node: String) {
        if (!userExist(name)) addUser(User(name, getDefaultGroup().name, mutableListOf(node)))
        else getUserByName(name)!!.permissions.add(node)
    }

    /**
     * Removes permission node from user.
     *
     * @param name target user name.
     * @param node permission node to remove.
     * @return true if permission node removed,
     * false if user not have this node or
     * user not exist.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun removeUserPermission(name: String, node: String) =
        if (!userExist(name)) false else getUserByName(name)!!.permissions.removeAll { it == node }
}
