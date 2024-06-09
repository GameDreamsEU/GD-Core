package eu.gamedreams.gdcore;

import eu.gamedreams.gdcore.api.GD_CoreAPI;
import eu.gamedreams.gdcore.configuration.ConfigurationManager;
import eu.gamedreams.gdcore.modules.ModuleManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class GD_Core extends JavaPlugin {

    public GD_CoreAPI gdCoreAPI;
    public ModuleManager moduleManager;

    @Override
    public void onEnable() {
        ConfigurationManager configurationManager = new ConfigurationManager(this);
        moduleManager = new ModuleManager(this, configurationManager);
        moduleManager.loadModules();
        moduleManager.registerModules();
        gdCoreAPI = new GD_CoreAPI(this);
    }
}
