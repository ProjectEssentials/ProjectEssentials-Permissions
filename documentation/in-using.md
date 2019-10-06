> ## Documentation for basically using PermissionsAPI.

## 1. For playing and running minecraft:

#### 1.1 Download Permissions API mod module.

Visit **Permissions API** repository on github, visit **releases** tab and download `.jar` files of latest _pre-release_ / release (**recommended**)

Releases page: https://github.com/ProjectEssentials/ProjectEssentials-Permissions/releases

#### 1.2 Install Permissions API modification.

The minecraft forge folder structure below will help you understand what is written below.

```
.
├── assets
├── config
├── libraries
├── mods (that's how it should be)
│   ├── Permissions-1.14.4-X.X.X.X.jar
│   └── Project Essentials Core-1.14.4-1.X.X.X.jar
└── ...
```

Place your mods and Permissions API mods according to the structure above.

#### 1.3 Verifying mod on the correct installation.

Run the game, check the number of mods, if the list of mods contains `Project Essentials Permissions` mod, then the mod has successfully passed the initialization of the modification.

After that, go into a single world, then try to write the `/essentials permissions` command, if you **get an error** that you do not have permissions, then the modification works as it should.

#### 1.4 Control permissions via minecraft commands.

We understand that there are lazy people who do not like to dig into files (lol, although it’s easier through a file), we made the commands for you:

```
/essentials permissions

- description: base command of permissions api module; just send you about message.

- permission: ess.perm
```

```
/essentials permissions save

- description: save permission configuration.

- permission: ess.perm.save
```

```
/essentials permissions reload

- description: reload permission configuration !!!without saving.

- permission: ess.perm.reload
```

```
/essentials permissions group <name> [set | remove] <node>

- description: remove or set permission for target group.

- permission: ess.perm.group
```

```
/essentials permissions user <name> [set | remove] <node>

OR

/essentials permissions user <name> set <group name>

- description: remove or set user permission node, or only set group for user.

- permission: ess.perm.user
```

## 2. For developing and developers:

### 2.1 Getting started with installing.

To get the Permissions API source for development and interactions with the rights of players, you need to get the dependencies and get the documentation to view it in your IDE.

Installation documentation is located in the readme file or just follow the link: https://github.com/ProjectEssentials/ProjectEssentials-Permissions#-install-using-gradle

### 2.2 Configuration.

Like many modifications, the Permissions API has its own modification; it stores groups of players, players, and other necessary data related to rights. The configuration is in `json` format, which is convenient for reading and understanding even the **most stupid person**.

#### 2.2.1 Configuration file Location.

Due to the different file structure on the server and the client of the minecraft, technically we must use different paths for the two sides.

The differences in the paths are primarily due to the different locations of the kernel of minecraft version, but for the average person, the paths will be exactly the same, i.e. like that.

    server: ./config/ProjectEssentials/permissions.json
    client: ./config/ProjectEssentials/permissions.json

#### 2.2.2 Configuration file structure.

##### 2.2.2.1 `groups` configuration section.

`groups` contains an array of groups, each element of the array (group) must have the `name`, `isDefault` and `permissions` properties, if there is no property, the default value will be used, but I strongly recommend writing all the properties to manual.

`name` is the name of the group; it should preferably not contain any characters other than letters; **This property must be required**.

`isDefault` only accepts a boolean value; if false, the group will not be set by default; if true, the group will be used as the default; **Note:** at least one group must be with the value `true`, if this is not so, I do not know what will happen, **I did not check**.

`permissions` is a string array, it just records the rights for a specific group. If you are not familiar with `json`, I strongly recommend that you see what it is. **Note:** if the `permissions` array has `"*"`, then this is equivalent to the **operator’s permissions or just gives all the permissions** that the PermissionsAPI controls.

##### 2.2.2.2 `users` configuration section.

`users` contains an array of users, each element of the array (user) must have the `nickname`, `group` and `permissions` properties, if there is no property, the default value will be used, but I strongly recommend writing all the properties to manual.

`nickname` is the nickname of the player; it should preferably not contain any characters other than letters; **This property must be required**.

`group` is the name of the group in which the player is entitled.

`permissions` is a list of rights that belong to a specific player, they can serve as a finer setting of rights; **Note:** if the player has a group, then the rights that are written by the player will be added to the rights that he received from the group. **In short: these are just additional rights for the player.**

##### 2.2.2.3 Just in case.

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

### 2.3 API usage.

I could not write this damn documentation at all and spend time on it, because it is so fucking understandable. But just in case, I will nevertheless sign here some trifles.

Let's start small?

#### 2.3.1 Functions.

```
PermissionsAPI.hasPermission

- accepts:
    - playerNickName - nickname of target player. (string)
    - node - permission node as string, e.g ess.weather (string)
    - isServerSender - needed for additional checking permissions. ((boolean) by default is false)

- return: true if user have permission, else return false. (boolean)
```

```
PermissionsAPI.getAllUserPermissions

- accepts:
    - playerNickName - nickname of target player. (string)

- return: list with all able user and group for user permissions. (list with string type)
```

```
PermissionsAPI.getDefaultGroup

- description: NOTE: if default group not exist then return group without permissions and without name.

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
    - playerNickName - nickname of target player. (string)

- return: instance of the class of the rights group the user belongs to. (Group class instance)
```

```
PermissionsAPI.getUserPermissions

- accepts:
    - playerNickName - nickname of target player. (string)

- return: list with all able user permissions. (list with string type)
```

```
PermissionsAPI.removeGroupPermission

- description: Remove permission node from group.

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

- description: Install \ Set new permission group for user.

- accepts:
    - playerNickName - nickname of target player. (string)
    - groupName - new user permission group. (string)
    OR
    - playerNickName - nickname of target player. (string)
    - groupInstance - new user permission group. (Group class instance)
```

```
PermissionsAPI.setUserPermissionNode

- description: Install \ Add new permission for user.

- accepts:
    - playerNickName - nickname of target player. (string)
    - node - new user permission. setting up for "*" (any) player. (string)
```

## These are all API methods, I think you understand that everything is very simple.

### For all questions, be sure to write issues!
