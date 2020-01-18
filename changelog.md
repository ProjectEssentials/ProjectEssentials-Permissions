# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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








  
