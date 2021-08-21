package eu.rex2go.chat2go.filter;

import eu.rex2go.chat2go.exception.FilterException;

import java.util.List;

public abstract class Filter {

    public abstract List<String> filter(String message) throws FilterException;

    protected boolean isWhitelisted(String phrase) {
        // TODO
        /*for (Pattern pattern : Config.getFilterWhitelist()) {
            if (pattern.matcher(phrase).find()) return true;
        }*/

        return false;
    }

    public String getMessageId() {
        return "chat.filter.blocked";
    }
}
