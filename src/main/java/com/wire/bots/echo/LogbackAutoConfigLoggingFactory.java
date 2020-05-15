package com.wire.bots.echo;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.dropwizard.logging.LoggingFactory;
import io.dropwizard.logging.LoggingUtil;

/**
 * https://github.com/dropwizard/dropwizard/issues/1567
 * Override getLoggingFactory for your configuration
 */
public class LogbackAutoConfigLoggingFactory implements LoggingFactory {

    @JsonIgnore
    private final LoggerContext loggerContext;
    @JsonIgnore
    private final ContextInitializer contextInitializer;

    public LogbackAutoConfigLoggingFactory() {
        this.loggerContext = LoggingUtil.getLoggerContext();
        this.contextInitializer = new ContextInitializer(loggerContext);
    }

    @Override
    public void configure(MetricRegistry metricRegistry, String name) {
        try {
            contextInitializer.autoConfig();
        } catch (JoranException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        loggerContext.stop();
    }

    @Override
    public void reset() {
        loggerContext.reset();
    }
}
