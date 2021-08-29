package eu.rex2go.chat2go.customcomponent;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.placeholder.Placeholder;
import eu.rex2go.chat2go.placeholder.PlaceholderProcessor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CustomComponent {

    @Getter
    @Setter
    private String text;

    @Getter
    @Setter
    private String hoverText;

    @Getter
    @Setter
    private String suggestCommand;

    @Getter
    @Setter
    private String runCommand;

    @Getter
    @Setter
    private String openUrl;

    public CustomComponent(String text) {
        this.text = text;
    }

    public BaseComponent[] build(Player player, Placeholder... placeholders) {
        text = Chat2Go.parseColor(text);
        BaseComponent[] components = PlaceholderProcessor.process(text, player, false, placeholders);

        for (BaseComponent component : components) {
            if (hoverText != null && !hoverText.equals("")) {
                component.setHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new Text(PlaceholderProcessor.process(this.hoverText, player, false, placeholders))
                ));
            }

            if (suggestCommand != null && !suggestCommand.equals("")) {
                component.setClickEvent(new ClickEvent(
                        ClickEvent.Action.SUGGEST_COMMAND,
                        TextComponent.toPlainText(PlaceholderProcessor.process(this.suggestCommand, player, false, placeholders))
                ));
            }

            if (runCommand != null && !runCommand.equals("")) {
                component.setClickEvent(new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        TextComponent.toPlainText(PlaceholderProcessor.process(this.runCommand, player, false, placeholders))
                ));
            }

            if (openUrl != null && !openUrl.equals("")) {
                component.setClickEvent(new ClickEvent(
                        ClickEvent.Action.OPEN_URL,
                        TextComponent.toPlainText(PlaceholderProcessor.process(this.openUrl, player, false, placeholders))
                ));
            }
        }

        return components;
    }

}
