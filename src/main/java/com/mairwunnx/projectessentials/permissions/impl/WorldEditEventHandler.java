package com.mairwunnx.projectessentials.permissions.impl;

import com.mairwunnx.projectessentials.core.api.v1.module.ModuleAPI;
import com.sk89q.worldedit.event.platform.PlatformReadyEvent;

@SuppressWarnings("unused")
public class WorldEditEventHandler {
    public static void onWorldEditConfigured(PlatformReadyEvent event) {
        ((ModuleObject) ModuleAPI.INSTANCE.getModuleByName("permissions")).replaceWorldEditPermissionHandler();
    }
}
