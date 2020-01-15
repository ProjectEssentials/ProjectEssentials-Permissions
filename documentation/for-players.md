> ## Installation instructions.

For start the modification, you need installed Forge, it is desirable that the version matches the supported versions. You can download Forge from the [link](https://files.minecraftforge.net/maven/net/minecraftforge/forge/index_1.14.4.html).
Move the downloaded mod to the `mods` folder (installation example below).

Also do not forget to install dependencies, only two types of dependencies
    - mandatory (game will not start without a mod)
    - recommended (without a mod, game can start, but I recommend using it)

Downloads: [Cooldown](https://github.com/ProjectEssentials/ProjectEssentials-Cooldown) · [Core](https://github.com/ProjectEssentials/ProjectEssentials-Core)

```
.
├── assets
├── config
├── libraries
├── mods (that's how it should be)
│   ├── Project Essentials Core-MOD-1.14.4-1.X.X.X.jar (mandatory)
│   ├── Project Essentials Cooldown-1.14.4-1.X.X.X.jar (recommended)
│   └── Project Essentials Permissions-1.14.4-1.X.X.X.jar
└── ...
```

Now try to start the game, go to the `mods` tab, if this modification is displayed in the `mods` tab, then the mod has been successfully installed.

### Control cooldowns via commands

`Note: /permissions command aliases: permission, perm.`

```
/permissions about

- description: just send you about the message.

- permission: ess.perm.about
```

```
/permissions save

- description: save permission configuration.

- permission: ess.perm.save
```

```
/permissions reload

- description: reload permission configuration !!!without saving.

- permission: ess.perm.reload
```

```
/permissions group <name> [set | remove] <node>

- description: remove or set permission for the target group.

- permission: ess.perm.group
```

```
/permissions user <name> [set | remove] <node>

OR

/permissions user <name> set <group name>

- description: remove or set user permission node or only set group for the user.

- permission: ess.perm.user
```

### If you have any questions or encounter a problem, be sure to open an [issue](https://github.com/ProjectEssentials/ProjectEssentials-Permissions/issues/new/choose)!
