package eu.gamedreams.gdcore.modules.main;

import eu.gamedreams.gdcore.modules.Module;
import eu.gamedreams.gdcore.modules.ModuleManager;
import eu.gamedreams.gdcore.modules.main.commands.GDCoreCommand;

import java.util.Objects;

public class MainModule extends Module {

    private final ModuleManager moduleManager;

    public MainModule(ModuleManager moduleManager) {
        this.isEnabled = false;
        this.moduleManager = moduleManager;
    }

    @Override
    public String getName() {
        return "main";
    }

    @Override
    public void register() {
        Objects.requireNonNull(plugin.getCommand("gd-core")).setExecutor(new GDCoreCommand(this, plugin, moduleManager));
    }
}
