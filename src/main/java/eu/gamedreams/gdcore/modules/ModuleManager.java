package eu.gamedreams.gdcore.modules;

import dev.dejvokep.boostedyaml.YamlDocument;
import eu.gamedreams.gdcore.GD_Core;
import eu.gamedreams.gdcore.configuration.ConfigurationManager;
import eu.gamedreams.gdcore.modules.charfilter.CharFilterModule;
import eu.gamedreams.gdcore.modules.chat.ChatModule;
import eu.gamedreams.gdcore.modules.commandutils.CommandUtilsModule;
import eu.gamedreams.gdcore.modules.main.MainModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModuleManager {

    private final GD_Core plugin;
    private final ConfigurationManager configurationManager;
    private final List<Module> modules = new ArrayList<>();

    public Module getModule(String name) {
        return modules.stream()
                .filter(module -> module.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<Module> getModules() {
        return modules;
    }

    public ModuleManager(GD_Core plugin, ConfigurationManager configurationManager) {
        this.plugin = plugin;
        this.configurationManager = configurationManager;
    }

    public void loadModules() {
        loadModule(new MainModule(this), new ChatModule(), new CharFilterModule(), new CommandUtilsModule());
    }

    private void loadModule(Module... modules) {
        this.modules.addAll(Arrays.asList(modules));
    }

    public void registerModules() {
        for (Module module : modules) {
            module.setPlugin(plugin);
            YamlDocument moduleConfiguration;
            try {
                moduleConfiguration = configurationManager.initializeModuleFile(module);

                if (moduleConfiguration == null) throw new IOException();
            } catch (IOException e) {
                module.setEnabled(false);
                plugin.getLogger().severe("Configuration for module " + module.getName() + " couldn't be loaded. The module will be disabled.");
                return;
            }

            module.setModuleConfiguration(moduleConfiguration);
            module.setEnabled(moduleConfiguration.getBoolean("enabled"));
            module.register();
        }
    }

}
