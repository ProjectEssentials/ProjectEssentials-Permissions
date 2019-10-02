package com.mairwunnx.projectessentialspermissions.helpers

import com.mairwunnx.projectessentialspermissions.MOD_NAME
import com.mairwunnx.projectessentialspermissions.enums.ForgeRootPaths
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import java.io.File

val CONFIG_FOLDER = root + File.separator + "config"
val MOD_CONFIG_FOLDER = CONFIG_FOLDER + File.separator + MOD_NAME.replace(" ", "")
val PERMISSIONS_CONFIG = MOD_CONFIG_FOLDER + File.separator + "permissions.json"

private val root: String
    get() {
        var rootPath = ""
        DistExecutor.runWhenOn(Dist.CLIENT) {
            Runnable {
                rootPath = getRootPath(ForgeRootPaths.CLIENT)
            }
        }
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER) {
            Runnable {
                rootPath = getRootPath(ForgeRootPaths.SERVER)
            }
        }
        return rootPath
    }
