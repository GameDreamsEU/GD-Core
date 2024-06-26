package eu.gamedreams.gdcore.modules;

import dev.dejvokep.boostedyaml.YamlDocument;
import eu.gamedreams.gdcore.GD_Core;

import java.io.IOException;

public abstract class Module {

    protected String name;
    protected boolean isEnabled;
    protected GD_Core plugin;
    protected YamlDocument moduleConfiguration;

    public YamlDocument getModuleConfiguration() {
        return moduleConfiguration;
    }

    public void setModuleConfiguration(YamlDocument moduleConfiguration) {
        this.moduleConfiguration = moduleConfiguration;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public void setPlugin(GD_Core plugin) {
        this.plugin = plugin;
    }

    public void reload() throws IOException {
        moduleConfiguration.reload();
        setEnabled(moduleConfiguration.getBoolean("enabled"));
    }

    protected abstract void register();


}
