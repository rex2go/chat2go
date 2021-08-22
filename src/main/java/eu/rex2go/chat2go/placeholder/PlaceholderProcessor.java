package eu.rex2go.chat2go.placeholder;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.config.ChatConfig;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderProcessor {

    public static BaseComponent[] process(String format, Player processor, Placeholder... placeholders) {
        // check for placeholder api stuff
        if (Chat2Go.isPlaceholderInstalled()) {
            format = PlaceholderAPI.setPlaceholders(processor.getPlayer(), format);
            // translate colors of placeholder api stuff
            format = Chat2Go.parseColor(format);
        }

        // placeholder regex, e.g. { prefix }, {suffix}
        Pattern pattern = Pattern.compile("\\{( *)(.*?)( *)}");
        Matcher matcher = pattern.matcher(format);
        List<BaseComponent> componentList = new ArrayList<>();
        String remainder = "";

        while (matcher.find()) {
            String match = matcher.group(0);
            String leadingSpaces = matcher.group(1);
            String placeholderKey = matcher.group(2);
            String trailingSpaces = matcher.group(3);
            BaseComponent[] placeholderContent = new BaseComponent[0];

            for (Placeholder placeholder : placeholders) {
                String key = placeholder.getKey();
                BaseComponent[] value = placeholder.getReplacement();

                if (placeholderKey.equalsIgnoreCase(key)) {
                    placeholderContent = value;
                    break;
                }
            }

            // custom components
            if (ChatConfig.isCustomComponentsEnabled()
                    && placeholderContent.length == 0
                    && ChatConfig.getCustomComponents().containsKey(placeholderKey)) {
                placeholderContent = ChatConfig.getCustomComponents().get(placeholderKey).build(processor.getPlayer(), placeholders);
            }

            // split up into components, escape { because of regex errors
            String[] split = format.split(match.replace("{", "\\{"), 2);
            String before = split[0];
            remainder = split[1];

            BaseComponent[] beforeComponents = TextComponent.fromLegacyText(before);
            net.md_5.bungee.api.ChatColor chatColor = beforeComponents[beforeComponents.length - 1].getColorRaw();

            componentList.addAll(Arrays.asList(beforeComponents));

            // remaining unprocessed text
            format = remainder;

            // cleanup empty curly braces e.g. { }
            if (placeholderContent.length == 0
                    || (placeholderContent.length == 1
                    && ((TextComponent) placeholderContent[0]).getText().isEmpty())) {
                continue;
            }

            if (chatColor != null) {
                for (BaseComponent baseComponent : placeholderContent) {
                    if (baseComponent.getColorRaw() != null) {
                        chatColor = baseComponent.getColorRaw();
                    }

                    baseComponent.setColor(chatColor);
                }
            }

            BaseComponent[] components = new ComponentBuilder(leadingSpaces)
                    .append(placeholderContent)
                    .append(trailingSpaces)
                    .create();

            componentList.addAll(Arrays.asList(components));
        }

        componentList.addAll(Arrays.asList(TextComponent.fromLegacyText(remainder)));

        return componentList.toArray(new BaseComponent[0]);
    }

}
