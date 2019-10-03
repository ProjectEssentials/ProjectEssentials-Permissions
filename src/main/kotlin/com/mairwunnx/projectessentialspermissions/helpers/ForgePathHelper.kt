package com.mairwunnx.projectessentialspermissions.helpers

import com.mairwunnx.projectessentialspermissions.enums.ForgeRootPaths
import net.minecraft.client.Minecraft
import java.io.File

private val clientRootDir by lazy {
    Minecraft.getInstance().gameDir.absolutePath
}
private val serverRootDir by lazy {
    File(".").absolutePath
}

internal fun getRootPath(pathType: ForgeRootPaths): String {
    return when (pathType) {
        ForgeRootPaths.CLIENT -> clientRootDir
        ForgeRootPaths.SERVER -> serverRootDir
    }
}
