package eu.rex2go.chat2go.chat;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.config.ChatConfig;
import eu.rex2go.chat2go.exception.FilterException;
import eu.rex2go.chat2go.filter.AdvertisementFilter;
import eu.rex2go.chat2go.filter.BadWordFilter;
import eu.rex2go.chat2go.filter.Filter;

import java.util.ArrayList;
import java.util.List;

public class ChatManager {

    private List<Filter> filters = new ArrayList<>();

    public ChatManager() {
        filters.add(new BadWordFilter());
        filters.add(new AdvertisementFilter());
    }

    public String filter(String message) throws FilterException {
        for (Filter filter : filters) {
            List<String> matches = filter.filter(message);

            if (!matches.isEmpty()) {
                if (ChatConfig.getFilterFilterMode().equalsIgnoreCase("CENSOR")) {
                    message = censor(message, matches);
                    continue;
                }

                throw new FilterException(Chat2Go.getTranslator().getTranslation(filter.getMessageId(), matches.get(0)));
            }
        }

        return message;
    }

    public String censor(String message, List<String> censors) {
        for (String censor : censors) {
            if (message.toLowerCase().contains(censor)) {
                for (String msg : message.split(" ")) {
                    if (msg.toLowerCase().contains(censor)) {
                        StringBuilder stringBuilder = new StringBuilder();

                        for (int i = 0; i < msg.length(); i++) {
                            stringBuilder.append("*");
                        }

                        message = message.replaceAll(msg, stringBuilder.toString());
                    }
                }
            }
        }

        return message;
    }
}
