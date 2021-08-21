package eu.rex2go.chat2go.placeholder;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;

public class Placeholder {

    @Getter
    private final String key;

    @Getter
    private final BaseComponent[] replacement;

    public Placeholder(String key, BaseComponent... replacement) {
        this.key = key;
        this.replacement = replacement;
    }
}

