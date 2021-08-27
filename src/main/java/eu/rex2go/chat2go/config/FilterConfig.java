package eu.rex2go.chat2go.config;

import eu.rex2go.chat2go.Chat2Go;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FilterConfig extends RexConfig {

    @Getter
    private static ArrayList<String> blockedWords;

    @Getter
    private static HashMap<String, String> replacements;

    public FilterConfig() {
        super(Chat2Go.getInstance(), "filter.yml", 1);
    }

    @Override
    public void load() {
        super.load();

        this.blockedWords = new ArrayList<>();
        this.replacements = new HashMap<>();

        List<String> blockedWords = getConfig().getStringList("blockedWords");

        for (String blockedWord : blockedWords) {
            this.blockedWords.add(blockedWord);
        }

        getConfig().getConfigurationSection("replacements").getKeys(false).forEach(id -> {
            String replacement = getConfig().getString("replacements." + id);

            replacements.put(id, replacement);
        });
    }

    public void save() {
        getConfig().set("blockedWords", blockedWords);

        for(String key : replacements.keySet()) {
            getConfig().set("replacements." + key, replacements.get(key));
        }

        try {
            getConfig().save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
