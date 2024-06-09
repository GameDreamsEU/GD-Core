package eu.gamedreams.gdcore.modules.charfilter.filter;

import eu.gamedreams.gdcore.modules.charfilter.utils.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.filter.AbstractFilter;

@Plugin(name = "LogFilter", category = "Core", elementType = "filter", printObject = true)
public class LogFilter extends AbstractFilter {

    private final String regex;
    private final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    private final Configuration config = ctx.getConfiguration();
    private final LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
    private boolean isEnabled;

    public LogFilter(String regex, boolean isEnabled) {
        this.regex = regex;
        this.isEnabled = isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public Result filter(LogEvent event) {
        if (!isEnabled) return Result.NEUTRAL;
        String message = event.getMessage().getFormattedMessage();
        if (Validator.isInvalidString(message, regex)) return Result.DENY;

        return Result.NEUTRAL;
    }

    public void registerFilter() {
        loggerConfig.addFilter(this);
        ctx.updateLoggers();
    }

}
