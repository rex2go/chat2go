package eu.rex2go.chat2go.filter;

import eu.rex2go.chat2go.config.ChatConfig;
import eu.rex2go.chat2go.exception.FilterException;

import java.util.List;
import java.util.regex.Pattern;

public abstract class Filter {

    public abstract List<String> filter(String message) throws FilterException;

    protected boolean isWhitelisted(String phrase) {
        for (Pattern pattern : ChatConfig.getFilterWhitelist()) {
            if (pattern.matcher(phrase).find()) return true;
        }

        return false;
    }

    public String getMessageId() {
        return "chat.filter.blocked";
    }
}
