# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Since 2.0.0 versions change log same for all supported minecraft versions.

⚠ - Breaking backward compatibility with dependants.

## [Unreleased]

## [2.0.0-RC.1] - 2020-05-20

### Added
- Chinese simplified translation added by @KuroNoSeiHai.
- Compatibility with WorldEdit added. #25.
- `ModuleObject` implemented.
- Extended permissions checking for world events added.
- Permissions configuration added.
- `useSimplifiedWorldPermissions` setting added.
- `replaceWorldEditPermissionsHandler` setting added.
- Wrappers for forge and world edit added.
- Prefix configuration added.
- Command node for controlling group prefix added.
- `setGroupPrefix` method added to `PermissionsAPI`.
- Sub-commands for permissions node `save` and `reload` added.
- `debugMode` setting added.
- User removing command action implemented.
- User add permission command action implemented.
- User remove permission command action implemented.
- User listing permission command action implemented.
- User group set command action implemented.
- `getGroupPrefix` method added to `PermissionsAPI.kt`.
- `/perm user info <>` implemented.
- `/perm group list [page]` implemented.
- `/perm group set-default <>` implemented.
- `/perm group create <>` implemented.
- `/perm group remove <>` implemented. 
- `/perm group permissions <> add <>` implemented. 
- `/perm group permissions <> remove <>` implemented. 
- `/perm group permissions <> list [[] []]` implemented.
- `/perm group inherit <> add <>` implemented.
- `/perm group inherit <> remove <>` implemented. 
- `/perm group inherit <> list` implemented.
- Settings: `handle`: `BlockBreaking`, `BlockPlacing`, `FarmlandTrampling`, `ItemUsing` added.
- `setUserGroup`: group exist checking added.
- Documentation for `PermissionsAPI.kt` added.
- `groupPrefixTake` implemented.
- `enablePermissionsCommand` setting added. 
- `/perm group prefix <> <>` implemented.
- `/configure-permissions` command implemented.
- Inverting permission with `^` at node start added.

### Changed
- Permissions module fully rewritten for new CoreV2.
- `mods.toml` version property value changed to `${file.jarVersion}`.
- Permissions API fully re-written.
- build.gradle cleanup.
- Configuration model changed.
- Kotlin version updated to `1.3.72`.
- Kotlin serialization gradle plugin updated.
- `.gitignore` synced with core module `.gitignore`.
- Gradle wrapper distribution type changed to `bin`.
- `description` property in `pack` object in `pack.mcmeta` changed.
- Updater end-point file changed to `updatev2.json`.
- `credits` property value changed in `mods.toml`.
- `description` property value changed in `mods.toml`.
- `project_essentials_core` dependency `versionRange` changed to `[2,)`.
- By default now owner group does not inherit any permissions.
- Default permissions was converted to new.
- `bug_report.md` synced with core repository.
- `PULL_REQUEST_TEMPLATE.md` synced with core repository. 
- License now is `MIT` instead `LGPLv3`.

### Fixed
- `setUserGroup` fixed incorrect behavior when user not exist.
- `kapt` removed from plugins.
- Removed `cooldown` dependency.
- `https://jitpack.io` maven repository removed.
- description from `dokkaJar` task removed.
- Incorrect result after adding group fixed.
- Incorrect result after adding user fixed.
- Incorrect behavior in `removeUserPermission` fixed.
- Some confusing with getting user by name fixed.

### Removed
- Local assets removed.
- Links to not existing assets removed from `readme.md`.
- Directory paths for debugging removed.
- `klaxon_version` property removed from `gradle.properties`.

## [1.15.2-1.0.2] - 2020-03-21

### Fixed
- `java.lang.NoSuchMethodError` exception while server starting.

## [1.14.4-1.2.2] - 2020-03-21

### Fixed
- `java.lang.NoSuchMethodError` exception while server starting.

## [1.15.2-1.0.1] - 2020-03-13

### Added
- Server-side only safe localization.
- Default user `#server` with all permissions.

### Changed
- Improved internal code.
- Dependencies: Kotlin, KotlinxSerialization updated.
- Dokka and Forge API updated.

## [1.14.4-1.2.1] - 2020-03-13

### Added
- Server-side only safe localization.
- Default user `#server` with all permissions.

### Changed
- Improved internal code.
- Dependencies: Kotlin, KotlinxSerialization updated.
- Dokka and Forge API updated.

## [1.14.4-1.2.0] - 2020-02-08

### Changed
- Uses `cooldownAPIClassPath` from CoreAPI.
- modVersion in EntryPoint.kt updated.

### Fixed
- Inconsistent version number format with semver.

## [1.15.2-1.0.0] - 2020-02-07

### Added
- Initial release.

## [1.14.4-1.1.0.0] - 2020-01-18

### Added
- Resolved #20. (Improve default permission configuration.)
- Resolved #19. (Implement permissions for block break and block place events.)

### Changed
- Updated core module version.

### Fixed
- Not working permissions in group with inheritance.

## [1.14.4-1.0.1.0] - 2020-01-15

### Added
- Added compatibility with core module `1.0.3.2`.
- Added compatibility with cooldown module `1.0.2.0`.

### Changed
- Simplified code for creating directory.
- Usings optimal `JsonConfiguration` from core module.
- Updated gradle wrapper version to `5.6.4`.
- CHANGELOG.md renamed to changelog.md.
- Updated core module and cooldown module.
- Updated version number to `1.0.1.0`.

### Removed
- Comments from [gradle.properties](gradle.properties).
- Redundant information logging.

## [1.14.4-1.0.0.0] - 2020-01-12

### Added
- CurseForge link for `/perm about` command.
- Compatibility with future versions of `Cooldowns` module.
- Detecting opped players (now all opped players have all permissions).
- `ess_core_version` and `ess_cooldown_version` variables to [gradle.properties](./gradle.properties).
- German translation by [@BixelPitch](https://github.com/BixelPitch).
- Serbian translation by [@vr1e](https://github.com/vr1e).
- Pull request temple to this repository by [@huangyz0918](https://github.com/huangyz0918).
- Ability to use shorten permission nodes.
- Implemented permission groups inheritance permissions.
- This [CHANGELOG.md](./changelog.md) file.

### Changed
- [readme.md](./readme.md) changed information for developers.
- Simplified permissions command to `/permissions` or `/perm`.
- Simplified `JsonConfiguration` in [PermissionBase.kt](./src/main/kotlin/com/mairwunnx/projectessentials/permissions/permissions/PermissionBase.kt).
- Updated kotlin runtime version.
- Updated forge version, bumped kotlinx serialization.
- [build.gradle](./build.gradle) little file refactoring.
- PermissionData renamed to [PermissionModel.kt](./src/main/kotlin/com/mairwunnx/projectessentials/permissions/permissions/PermissionModel.kt). 

### Fixed
- Grammatical mistakes by [@Aircoookie](https://github.com/Aircoookie).
- Grammatical mistakes by [@abhiroopwastaken](https://github.com/abhiroopwastaken).
- Incorrect permission node for /permission reload command.

### Removed
- Redundant logger messages.

## [1.14.4-0.2.0.0] - 2019-10-12

### Changed
- Improved configuration logging after loading.
- Fixed incorrect permission removing from user.
- Fixed mod crash on loading permission data for users.
- Fixed incorrect command aliases registering.

## [1.14.4-0.1.1.0] - 2019-10-08

### Added
- Added core module as dependency.

### Changed
- Code cleanup

## [1.14.4-0.1.0.2] - 2019-10-06

### Changed
- Fixed some wrong meta information for mod.

## [1.14.4-0.1.0.1] - 2019-10-04

### Added 
- Included gradle wrapper (Basically for API).

## [1.14.4-0.1.0.0] - 2019-10-04

### Added
- Initial release of PermissionsAPI as Project Essentials part.
