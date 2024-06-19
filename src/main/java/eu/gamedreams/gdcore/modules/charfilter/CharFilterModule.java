package eu.gamedreams.gdcore.modules.charfilter;

import eu.gamedreams.gdcore.modules.Module;
import eu.gamedreams.gdcore.modules.charfilter.filter.LogFilter;
import eu.gamedreams.gdcore.modules.charfilter.listeners.FilterListeners;

import java.io.IOException;

public class CharFilterModule extends Module {

    private LogFilter logFilter;

    public CharFilterModule() {
        this.isEnabled = false;
    }

    @Override
    public String getName() {
        return "charfilter";
    }

    @Override
    public void reload() throws IOException {
        super.reload();
        logFilter.setEnabled(moduleConfiguration.getBoolean("enabled"));
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (logFilter != null) logFilter.setEnabled(enabled);
    }

    @Override
    public void register() {
        logFilter = new LogFilter(moduleConfiguration.getString("regex"), isEnabled);
        logFilter.registerFilter();
        plugin.getServer().getPluginManager().registerEvents(new FilterListeners(this), plugin);
    }
}
