package eu.rex2go.chat2go.config;

import eu.rex2go.chat2go.Chat2Go;

import java.util.Map;

public class MessageConfig extends RexConfig {

    public MessageConfig() {
        super(Chat2Go.getInstance(), "messages.yml", 4);
    }

    @Override
    public void load() {
        super.load();

        Map<String, Object> values = getConfig().getValues(true);

        Chat2Go.getTranslator().getTranslations().clear();

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String) {
                Chat2Go.getTranslator().addTranslation(key, (String) value);
            }
        }
    }
}
