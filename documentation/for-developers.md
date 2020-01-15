>> ## Documentation for basic use of the PermissionsAPI.

### Getting Permissions API as dependency

```groovy
repositories {
    maven { url("https://jitpack.io") }
}

dependencies {
    compile(
        group: "com.github.projectessentials",
        name: "ProjectEssentials-Permissions",
        version: "v1.14.4-1.0.1.0"
    )
}
```

### Configuration

Like many modifications, the Permissions API has its own modification; it stores groups of players, players, and other necessary data related to rights. The configuration is in `json` format, which is convenient for reading and understanding.

### Configuration file Location

Due to the different file structure on the server and the client of the Minecraft, technically we must use different paths for the two sides.

The differences in the paths are primarily due to the different locations of the kernel of the Minecraft version, but for the average person, the paths will be exactly the same, i.e. like that.

    server: ./config/ProjectEssentials/permissions.json
    client: ./config/ProjectEssentials/permissions.json

### Configuration file structure

##### `groups` configuration section.

`groups` contains an array of groups, each element of the array (group) must have the `name`, `isDefault` and `permissions` properties, if there is no property, the default value will be used, but I strongly recommend adding all the properties manually.

`name` is the name of the group; it should preferably not contain any characters other than letters; **This property is required**.

`isDefault` only accepts a boolean value; if false, the group will not be set by default; if true, the group will be used as the default; **Note:** at least one group must have the value `true`, if this is not the case, I do not know what will happen, **I did not check**.

`permissions` is a string array, it just records the rights for a specific group. If you are not familiar with `json`, I strongly recommend that you get familiar with it. **Note:** If the `permissions` array has `"*"`, then this is equivalent to the **operator’s permissions or just gives all the permissions** that the PermissionsAPI controls.

##### `users` configuration section.

`users` contains an array of users, each element of the array (user) must have the `nickname`, `group` and `permissions` properties, if there is no property, the default value will be used, but I strongly recommend writing all the properties manually.

`nickname` is the nickname of the player; it should preferably not contain any characters other than letters; **This property must be required**.

`group` is the name of the group in which the player is entitled.

`permissions` is a list of rights that belong to a specific player, they can serve as a finer setting of rights; **Note:** If the player has a group, then the rights that are written by the player will be added to the rights that he received from the group. **In short: these are just additional rights for the player.**

##### Just in case.

If something goes according to the cunt, and your configuration flies, just delete the configuration or take the default configuration from here.

**Default configuration**:

```json
{
  "groups": [
    {
      "name": "default",
      "isDefault": true,
      "permissions": []
    },
    {
      "name": "owner",
      "isDefault": false,
      "permissions": ["*"]
    }
  ],
  "users": [
    {
      "nickname": "*",
      "group": "default",
      "permissions": []
    }
  ]
}
```

### API Functions / Properties

```
PermissionsAPI.hasPermission

- accepts:
    - playerNickName - nickname of target player. (string)
    - node - permission node as string, e.g ess.weather (string)
    - isServerSender - needed for additional checking permissions. ((boolean) by default is false)

- return: true if the user has permission, else return false. (boolean)
```

```
PermissionsAPI.getAllUserPermissions

- accepts:
    - playerNickName - the nickname of the target player. (string)

- return: list with all able users and groups for user permissions. (list with string type)
```

```
PermissionsAPI.getDefaultGroup

- description: NOTE: if default group does not exist then return group without permissions and without a name.

- return: default group in what defined configuration file. (Group class instance)
```

```
PermissionsAPI.getGroupPermissions

- accepts:
    - groupName - just group name. (string)
    OR
    - groupInstance - just group class instance. (Group class instance)

- return: list with all able group permissions. (list with string type)
```

```
PermissionsAPI.getUserGroup

- accepts:
    - playerNickName - the nickname of the target player. (string)

- return: an instance of the class of the rights group the user belongs to. (Group class instance)
```

```
PermissionsAPI.getUserPermissions

- accepts:
    - playerNickName - the nickname of the target player. (string)

- return: list with all able user permissions. (list with string type)
```

```
PermissionsAPI.removeGroupPermission

- description: Remove permission node from the group.

- accepts:
    - groupName - just group name. (string)
    - node - group permission. (string)
    OR
    - groupInstance - just group class instance. (Group class instance)
    - node - group permission. (string)
```

```
PermissionsAPI.removeUserPermission

- description: Remove permission node from user.

- accepts:
    - playerNickName - nickname of target player. (string)
    node - user permission. setting up for "*" (any) player. (string)
```

```
PermissionsAPI.setGroupPermissionNode

- description: Install \ Add new permission for group.

- accepts:
    - groupName - just group name. (string)
    - node - new group permission. (string)
    OR
    - groupInstance - just group class instance. (Group class instance)
    - node - new group permission. (string)
```

```
PermissionsAPI.setUserPermissionGroup

- description: Install \ Set a new permission group for the user.

- accepts:
    - playerNickName - nickname of target player. (string)
    - groupName - new user permission group. (string)
    OR
    - playerNickName - nickname of target player. (string)
    - groupInstance - new user permission group. (Group class instance)
```

```
PermissionsAPI.setUserPermissionNode

- description: Install \ Add new permission for the user.

- type: MutableList<String>
```

```
PermissionsAPI.oppedPlayers

- description: Contain all opped players, for advanced permission checking.

- accepts:
    - playerNickName - nickname of target player. (string)
    - node - new user permission. setting up for "*" (any) player. (string)
```

### Dependencies using by Core API.

```
    - kotlin-std lib version: 1.3.61
    - kotlinx serialization version: 0.14.0
    - forge version: 1.14.4-28.1.114
    - brigadier version: 1.0.17
    - target jvm version: 1.8
```

### If you have any questions or encounter a problem, be sure to open an issue!
