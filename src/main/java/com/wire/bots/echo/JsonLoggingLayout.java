package com.wire.bots.echo;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Layout used on Wire production services in the ELK stack.
 */
public class JsonLoggingLayout extends LayoutBase<ILoggingEvent> {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC);

    @Override
    public String doLayout(ILoggingEvent event) {
        StringBuffer buffer = new StringBuffer(256);
        buffer.append("{");

        appendJson(buffer, "@timestamp", formatTime(event));

        Map<String, String> mdc = event.getMDCPropertyMap();
        if (mdc.containsKey("infra_request")) {
            appendJson(buffer, "infra_request", mdc.get("infra_request"));
        }

        if (mdc.containsKey("app_request")) {
            appendJson(buffer, "app_request", mdc.get("app_request"));
        }

        appendJson(buffer, "logger", event.getLoggerName());
        appendJson(buffer, "message", event.getFormattedMessage());
        appendJson(buffer, "level", event.getLevel().levelStr);
        appendJson(buffer, "thread_name", event.getThreadName());

        try {
            appendException(buffer, event.getThrowableProxy());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        buffer.append("}")
                .append(CoreConstants.LINE_SEPARATOR);

        return buffer.toString();
    }

    private void appendException(StringBuffer buffer, @Nullable IThrowableProxy proxy) throws JsonProcessingException {
        if (proxy == null) return;

        final Map<String, String> jsonMap = new LinkedHashMap<>();
        jsonMap.put("stacktrace", ThrowableProxyUtil.asString(proxy));
        jsonMap.put("message", proxy.getMessage());
        jsonMap.put("class", proxy.getClassName());

        final String exception = new ObjectMapper().writeValueAsString(jsonMap);
        buffer.append("\"exception\":").append(exception);
    }

    private void appendJson(StringBuffer buffer, String key, String value) {
        buffer.append("\"")
                .append(key)
                .append("\":\"")
                .append(value)
                .append("\"")
                .append(",");
    }

    private String formatTime(ILoggingEvent event) {
        return dateTimeFormatter.format(Instant.ofEpochMilli(event.getTimeStamp()));
    }
}
