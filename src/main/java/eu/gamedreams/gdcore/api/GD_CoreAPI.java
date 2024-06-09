package eu.gamedreams.gdcore.api;

import eu.gamedreams.gdcore.GD_Core;
import eu.gamedreams.gdcore.modules.ModuleManager;

public class GD_CoreAPI {

    private final GD_Core plugin;

    public GD_CoreAPI(GD_Core plugin) {
        this.plugin = plugin;
    }

    public ModuleManager getModuleManager() {
        return plugin.moduleManager;
    }

}
