package eu.rex2go.chat2go.translator;

import eu.rex2go.chat2go.Chat2Go;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translator {

    private final Locale locale;

    private final HashMap<Locale, HashMap<String, String>> translations = new HashMap<>();

    public Translator(Locale locale) {
        this.locale = locale;

        for (Locale l : Locale.values()) {
            translations.put(l, new HashMap<>());
        }

        setupTranslations();
    }

    public String getTranslation(String key, String... args) {
        String str = translations.get(locale).get(key);
        if (str == null) str = translations.get(Locale.EN).get(key);

        if (str == null) {
            Chat2Go.getInstance().getLogger().log(Level.WARNING, "Missing " + locale.name() + " translation: " + key);
            return key;
        }

        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                String replace = "{" + i + "}";

                str = str.replace(replace, arg);
            }
        }

        str = Chat2Go.parseColor(str);

        return str;
    }

    private void addTranslation(Locale locale, String key, String translation) {
        translations.get(locale).put(key, translation);
    }

    private void setupTranslations() {
        addTranslation(Locale.EN, "command.no_permission", "&cYou are not permitted to execute this command.");

        addTranslation(Locale.EN, "command.chat.type_help", "&7Type \"/chat help\" for help");

        addTranslation(Locale.EN, "command.chat.reload.reloading", "&7Reloading {0}..");
        addTranslation(Locale.EN, "command.chat.reload.reloaded", "&a{0} has been reloaded.");

        addTranslation(Locale.EN, "command.chat.badword.list.delete_hover", "Delete \"{0}\"");
        addTranslation(Locale.EN, "command.chat.badword.list.no_bad_words", "&cNo bad words found. &7You can add one using /chat badword add <word>.");
        addTranslation(Locale.EN, "command.chat.badword.add.success", "&aSuccessfully &7added &c\"{0}\" &7to bad words.");
        addTranslation(Locale.EN, "command.chat.badword.add.error", "&cCould not add \"{0}\" to bad words! Is it a duplicate?");
        addTranslation(Locale.EN, "command.chat.badword.remove.success", "&aSuccessfully &7removed &c\"{0}\" &7from bad words.");
        addTranslation(Locale.EN, "command.chat.badword.remove.error", "&cCould not remove \"{0}\" from bad words! Is it in the list?");

        addTranslation(Locale.EN, "command.message.message_yourself", "&cYou can't message yourself.");
        addTranslation(Locale.EN, "command.message.no_player_to_reply_to", "&cNo player to reply to.");

        addTranslation(Locale.EN, "chat.cooldown", "&cChat is in slow mode. Please wait {0} seconds before sending a message.");
        addTranslation(Locale.EN, "chat.disabled", "&7Chat is disabled.");

        addTranslation(Locale.EN, "chat.antispam.repeating", "&cYou are repeating yourself.");
        addTranslation(Locale.EN, "chat.antispam.suspicious", "&cYour message seems suspicious.");
        addTranslation(Locale.EN, "chat.antispam.too_fast", "&cYou are chatting too fast.");

        addTranslation(Locale.EN, "command.mute.muted", "&7You muted &f{0} &7for &f{1}&7.");
        addTranslation(Locale.EN, "command.mute.muted_reason", "&7You muted &f{0} &7for &f{1}&7. &cReason: {2}");

        addTranslation(Locale.EN, "chat.you_have_been_muted", "&eYou have been muted for {0}.");
        addTranslation(Locale.EN, "chat.you_have_been_muted_reason", "&eYou have been muted for {0}: {1}");

        addTranslation(Locale.EN, "bad_words", "Bad words");
        addTranslation(Locale.EN, "delete", "delete");

        addTranslation(Locale.EN, "player", "player");
        addTranslation(Locale.EN, "time", "time");
        addTranslation(Locale.EN, "reason", "reason");

        addTranslation(Locale.EN, "pagination", "&7Page &b{0} &7of &b{1}");
        addTranslation(Locale.EN, "next_page", "Next page");
        addTranslation(Locale.EN, "previous_page", "Previous page");

        addTranslation(Locale.EN, "chat.filter.blocked", "&cYour message containing \"{0}\" has been blocked.");
    }

    public void sendMessage(CommandSender sender, String message) {
        String regex = "\\{(.*?)}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String full = matcher.group();
            String expression = matcher.group(1);
            String[] args = null;

            if (expression.contains("(")) {
                String[] parts = expression.split("\\(");
                String pre = parts[0];
                String post = parts[1];

                expression = pre;
                args = extractArgs("(" + post);
            }

            String translated = getTranslation(expression, args);
            message = message.replace(full, translated);
        }

        sender.sendMessage(message);
    }

    private String[] extractArgs(String expression) {
        ArrayList<String> parametersList = new ArrayList<>();

        String regex = "\\((.*?)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(expression);

        while (matcher.find()) {
            String parameters = matcher.group(1);
            if (parameters.contains(",")) {
                parametersList.addAll(Arrays.asList(parameters.split(",")));
            } else {
                parametersList.add(parameters);
            }
        }

        return parametersList.isEmpty() ? null : parametersList.toArray(new String[0]);
    }


}
